package org.openqa.selenium.htmlunit;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

/**
 * Implements keyboard operations using the HtmlUnit WebDriver.
 */
public class HtmlUnitKeyboard implements Keyboard {
  HtmlUnitKeyboard() {
  }

  @Override
  public void sendKeys(WebElement toElement, CharSequence... keysToSend) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    htmlElem.sendKeys(keysToSend);
  }

  @Override
  public void pressKey(WebElement toElement, Keys keyToPress) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    htmlElement.sendKeyDownEvent(keyToPress);
  }

  @Override
  public void releaseKey(WebElement toElement, Keys keyToRelease) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    htmlElement.sendKeyUpEvent(keyToRelease);
  }
}
