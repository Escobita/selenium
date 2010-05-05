package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Presses the left mouse button without releasing it.
 */
public class MouseClickAndHoldAction implements Action {
  private final WebElement onElement;
  private final Mouse mouse;

  public MouseClickAndHoldAction(Mouse mouse, WebElement onElement) {
    this.mouse = mouse;
    this.onElement = onElement;
  }

  public void perform() {
    mouse.mouseDown(onElement);
  }
}
