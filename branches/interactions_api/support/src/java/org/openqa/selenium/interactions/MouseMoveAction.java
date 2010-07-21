package org.openqa.selenium.interactions;

import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

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
