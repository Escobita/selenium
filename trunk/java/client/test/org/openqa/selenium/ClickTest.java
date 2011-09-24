/*
Copyright 2011 WebDriver committers
Copyright 2011 Google Inc.

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

import static org.openqa.selenium.Ignore.Driver.ANDROID;
import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.OPERA;
import static org.openqa.selenium.Ignore.Driver.SELENESE;
import static org.openqa.selenium.TestWaiter.waitFor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ClickTest extends AbstractDriverTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    driver.get(pages.clicksPage);
  }

  @Override
  protected void tearDown() throws Exception {
    driver.switchTo().defaultContent();

    super.tearDown();
  }

  @Ignore(value = {IPHONE}, reason = "iPhone: Frame switching is unsupported")
  public void testCanClickOnALinkAndFollowIt() {
    driver.findElement(By.id("normal")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  @Ignore(value = {IPHONE, CHROME, OPERA, SELENESE},
      reason = "Not tested on these browsers.")
  public void testCanClickOnALinkThatOverflowsAndFollowIt() {
    driver.findElement(By.id("overflowLink")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  @JavascriptEnabled
  public void testCanClickOnAnAnchorAndNotReloadThePage() {
    ((JavascriptExecutor) driver).executeScript("document.latch = true");

    driver.findElement(By.id("anchor")).click();

    Boolean samePage = (Boolean) ((JavascriptExecutor) driver)
        .executeScript("return document.latch");

    assertEquals("Latch was reset", Boolean.TRUE, samePage);
  }

  @Ignore(value = {IPHONE, OPERA, ANDROID}, reason = "iPhone: Frame switching is unsupported"
      + "Opera: Incorrect runtime retrieved, Android: A bug in emulator JSC egine on 2.2, "
      + "works on devices.")
  public void testCanClickOnALinkThatUpdatesAnotherFrame() {
    driver.switchTo().frame("source");

    driver.findElement(By.id("otherframe")).click();
    driver.switchTo().defaultContent().switchTo().frame("target");

    assertTrue("Target did not reload",
        driver.getPageSource().contains("Hello WebDriver"));
  }

  @JavascriptEnabled
  @Ignore(value = {IPHONE, SELENESE, OPERA, ANDROID},
      reason = "iPhone: Frame switching is unsupported"
          + "Opera: Incorrect runtime retrieved, Android: fails when running with other tests.")
  public void testElementsFoundByJsCanLoadUpdatesInAnotherFrame() {
    driver.switchTo().frame("source");

    WebElement toClick = (WebElement) ((JavascriptExecutor) driver).executeScript(
        "return document.getElementById('otherframe');"
        );
    toClick.click();
    driver.switchTo().defaultContent().switchTo().frame("target");

    assertTrue("Target did not reload",
        driver.getPageSource().contains("Hello WebDriver"));
  }

  @JavascriptEnabled
  @Ignore(value = {IPHONE, SELENESE, OPERA, ANDROID},
      reason = "iPhone: Frame switching is unsupported"
          + "Opera: Incorrect runtime retrieved, Android: Fails when running with other tests.")
  public void testJsLoactedElementsCanUpdateFramesIfFoundSomehowElse() {
    driver.switchTo().frame("source");

    // Prime the cache of elements
    driver.findElement(By.id("otherframe"));

    // This _should_ return the same element
    WebElement toClick = (WebElement) ((JavascriptExecutor) driver).executeScript(
        "return document.getElementById('otherframe');"
        );
    toClick.click();
    driver.switchTo().defaultContent().switchTo().frame("target");

    assertTrue("Target did not reload",
        driver.getPageSource().contains("Hello WebDriver"));
  }

  @JavascriptEnabled
  public void testCanClickOnAnElementWithTopSetToANegativeNumber() {
    String page = appServer.whereIs("styledPage.html");
    driver.get(page);
    WebElement searchBox = driver.findElement(By.name("searchBox"));
    searchBox.sendKeys("Cheese");
    driver.findElement(By.name("btn")).click();

    String log = driver.findElement(By.id("log")).getText();
    assertEquals("click", log);
  }

  @JavascriptEnabled
  @Ignore(value = {ANDROID, CHROME, HTMLUNIT, OPERA, SELENESE}, reason = "Not implemented")
  public void testShouldSetRelatedTargetForMouseOver() {
    driver.get(pages.javascriptPage);

    driver.findElement(By.id("movable")).click();

    String log = driver.findElement(By.id("result")).getText();

    // Note: It is not guaranteed that the relatedTarget property of the mouseover
    // event will be the parent, when using native events. Only check that the mouse
    // has moved to this element, not that the parent element was the related target.
    if (TestUtilities.isNativeEventsEnabled(driver)) {
      assertTrue("Should have moved to this element.", log.startsWith("parent matches?"));
    } else {
      assertEquals("parent matches? true", log);
    }
  }
  
  @JavascriptEnabled
  @NoDriverAfterTest
  @Ignore(value = {IPHONE, SELENESE}, reason = "Doesn't support multiple windows")
  public void testShouldOnlyFollowHrefOnce() {
    driver.get(pages.clicksPage);
    int windowHandlesBefore = driver.getWindowHandles().size();
    
    driver.findElement(By.id("new-window")).click();
    assertThat(driver.getWindowHandles().size(), equalTo(windowHandlesBefore + 1));
  }

  @Ignore
  public void testShouldSetRelatedTargetForMouseOut() {
    fail("Must. Write. Meamingful. Test (but we don't fire mouse outs synthetically");
  }

  @Ignore(HTMLUNIT)
  public void testClickingLabelShouldSetCheckbox() {
    driver.get(pages.formPage);

    driver.findElement(By.id("label-for-checkbox-with-label")).click();

    assertTrue(
        "Should be selected",
        driver.findElement(By.id("checkbox-with-label")).isSelected());
  }

  public void testCanClickOnALinkWithEnclosedImage() {
    driver.findElement(By.id("link-with-enclosed-image")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  public void testCanClickOnAnImageEnclosedInALink() {
    driver.findElement(By.id("link-with-enclosed-image")).findElement(By.tagName("img")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  public void testCanClickOnALinkThatContainsTextWrappedInASpan() {
    driver.findElement(By.id("link-with-enclosed-span")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  public void testCanClickOnAnElementEnclosedInALink() {
    driver.findElement(By.id("link-with-enclosed-span")).findElement(By.tagName("span")).click();

    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

}
