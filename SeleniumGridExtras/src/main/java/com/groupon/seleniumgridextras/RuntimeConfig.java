/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */

package com.groupon.seleniumgridextras;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class RuntimeConfig {

  private static JsonObject config;
  private static String configFile = "selenium_grid_extras_config.json";

  public static JsonObject getConfig() {
    return config;
  }

  public static void setConfig(String file) {
    configFile = file;
  }

  public static String getConfigFile() {
    return configFile;
  }

  public static void loadConfig() {

    String configString = readConfigFile(configFile);

    if (configString != "") {
      setFullConfig(new JsonParser().parse(configString).getAsJsonObject());
    }
  }

  public static JsonArray getSetupModules() {
    return config.get("setup").getAsJsonArray();
  }

  public static String getExposedDirectory() {
    return config.get("expose_directory").toString();
  }

  public static JsonArray getTeardownModules() {
    return config.get("teardown").getAsJsonArray();
  }

  public static JsonArray getActivatedModules() {
    return config.get("activated_modules").getAsJsonArray();
  }

  public static JsonObject getDeactivatedModules() {
    return config.get("deactivated_modules").getAsJsonObject();
  }

  public static Boolean checkIfModuleEnabled(String module) {
    return getActivatedModules().getAsJsonObject().has(module);
  }

  public static JsonElement getWebdriverConfig() {
    return config.get("webdriver");
  }

  public static JsonObject getGridConfig() {
    return (JsonObject) config.get("grid");
  }

  public static Boolean autoStartHub() {
    JsonElement grid = getGridConfig();

    String value = grid.getAsJsonObject().get("auto_start_hub").getAsString();

    if (value.equals("1")) {
      return true;
    } else {
      return false;
    }
  }

  public static Boolean autoStartNode() {
    JsonElement grid = getGridConfig();

    String value = grid.getAsJsonObject().get("auto_start_node").getAsString();

    if (value.equals("1")) {
      return true;
    } else {
      return false;
    }
  }

  public static String getSeleniumGridExtrasJarFile() {
    return SeleniumGridExtras.class.getProtectionDomain().getCodeSource().getLocation().getPath();
  }

  public static String getCurrentHostIP() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostAddress();
    } catch (UnknownHostException error) {
      System.out.println(error);
      return "";
    }
  }

  public static String getSeleniungGridExtrasHomePath() {
    String path = getSeleniumGridExtrasJarFile();
    path = path.replaceAll("[\\w-\\d\\.]*\\.jar", "");

    if (OSChecker.isWindows()) {
      path = OSChecker.toWindowsPath(path);
    }

    return path;
  }

  public static String getWebdriverParentDir() {
    return RuntimeConfig.getWebdriverConfig().getAsJsonObject().get("directory").getAsString();
  }

  public static String getWebdriverVersion() {
    return RuntimeConfig.getWebdriverConfig().getAsJsonObject().get("version").getAsString();
  }

  public static void setWebdriverVersion(String newVersion) {
    getWebdriverConfig().getAsJsonObject().addProperty("version", newVersion);
  }

  public static void saveConfigToFile() throws IOException {
    String jsonText = new Gson().toJson(config);
    FileUtils.writeStringToFile(new File(configFile), jsonText);
  }

  private static void setFullConfig(JsonObject configHash) {
    if (!configHash.isJsonNull()) {
      config = configHash;
    }
  }

  private static String readConfigFile(String filePath) {
    String returnString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }
    } catch (FileNotFoundException error) {
      System.out.println("File " + filePath + " does not exist, going to use default configs");
      WriteDefaultConfigs.writeConfig(filePath);
      return readConfigFile(filePath);

    } catch (IOException error) {
      System.out.println("Error reading" + filePath + ". Going with default configs");
      WriteDefaultConfigs.writeConfig(filePath);
      return readConfigFile(filePath);
    }

    return returnString;
  }

}
