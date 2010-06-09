/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium;

import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.REMOTE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

public class PartialLinkTextMatchTest extends AbstractDriverTestCase {

  @Ignore({IE, REMOTE, SELENESE})
  @Test public void LinkWithFormattingTags() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res =
        elem.findElement(By.partialLinkText("link with formatting tags"));
    assertNotNull(res);
    assertEquals("link with formatting tags", res.getText());
  }

  @Ignore({IE, REMOTE, SELENESE})
  @Test public void LinkWithLeadingSpaces() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res = elem.findElement(By.partialLinkText("link with leading space"));
    assertNotNull(res);
    assertEquals("link with leading space", res.getText());
  }

  @Ignore({IE, REMOTE, SELENESE})
  @Test public void LinkWithTrailingSpace() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res =
        elem.findElement(By.partialLinkText("link with trailing space"));
    assertNotNull(res);
    assertEquals("link with trailing space", res.getText());
  }

  @Ignore({IE, REMOTE, SELENESE})
  @Test public void findMultipleElements() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    Collection<WebElement> elements =
        elem.findElements(By.partialLinkText("link"));
    assertNotNull(elements);
    assertEquals(3, elements.size());
  }
  
  @Test public void CanGetLinkByLinkTestIgnoringTrailingWhitespace() {
    driver.get(pages.simpleTestPage);
    WebElement link = null;
    try {
      link = driver.findElement(By.linkText("link with trailing space"));
    } catch (NoSuchElementException e) {
      fail("Should have found link");
    }
    assertEquals("linkWithTrailingSpace", link.getAttribute("id"));
  }
}
