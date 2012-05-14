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

import com.thoughtworks.selenium.InternalSelenseTestBase;

import org.junit.Test;

import java.util.regex.Pattern;

public class TestSelectWindowTitle extends InternalSelenseTestBase {
  @Test
  public void testSelectWindowTitle() throws Exception {
    selenium.open("../tests/html/test_select_window.html");
    selenium.click("popupPage");
    selenium.waitForPopUp("myPopupWindow", "5000");
    selenium.selectWindow("Select Window Popup");
    verifyTrue(selenium.getLocation().matches(
        "^[\\s\\S]*/tests/html/test_select_window_popup\\.html$"));
    verifyEquals(selenium.getTitle(), "Select Window Popup");
    verifyTrue(join(selenium.getAllWindowNames(), ',').matches("^[\\s\\S]*,[\\s\\S]*$"));
    verifyTrue(Pattern.compile("myPopupWindow").matcher(join(selenium.getAllWindowNames(), ','))
        .find());
    selenium.close();
    selenium.selectWindow("null");
    verifyTrue(selenium.getLocation().matches("^[\\s\\S]*/tests/html/test_select_window\\.html$"));
    selenium.click("popupPage");
    selenium.waitForPopUp("myPopupWindow", "5000");
    selenium.selectWindow("myPopupWindow");
    verifyTrue(selenium.getLocation().matches(
        "^[\\s\\S]*/tests/html/test_select_window_popup\\.html$"));
    selenium.close();
    selenium.selectWindow("null");
    selenium.click("popupAnonymous");
    selenium.waitForPopUp("anonymouspopup", "5000");
    selenium.selectWindow("anonymouspopup");
    verifyTrue(selenium.getLocation().matches(
        "^[\\s\\S]*/tests/html/test_select_window_popup\\.html$"));
    selenium.click("closePage");
    selenium.selectWindow("null");
    selenium.click("popupAnonymous");
    selenium.waitForPopUp("anonymouspopup", "5000");
    selenium.selectWindow("anonymouspopup");
    verifyTrue(selenium.getLocation().matches(
        "^[\\s\\S]*/tests/html/test_select_window_popup\\.html$"));
    selenium.click("closePage2");
  }
}
