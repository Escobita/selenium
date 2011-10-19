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

import static org.openqa.selenium.Ignore.Driver.ANDROID;
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
 * Tests the basic flick operations on touch enabled devices.
 */
public class TouchFlickTest extends AbstractDriverTestCase {
  
  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickHorizontallyFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    WebElement link = driver.findElement(By.id("link1"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 2000);

    Action flick = getBuilder(driver).flick(toFlick, -400, 0, FlickAction.SPEED_NORMAL)
        .build();
    flick.perform();
    
    x = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Got: " + x, x < 2000);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickHorizontallyFastFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    WebElement link = driver.findElement(By.id("link2"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 3500);

    Action flick = getBuilder(driver).flick(toFlick, -400, 0, FlickAction.SPEED_FAST)
        .build();
    flick.perform();
    x = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected x < 3500, but got: " + x, x < 3500);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickHorizontally() {
    driver.get(pages.clicksPage);
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link1"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 2000);

    Action flick = getBuilder(driver).flick(750, 0).build();
    flick.perform();
    x = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Got: " + x, x < 1500);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE, ANDROID},
      reason = "TouchScreen operations not supported, Android flick's can result in different "
          + "offsets")
  @NeedsFreshDriver
  public void testCanFlickHorizontallyFast() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link2"));
    int x = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    assertTrue(x > 3500);

    Action flick = getBuilder(driver).flick(1500, 0).build();
    flick.perform();
    x = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Got: " + x, x < 3000);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickVerticallyFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int y = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 4200);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    Action flick = getBuilder(driver).flick(toFlick, 0, -600, FlickAction.SPEED_NORMAL)
        .build();
    flick.perform();
    y = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < 4000, but got: " + y, y < 4000);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickVerticallyFastFromWebElement() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link4"));
    int y = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 8700);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    Action flick = getBuilder(driver).flick(toFlick, 0, -600, FlickAction.SPEED_FAST)
        .build();
    flick.perform();
    y = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < 8700, but got: " + y, y < 8700);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickVertically() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int y = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 4200);

    Action flick = getBuilder(driver).flick(0, 750).build();
    flick.perform();
    y = link.getLocation().y;
    
    // After flicking, the element should now be visible on the screen.
    assertTrue("Got: " + y, y < 4200);
  }

  @Ignore(value = {CHROME, FIREFOX, HTMLUNIT, IE, IPHONE, OPERA, SELENESE},
      reason = "TouchScreen operations not supported")
  @NeedsFreshDriver
  public void testCanFlickVerticallyFast() {
    driver.get(pages.touchLongContentPage);

    WebElement link = driver.findElement(By.id("link4"));
    int y = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    assertTrue(y > 8700);

    Action flick = getBuilder(driver).flick(0, 1500).build();
    flick.perform();
    y = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Got: " + y, y < 4000);
  }
}
