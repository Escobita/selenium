// Copyright 2010 Google Inc. All Rights Reserved.
package org.openqa.selenium.htmlunit;

/**
 * Holds the state of the modifier keys (Shift, ctrl, alt) for HtmlUnit.
 *
 * @author eranm@google.com (Eran Messeri)
 */
public class KeyboardModifiersState {
  private boolean shiftPressed = false;
  private boolean ctrlPressed = false;
  private boolean altPressed = false;

  public void setShiftPressed(boolean shiftPressed) {
    this.shiftPressed = shiftPressed;
  }

}
