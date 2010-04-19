package org.openqa.selenium.support.interactions;

import com.google.common.collect.Lists;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

/**
 * Emulates key press only, without the release.
 */
public class KeyDownAction extends SingleKeyAction implements Action {
  public KeyDownAction(Keyboard keyboard, WebElement toElement, Keys key) {
    super(keyboard, toElement, key);
  }

  @Override
  public void perform() {
    keyboard.pressKey(toElement, key);
  }
}
