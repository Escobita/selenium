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

public class TestComments extends InternalSelenseTestBase {
  @Test
  public void testComments() throws Exception {
    selenium.open("../tests/html/test_verifications.html?foo=bar");
    verifyTrue(selenium.getLocation().matches(
        "^[\\s\\S]*/tests/html/test_verifications\\.html[\\s\\S]*$"));
    verifyEquals(selenium.getValue("theText"), "the text value");
    verifyEquals(selenium.getValue("theHidden"), "the hidden value");
    verifyEquals(selenium.getText("theSpan"), "this is the span");
  }
}
