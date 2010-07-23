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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keyboard;

/**
 * Sending a sequence of keys to an element.
 *
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class SendKeysAction implements Action {
  private final Keyboard keyboard;
  private final WebElement toElement;
  private final CharSequence[] keysToSend;

  public SendKeysAction(Keyboard keyboard, WebElement toElement, CharSequence... keysToSend) {
    this.keyboard = keyboard;
    this.toElement = toElement;
    this.keysToSend = keysToSend;
  }

  public SendKeysAction(Keyboard keyboard, CharSequence... keysToSend) {
    this(keyboard, null, keysToSend);
  }

  public void perform() {
    keyboard.sendKeys(toElement, keysToSend);
  }
}
