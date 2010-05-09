/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

public class VisibilityTest extends AbstractDriverTestCase {

  @JavascriptEnabled
  @Test public void shouldAllowTheUserToTellIfAnElementIsDisplayedOrNot() {
    driver.get(pages.javascriptPage);

    assertThat(((RenderedWebElement) driver.findElement(By.id("displayed"))).isDisplayed(),
               is(true));
    assertThat(((RenderedWebElement) driver.findElement(By.id("none"))).isDisplayed(), is(false));
    assertThat(((RenderedWebElement) driver.findElement(By.id("suppressedParagraph")))
        .isDisplayed(), is(false));
    assertThat(((RenderedWebElement) driver.findElement(By.id("hidden"))).isDisplayed(), is(false));
  }

  @JavascriptEnabled
  @Test public void VisibilityShouldTakeIntoAccountParentVisibility() {
    driver.get(pages.javascriptPage);

    RenderedWebElement childDiv = (RenderedWebElement) driver.findElement(By.id("hiddenchild"));
    RenderedWebElement hiddenLink = (RenderedWebElement) driver.findElement(By.id("hiddenlink"));

    assertFalse(childDiv.isDisplayed());
    assertFalse(hiddenLink.isDisplayed());
  }

  @JavascriptEnabled
  @Test public void shouldCountElementsAsVisibleIfStylePropertyHasBeenSet() {
    driver.get(pages.javascriptPage);

    RenderedWebElement shown = (RenderedWebElement) driver.findElement(By.id("visibleSubElement"));

    assertTrue(shown.isDisplayed());
  }

  @JavascriptEnabled
  @Test public void shouldModifyTheVisibilityOfAnElementDynamically() {
    driver.get(pages.javascriptPage);

    RenderedWebElement element = (RenderedWebElement) driver.findElement(By.id("hideMe"));

    assertTrue(element.isDisplayed());

    element.click();

    assertFalse(element.isDisplayed());
  }

  @JavascriptEnabled
  @Test public void HiddenInputElementsAreNeverVisible() {
    driver.get(pages.javascriptPage);

    RenderedWebElement shown = (RenderedWebElement) driver.findElement(By.name("hidden"));

    assertFalse(shown.isDisplayed());
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldNotBeAbleToClickOnAnElementThatIsNotDisplayed() {
    driver.get(pages.javascriptPage);
    WebElement element = driver.findElement(By.id("unclickable"));

    try {
      element.click();
      fail("You should not be able to click on an invisible element");
    } catch (ElementNotVisibleException e) {
      // This is expected
    }
  }

  @JavascriptEnabled
  @Ignore(value = {SELENESE, IPHONE}, reason = "iPhone: toggle not yet implemented")
  @Test public void shouldNotBeAbleToToggleAnElementThatIsNotDisplayed() {
    driver.get(pages.javascriptPage);
    WebElement element = driver.findElement(By.id("untogglable"));

    try {
      element.toggle();
      fail("You should not be able to toggle an invisible element");
    } catch (ElementNotVisibleException e) {
      // This is expected
    }
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldNotBeAbleToSelectAnElementThatIsNotDisplayed() {
    driver.get(pages.javascriptPage);
    WebElement element = driver.findElement(By.id("untogglable"));

    try {
      element.setSelected();
      fail("You should not be able to select an invisible element");
    } catch (ElementNotVisibleException e) {
      // This is expected
    }
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldNotBeAbleToTypeAnElementThatIsNotDisplayed() {
    driver.get(pages.javascriptPage);
    WebElement element = driver.findElement(By.id("unclickable"));

    try {
      element.sendKeys("You don't see me");
      fail("You should not be able to send keyboard input to an invisible element");
    } catch (ElementNotVisibleException e) {
      // This is expected
    }

    assertThat(element.getValue(), is(not("You don't see me")));
  }

  @JavascriptEnabled
  @Ignore({HTMLUNIT, IE, SELENESE})
  @Test public void shouldNotAllowAnElementWithZeroHeightToBeCountedAsDisplayed() {
    driver.get(pages.javascriptPage);

    RenderedWebElement zeroHeight = (RenderedWebElement) driver.findElement(By.id("zeroheight"));

    assertFalse(zeroHeight.isDisplayed());
  }

  @JavascriptEnabled
  @Ignore({HTMLUNIT, IE, SELENESE})
  @Test public void shouldNotAllowAnElementWithZeroWidthToBeCountedAsDisplayed() {
    driver.get(pages.javascriptPage);

    RenderedWebElement zeroWidth = (RenderedWebElement) driver.findElement(By.id("zerowidth"));

    assertFalse(zeroWidth.isDisplayed());
  }

}
