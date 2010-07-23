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

package org.openqa.selenium.htmlunit;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Implements keyboard operations using the HtmlUnit WebDriver.
 *
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class HtmlUnitKeyboard implements Keyboard {
  private final KeyboardModifiersState modifiersState;
  private final HtmlUnitDriver parent;

  HtmlUnitKeyboard(HtmlUnitDriver parent, KeyboardModifiersState modifiersState) {
    this.modifiersState = modifiersState;
    this.parent = parent;
  }

  private HtmlUnitWebElement getElementToSend(WebElement toElement) {
    WebElement sendToElement = toElement;
    if (sendToElement == null) {
      sendToElement = parent.switchTo().activeElement();
    }

    return (HtmlUnitWebElement) sendToElement;
  }

  public void sendKeys(WebElement toElement, CharSequence... keysToSend) {

    HtmlUnitWebElement htmlElem = getElementToSend(toElement);
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
    HtmlUnitWebElement htmlElement = getElementToSend(toElement);
    modifiersState.storeKeyDown(keyToPress);
    htmlElement.sendKeyDownEvent(keyToPress);
  }

  public void releaseKey(WebElement toElement, Keys keyToRelease) {
    HtmlUnitWebElement htmlElement = getElementToSend(toElement);
    modifiersState.storeKeyUp(keyToRelease);
    htmlElement.sendKeyUpEvent(keyToRelease);
  }
}
