package org.openqa.selenium.interactions;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Emulates key press only, without the release.
 */
public class KeyDownAction extends SingleKeyAction implements Action {
  public KeyDownAction(Keyboard keyboard, WebElement toElement, Keys key) {
    super(keyboard, toElement, key);
  }

  public KeyDownAction(Keyboard keyboard, Keys key) {
    super(keyboard, null, key);
  }

  public void perform() {
    keyboard.pressKey(toElement, key);
  }
}
