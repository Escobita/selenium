package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Double-clicks an element.
 */
public class DoubleClickAction implements Action {
  private final Mouse mouse;
  private final WebElement onElement;
  public DoubleClickAction(Mouse mouse, WebElement onElement) {
    this.mouse = mouse;
    this.onElement = onElement;
  }

  public void perform() {
    mouse.doubleClick(onElement);
  }
}
