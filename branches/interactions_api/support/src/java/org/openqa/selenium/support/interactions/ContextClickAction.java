package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Context-clicks an element
 */
public class ContextClickAction {
  private final Mouse mouse;
  private final WebElement onElement;

  public ContextClickAction(Mouse mouse, WebElement onElement) {
    this.mouse = mouse;
    this.onElement = onElement;
  }

  public void perform() {
    mouse.contextClick(onElement);
  }

}
