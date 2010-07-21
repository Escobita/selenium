package org.openqa.selenium.htmlunit;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Implements keyboard operations using the HtmlUnit WebDriver.
 */
public class HtmlUnitKeyboard implements Keyboard {
  HtmlUnitKeyboard() {
  }

  public void sendKeys(WebElement toElement, CharSequence... keysToSend) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    htmlElem.sendKeys(keysToSend);
  }

  public void pressKey(WebElement toElement, Keys keyToPress) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    htmlElement.sendKeyDownEvent(keyToPress);
  }

  public void releaseKey(WebElement toElement, Keys keyToRelease) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    htmlElement.sendKeyUpEvent(keyToRelease);
  }
}
