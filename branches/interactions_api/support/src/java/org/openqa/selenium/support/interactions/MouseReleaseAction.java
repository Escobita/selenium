package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Releases the left mouse button
 */

public class MouseReleaseAction implements Action {
  private Mouse mouse;
  private WebElement onElement;

  public MouseReleaseAction(Mouse mouse, WebElement element) {
    this.mouse = mouse;
    this.onElement = element;
  }

  public void perform() {
    mouse.mouseUp(onElement);
  }
}
