package org.openqa.selenium.support.interactions;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

/**
 * Emulates key release only, without the press.
 */
public class KeyUpAction extends SingleKeyAction implements Action {
  public KeyUpAction(Keyboard keyboard, WebElement toElement, Keys key) {
    super(keyboard, toElement, key);
  }

  public void perform() {
    keyboard.releaseKey(toElement, key);
  }
}
