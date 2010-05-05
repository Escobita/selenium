package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

/**
 * Sending a sequence of keys to an element.
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

  public void perform() {
    keyboard.sendKeys(toElement, keysToSend);
  }
}
