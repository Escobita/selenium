package org.openqa.selenium.htmlunit;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Implements keyboard operations using the HtmlUnit WebDriver.
 */
public class HtmlUnitKeyboard implements Keyboard {
  private final KeyboardModifiersState modifiersState;

  HtmlUnitKeyboard(KeyboardModifiersState modifiersState) {
    this.modifiersState = modifiersState;
  }

  public void sendKeys(WebElement toElement, CharSequence... keysToSend) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    if (modifiersState.isShiftPressed()) {
      StringBuilder upperCaseKeys = new StringBuilder();
      for (CharSequence seq : keysToSend) {
        upperCaseKeys.append(seq.toString().toUpperCase());
        htmlElem.sendKeys(upperCaseKeys);
      }
    } else {
      // Send the keys without capitalizing anything.
      htmlElem.sendKeys(keysToSend);
    }
  }

  public void pressKey(WebElement toElement, Keys keyToPress) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    modifiersState.storeKeyDown(keyToPress);
    htmlElement.sendKeyDownEvent(keyToPress);
  }

  public void releaseKey(WebElement toElement, Keys keyToRelease) {
    HtmlUnitWebElement htmlElement = (HtmlUnitWebElement) toElement;
    modifiersState.storeKeyUp(keyToRelease);
    htmlElement.sendKeyUpEvent(keyToRelease);
  }
}
