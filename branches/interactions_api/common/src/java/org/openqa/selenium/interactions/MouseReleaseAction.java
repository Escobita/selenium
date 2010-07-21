package org.openqa.selenium.interactions;

import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

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
