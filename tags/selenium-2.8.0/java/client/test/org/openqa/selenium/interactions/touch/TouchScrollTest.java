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

import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.FIREFOX;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.OPERA;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.Ignore;
import org.openqa.selenium.NeedsFreshDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;

/**
 * Tests the basic scroll operations on touch enabled devices..
 */
public class TouchScrollTest extends AbstractDriverTestCase {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanScrollVerticallyFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int y = link.getLocation().y;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 4200);

    WebElement toScroll = driver.findElement(By.id("imagestart"));
    Action scroll = getBuilder(driver).scroll(toScroll, 0, -800).build();
    scroll.perform();

    y = link.getLocation().y;
    // After scrolling, the location of the element should change accordingly.
    assertTrue(y < 3500);
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanScrollHorizontallyFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link1"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 2000);

    WebElement toScroll = driver.findElement(By.id("imagestart"));
    Action scroll = getBuilder(driver).scroll(toScroll, -400, 0).build();
    scroll.perform();

    x = link.getLocation().x;
    // After scrolling, the location of the element should change accordingly.
    assertTrue(x < 2000);
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanScrollVertically() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int y = link.getLocation().y;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 4200);

    Action scrollDown = getBuilder(driver).scroll(0, 800).build();
    scrollDown.perform();

    y = link.getLocation().y;
    // After scrolling, the location of the element should change accordingly.
    assertTrue(y < 3500);
  }

  @Ignore(value = {CHROME, FIREFOX, OPERA, HTMLUNIT, IE, IPHONE, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanScrollHorizontally() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link1"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 2000);

    Action scrollDown = getBuilder(driver).scroll(400, 0).build();
    scrollDown.perform();

    x = link.getLocation().y;
    // After scrolling, the location of the element should change accordingly.
    assertTrue(x < 1800);
  }
}
