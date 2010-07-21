package org.openqa.selenium.interactions;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

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
