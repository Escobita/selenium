package org.openqa.selenium.interactions;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keyboard;

/**
 * Used both by KeyDownAction and KeyUpAction
 */
public class SingleKeyAction {
  final protected Keyboard keyboard;
  final protected WebElement toElement;
  final protected Keys key;
  private static final Keys[] MODIFIER_KEYS = {Keys.SHIFT, Keys.CONTROL, Keys.ALT};


  public SingleKeyAction(Keyboard keyboard, WebElement toElement, Keys key) {
    this.keyboard = keyboard;
    this.toElement = toElement;
    this.key = key;
    boolean isModifier = false;
    for (Keys modifier : MODIFIER_KEYS) {
      isModifier = isModifier | modifier.equals(key);
    }

    if (!isModifier) {
      throw new IllegalArgumentException("Key Down / Up events only make sense for modifier keys.");
    }

  }
}
