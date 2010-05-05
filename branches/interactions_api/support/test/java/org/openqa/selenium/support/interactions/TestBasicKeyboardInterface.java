package org.openqa.selenium.support.interactions;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests interaction through the advanced gestures API of keyboard handling.
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

  public void testSendingKeyDownOnly() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    KeyDownAction pressShift = new KeyDownAction(keyb, keysEventInput, Keys.SHIFT);

    pressShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));

    assertTrue("Key down event not isolated.", keyLoggingElement.getText().endsWith("keydown"));
  }

  public void testSendingKeyUp() {
    driver.get(pages.javascriptPage);

    WebElement keysEventInput = driver.findElement(By.id("theworks"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    KeyUpAction releaseShift = new KeyUpAction(keyb, keysEventInput, Keys.SHIFT);

    releaseShift.perform();

    WebElement keyLoggingElement = driver.findElement(By.id("result"));
    assertTrue("Key up event not isolated.", keyLoggingElement.getText().equals("keyup"));
  }
}
