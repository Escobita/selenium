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
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests interaction through the advanced gestures API of keyboard handling.
 * 
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class TestBasicKeyboardInterface extends AbstractDriverTestCase {
  public void testBasicKeyboardInput() {
    driver.get(pages.javascriptPage);

    WebElement keyReporter = driver.findElement(By.id("keyReporter"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    SendKeysAction sendLowercase = new SendKeysAction(keyb, keyReporter, "abc def");

    sendLowercase.perform();

    assertThat(keyReporter.getValue(), is("abc def"));

  }

  @JavascriptEnabled
  public void testSendingKeyDownOnly() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    KeyDownAction pressShift = new KeyDownAction(keyb, keysEventInput, Keys.SHIFT);

    pressShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));

    assertTrue("Key down event not isolated.", keyLoggingElement.getText().endsWith("keydown"));
  }

  @JavascriptEnabled
  public void testSendingKeyUp() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    KeyUpAction releaseShift = new KeyUpAction(keyb, keysEventInput, Keys.SHIFT);

    releaseShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));
    assertTrue("Key up event not isolated.", keyLoggingElement.getText().equals("keyup"));
  }

  @JavascriptEnabled
  public void testSendingKeysWithShiftPressed() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    keysEventInput.click();

    KeyDownAction pressShift = new KeyDownAction(keyb, keysEventInput, Keys.SHIFT);
    pressShift.perform();

    SendKeysAction sendLowercase = new SendKeysAction(keyb, keysEventInput, "ab");
    sendLowercase.perform();

    KeyUpAction releaseShift = new KeyUpAction(keyb, keysEventInput, Keys.SHIFT);
    releaseShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));
    assertTrue("Shift key not held, events: " + keyLoggingElement.getText(),
        keyLoggingElement.getText()
            .equals("focus keydown keydown keypress keyup keydown keypress keyup keyup"));

    assertThat(keysEventInput.getValue(), is("AB"));
  }

  @JavascriptEnabled
  public void testSendingKeysToActiveElement() {
    driver.get(pages.bodyTypingPage);

    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    SendKeysAction someKeys = new SendKeysAction(keyb, "ab");
    someKeys.perform();

    WebElement bodyLoggingElement = driver.findElement(By.id("body_result"));
    assertThat(bodyLoggingElement.getText(), is("keypress keypress"));

    WebElement formLoggingElement = driver.findElement(By.id("result"));
    assertThat(formLoggingElement.getText(), is(""));
  }

}
