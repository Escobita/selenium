package org.openqa.selenium.support.interactions;

import com.google.common.collect.Lists;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

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
    if (!Lists.newArrayList(MODIFIER_KEYS).contains(key)) {
      throw new IllegalArgumentException("Key Down / Up events only make sense for modifier keys.");
    }
  }
}
