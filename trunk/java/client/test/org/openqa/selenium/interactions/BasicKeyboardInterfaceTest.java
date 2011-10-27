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

import static org.openqa.selenium.Ignore.Driver.ANDROID;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.TestUtilities.isFirefox;
import static org.openqa.selenium.TestUtilities.isInternetExplorer;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.Ignore;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Tests interaction through the advanced gestures API of keyboard handling.
 * 
 */
@Ignore(IE)
public class BasicKeyboardInterfaceTest extends AbstractDriverTestCase {
  private Actions getBuilder(WebDriver driver) {
    return new Actions(driver);
  }

  @JavascriptEnabled
  @Ignore({ANDROID, IPHONE, SELENESE})
  public void testBasicKeyboardInput() {
    driver.get(pages.javascriptPage);

    WebElement keyReporter = driver.findElement(By.id("keyReporter"));

    Action sendLowercase = getBuilder(driver).sendKeys(keyReporter, "abc def").build();

    sendLowercase.perform();

    assertThat(keyReporter.getAttribute("value"), is("abc def"));

  }

  @JavascriptEnabled
  @Ignore({ANDROID, IPHONE, SELENESE, IE})
  public void testSendingKeyDownOnly() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));

    Action pressShift = getBuilder(driver).keyDown(keysEventInput, Keys.SHIFT).build();

    pressShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));
    String logText = keyLoggingElement.getText();

    Action releaseShift = getBuilder(driver).keyUp(keysEventInput, Keys.SHIFT).build();
    releaseShift.perform();

    assertTrue("Key down event not isolated, got: " + logText,
        logText.endsWith("keydown"));
  }

  @JavascriptEnabled
  @Ignore({ANDROID, IPHONE, SELENESE, IE})
  public void testSendingKeyUp() {
    driver.get(pages.javascriptPage);
    WebElement keysEventInput = driver.findElement(By.id("theworks"));

    Action pressShift = getBuilder(driver).keyDown(keysEventInput, Keys.SHIFT).build();
    pressShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));

    String eventsText = keyLoggingElement.getText();
    assertTrue("Key down should be isolated for this test to be meaningful. " +
        "Got events: " + eventsText, eventsText.endsWith("keydown"));

    Action releaseShift = getBuilder(driver).keyUp(keysEventInput, Keys.SHIFT).build();

    releaseShift.perform();

    eventsText = keyLoggingElement.getText();
    assertTrue("Key up event not isolated. Got events: " + eventsText,
        eventsText.endsWith("keyup"));
  }

  @JavascriptEnabled
  @Ignore({ANDROID, HTMLUNIT, IPHONE, SELENESE, IE})
  public void testSendingKeysWithShiftPressed() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));

    keysEventInput.click();

    String existingResult = getFormEvents();

    Action pressShift = getBuilder(driver).keyDown(keysEventInput, Keys.SHIFT).build();
    pressShift.perform();

    Action sendLowercase = getBuilder(driver).sendKeys(keysEventInput, "ab").build();
    sendLowercase.perform();

    Action releaseShift = getBuilder(driver).keyUp(keysEventInput, Keys.SHIFT).build();
    releaseShift.perform();

    String expectedEvents = " keydown keydown keypress keyup keydown keypress keyup keyup";
    assertThatFormEventsFiredAreExactly("Shift key not held",
        existingResult + expectedEvents);

    assertThat(keysEventInput.getAttribute("value"), is("AB"));
  }

  @JavascriptEnabled
  @Ignore({ANDROID, IPHONE, SELENESE})
  public void testSendingKeysToActiveElement() {
    driver.get(pages.bodyTypingPage);

    Action someKeys = getBuilder(driver).sendKeys("ab").build();
    someKeys.perform();

    assertThatBodyEventsFiredAreExactly("keypress keypress");
    assertThatFormEventsFiredAreExactly("");
  }

  @Ignore({ANDROID, IPHONE, SELENESE})
  public void testBasicKeyboardInputOnActiveElement() {
    driver.get(pages.javascriptPage);

    WebElement keyReporter = driver.findElement(By.id("keyReporter"));

    keyReporter.click();

    Action sendLowercase = getBuilder(driver).sendKeys("abc def").build();

    sendLowercase.perform();

    assertThat(keyReporter.getAttribute("value"), is("abc def"));
  }

  private void assertThatFormEventsFiredAreExactly(String message, String expected) {
    assertThat(message, getFormEvents(), is(expected.trim()));
  }

  private String getFormEvents() {
    return driver.findElement(By.id("result")).getText().trim();
  }

  private void assertThatFormEventsFiredAreExactly(String expected) {
    assertThatFormEventsFiredAreExactly("", expected);
  }

  private void assertThatBodyEventsFiredAreExactly(String expected) {
    assertThat(driver.findElement(By.id("body_result")).getText().trim(), is(expected.trim()));
  }
}
