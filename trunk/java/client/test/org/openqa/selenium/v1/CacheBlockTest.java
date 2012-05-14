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


package org.openqa.selenium.v1;

import static org.testng.Assert.assertNotNull;

import com.thoughtworks.selenium.InternalSelenseTestBase;

import org.testng.annotations.Test;

public class CacheBlockTest extends InternalSelenseTestBase {

  @Test(dataProvider = "system-properties")
  public void testCacheBlock() throws Exception {
    selenium.open("/selenium-server/cachedContentTest");
    String text = selenium.getBodyText();
    assertNotNull("body text should not be null", text);
    selenium.stop();

    selenium.start();
    selenium.open("/selenium-server/cachedContentTest");
    String text2 = selenium.getBodyText();
    assertFalse("content was cached: " + text, text.equals(text2));
  }
}
