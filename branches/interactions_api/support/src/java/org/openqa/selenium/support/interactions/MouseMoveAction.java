package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Moves the mouse from 
 */
public class MouseMoveAction implements Action {
  private final Mouse mouse;
  private final WebElement toElement;

  public MouseMoveAction(Mouse mouse, WebElement toElement) {
    this.mouse = mouse;
    this.toElement = toElement;
  }


  public void perform() {
    mouse.mouseMove(toElement);
  }
}
