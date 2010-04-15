package org.openqa.selenium.support.interactions;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests interaction through the advanced gestures API of keyboard handling.
 * 
 * Created by Eran Mes (eran.mes@gmail.com)
 */
public class TestBasicKeyboardInterface extends AbstractDriverTestCase {
  public void testBasicKeyboardInput() {
    driver.get(javascriptPage);

    WebElement keyReporter = driver.findElement(By.id("keyReporter"));
    Keyboard keyb = ((HasInputDevices) driver).getKeyboard();

    SendKeysAction sendLowercase = new SendKeysAction(keyb, keyReporter, "abc def");

    sendLowercase.perform();

    assertThat(keyReporter.getValue(), is("abc def"));

  }
}
