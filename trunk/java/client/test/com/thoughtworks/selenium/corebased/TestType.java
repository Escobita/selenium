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


package com.thoughtworks.selenium.corebased;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.thoughtworks.selenium.InternalSelenseTestBase;

import org.junit.Test;
import org.openqa.selenium.internal.WrapsDriver;

import java.io.File;

public class TestType extends InternalSelenseTestBase {
  @Test
  public void testType() throws Exception {
    selenium.open("../tests/html/test_type_page1.html");
    verifyEquals(selenium.getValue("username"), "");
    selenium.shiftKeyDown();
    selenium.type("username", "x");
    verifyEquals(selenium.getValue("username"), "X");
    selenium.shiftKeyUp();
    selenium.type("username", "TestUserWithLongName");
    verifyEquals(selenium.getValue("username"), "TestUserWi");
    selenium.type("username", "TestUser");
    verifyEquals(selenium.getValue("username"), "TestUser");
    verifyEquals(selenium.getValue("password"), "");
    selenium.type("password", "testUserPasswordIsVeryLong");
    verifyEquals(selenium.getValue("password"), "testUserPasswordIsVe");
    selenium.type("password", "testUserPassword");
    verifyEquals(selenium.getValue("password"), "testUserPassword");
    if (isAbleToUpdateFileElements()) {
      File tempFile = File.createTempFile("example", "upload");
      tempFile.deleteOnExit();
      Files.write("I like cheese", tempFile, Charsets.UTF_8);
      selenium.type("file", tempFile.getAbsolutePath());
      selenium.click("submitButton");
      selenium.waitForPageToLoad("30000");
      verifyTrue(selenium.isTextPresent("Welcome, TestUser!"));
    }
  }

  private boolean isAbleToUpdateFileElements() {
    String browser = System.getProperty("selenium.browser", runtimeBrowserString());

    return selenium instanceof WrapsDriver ||
           "*firefox".equals(browser) || "*firefoxchrome".equals(browser);
  }
}
