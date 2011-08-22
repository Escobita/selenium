/*
Copyright 2007-2011 WebDriver committers
Copyright 2007-2011 Google Inc.

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

package org.openqa.selenium.interactions.touch;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.Ignore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;

import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.FIREFOX;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.OPERA;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

/**
 * Tests the basic scroll operations.
 */
public class TouchScrollTest extends AbstractDriverTestCase {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  public void testCanScrollWithWebElement() {
    // Load a page before loading the long page so that we're sure that the
    // second load happens and that the page isn't already scrolled.
    driver.get(pages.formPage);
    driver.get(pages.touchScrollPage);
    WebElement toScrollDown = driver.findElement(By.id("image_reference"));
    Action scrollDown = getBuilder(driver).scroll(toScrollDown, 0, -150).build();
    scrollDown.perform();
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  public void testCanScrollWithXAndYOffsetsOnly() {
    // Load a page before loading the long page so that we're sure that the
    // second load happens and that the page isn't already scrolled.
    driver.get(pages.formPage);
    driver.get(pages.touchScrollPage);
    WebElement toScrollDown = driver.findElement(By.id("image_reference"));
    Action scrollDown = getBuilder(driver).scroll(0, -150).build();
    scrollDown.perform();
  }
}
