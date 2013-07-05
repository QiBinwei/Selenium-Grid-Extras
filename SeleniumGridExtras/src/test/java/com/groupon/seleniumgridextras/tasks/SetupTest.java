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


package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.groupon.seleniumgridextras.JsonWrapper;
import com.groupon.seleniumgridextras.RuntimeConfig;
import com.groupon.seleniumgridextras.WriteDefaultConfigs;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import com.groupon.seleniumgridextras.tasks.Setup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.assertEquals;


public class SetupTest {

  public ExecuteOSTask task;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfig("setup_test.json");
    WriteDefaultConfigs.writeConfig(RuntimeConfig.getConfigFile(), false);
    RuntimeConfig.loadConfig();

    task = new Setup();
    Boolean initilized = task.initialize();
  }

  @After
  public void tearDown() throws Exception {
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
  }

  @Test
  public void testExecute() {
    if (!java.awt.GraphicsEnvironment.isHeadless()) {
      JsonObject result = task.execute();
      Long exitCode = new Long(0);
      JsonArray expectedClasses = new JsonArray();
      expectedClasses.add(new JsonPrimitive("KillAllIE"));
      expectedClasses.add(new JsonPrimitive("MoveMouse"));

      assertEquals((Object) exitCode, result.get("exit_code").getAsLong());
      assertEquals(expectedClasses, result.get("classes_to_execute"));
    }
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/setup", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Calls several pre-defined tasks to act as setup before build",
                 task.getDescription());
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    if (!java.awt.GraphicsEnvironment.isHeadless()) {

      assertEquals(
          new JsonParser().parse("{\"exit_code\":0,\"results\":[\"\"],\"error\":[],\"classes_to_execute\":[\"KillAllIE\",\"MoveMouse\"],\"out\":[]}"),
          task.getJsonResponse().getJson());

      assertEquals("List of full canonical classes to execute on Setup",
                   task.getJsonResponse().getKeyDescriptions().get("classes_to_execute").getAsString());

      assertEquals("Hash object of tasks ran and their results",
                   task.getJsonResponse().getKeyDescriptions().get("results").getAsString());

      assertEquals(5, task.getResponseDescription().entrySet().size());
    }
  }

  @Test
  public void testGetAcceptedParams() throws Exception {
    assertEquals(0, task.getAcceptedParams().keySet().size());
  }


}
