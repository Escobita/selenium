// Copyright 2010 Google Inc. All Rights Reserved.
package org.openqa.selenium.htmlunit;

import org.openqa.selenium.Keys;

/**
 * Holds the state of the modifier keys (Shift, ctrl, alt) for HtmlUnit.
 *
 * @author eranm@google.com (Eran Messeri)
 */
public class KeyboardModifiersState {
  private boolean shiftPressed = false;
  private boolean ctrlPressed = false;
  private boolean altPressed = false;

  public boolean isShiftPressed() {
    return shiftPressed;
  }

  public boolean isCtrlPressed() {
    return ctrlPressed;
  }
  
  public boolean isAltPressed() {
    return altPressed;
  }

  public void storeKeyDown(Keys key) {
    storeIfEqualsShift(key, true);
    storeIfEqualsCtrl(key, true);
    storeIfEqualsAlt(key, true);
  }

  public void storeKeyUp(Keys key) {
    storeIfEqualsShift(key, false);
    storeIfEqualsCtrl(key, false);
    storeIfEqualsAlt(key, false);    
  }

  private void storeIfEqualsShift(Keys key, boolean keyState) {
    if (key.equals(Keys.SHIFT))
    shiftPressed = keyState;
  }

  private void storeIfEqualsCtrl(Keys key, boolean keyState) {
    if (key.equals(Keys.CONTROL))
    ctrlPressed = keyState;
  }

  private void storeIfEqualsAlt(Keys key, boolean keyState) {
    if (key.equals(Keys.ALT))
    altPressed = keyState;
  }
}
