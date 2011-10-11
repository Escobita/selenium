/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.

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

package org.openqa.selenium.interactions;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.Ignore;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WaitingConditions;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.openqa.selenium.Ignore.Driver.ANDROID;
import static org.openqa.selenium.Ignore.Driver.FIREFOX;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.OPERA;
import static org.openqa.selenium.Ignore.Driver.REMOTE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;
import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.elementToExist;

/**
 * Tests combined input actions.
 */
@Ignore(ANDROID)
public class CombinedInputActionsTest extends AbstractDriverTestCase {

  // TODO: Check if this could work in any browser without native events.
  @JavascriptEnabled
  @Ignore
  public void testClickingOnFormElements() {
    driver.get(pages.formSelectionPage);

    List<WebElement> options = driver.findElements(By.tagName("option"));

    Actions actions = new Actions(driver);
    Action selectThreeOptions = actions.click(options.get(1))
        .keyDown(Keys.SHIFT)
        .click(options.get(2))
        .click(options.get(3))
        .keyUp(Keys.SHIFT)
        .build();

    selectThreeOptions.perform();

    WebElement showButton = driver.findElement(By.name("showselected"));
    showButton.click();

    WebElement resultElement = driver.findElement(By.id("result"));
    assertEquals("Should have picked the last three options.", "roquefort parmigiano cheddar",
        resultElement.getText());
  }

  @JavascriptEnabled
  @Ignore({ANDROID, IE, FIREFOX, REMOTE, IPHONE, SELENESE, OPERA})
  public void testSelectingMultipleItems() {
    driver.get(pages.selectableItemsPage);

    WebElement reportingElement = driver.findElement(By.id("infodiv"));

    assertEquals("no info", reportingElement.getText());

    List<WebElement> listItems = driver.findElements(By.tagName("li"));

    Actions actions = new Actions(driver);
    Action selectThreeItems = actions.keyDown(Keys.CONTROL)
        .click(listItems.get(1))
        .click(listItems.get(3))
        .click(listItems.get(5))
        .keyUp(Keys.CONTROL)
        .build();

    selectThreeItems.perform();

    assertEquals("#item2 #item4 #item6", reportingElement.getText());

    // Now click on another element, make sure that's the only one selected.
    actions = new Actions(driver);
    actions.click(listItems.get(6)).build().perform();
    assertEquals("#item7", reportingElement.getText());
  }

  private void navigateToClicksPageAndClickLink() {
    driver.get(appServer.whereIs("clicks.html"));

    waitFor(elementToExist(driver, "normal"));
    WebElement link = driver.findElement(By.id("normal"));

    new Actions(driver)
        .click(link)
        .perform();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  @Ignore({SELENESE, IPHONE})
  public void testCanClickOnLinks() {
    navigateToClicksPageAndClickLink();
  }

  @Ignore(
      value = {HTMLUNIT, IPHONE, SELENESE},
      reason = "HtmlUnit: Advanced mouse actions only implemented in rendered browsers")
  public void testCanClickOnLinksWithAnOffset() {
    driver.get(appServer.whereIs("clicks.html"));

    waitFor(elementToExist(driver, "normal"));
    WebElement link = driver.findElement(By.id("normal"));

    new Actions(driver)
        .moveToElement(link, 1, 1)
        .click()
        .perform();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  /**
   * This test demonstrates the following problem: When the representation of
   * the mouse in the driver keeps the wrong state, mouse movement will end
   * up at the wrong coordinates.
   */
  @Ignore({SELENESE, IPHONE})
  public void testMouseMovementWorksWhenNavigatingToAnotherPage() {
    navigateToClicksPageAndClickLink();

    WebElement linkId = driver.findElement(By.id("linkId"));
    new Actions(driver)
        .moveToElement(linkId, 1, 1)
        .click()
        .perform();

    waitFor(WaitingConditions.pageTitleToBe(driver, "We Arrive Here"));
  }

  @Ignore({SELENESE, HTMLUNIT})
  public void testChordControlCutAndPaste() {
    // FIXME: macs don't have CONRTROL key
    if (Platform.getCurrent().is(Platform.MAC)) {
      return;
    }

    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.id("keyReporter"));

    new Actions(driver)
        .sendKeys(element, "abc def")
        .perform();

    assertEquals("abc def", element.getAttribute("value"));

    new Actions(driver)
        .sendKeys(Keys.CONTROL + "a")
        .sendKeys(Keys.CONTROL + "x")
        .perform();

    assertEquals("", element.getAttribute("value"));

    new Actions(driver)
        .sendKeys(Keys.CONTROL + "v")
        .sendKeys(Keys.CONTROL + "v")
        .perform();

    assertEquals("abc defabc def", element.getAttribute("value"));
  }
}
