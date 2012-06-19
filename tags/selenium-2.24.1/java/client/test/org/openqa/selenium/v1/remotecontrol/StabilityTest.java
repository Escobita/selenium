/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.openqa.selenium.v1.remotecontrol;

import com.thoughtworks.selenium.DefaultSelenium;

import org.junit.Test;

/**
 * Regression test suite for stability problems discovered in Selenium Remote Control
 * 
 * You need to have a Remote-Control server running on 4444 in a separate process before running
 * this test
 * 
 */
public class StabilityTest {

  @Test
  public void retrievelastRemoteControlLogsDoesNotTriggerOutOfMemoryErrors() {
    final DefaultSelenium seleniumDriver;

    seleniumDriver = new DefaultSelenium("localhost", 4444, "*chrome", "http://localhost:4444");
    for (int i = 1; i < 100000; i++) {
      seleniumDriver.retrieveLastRemoteControlLogs();
    }
  }

}
