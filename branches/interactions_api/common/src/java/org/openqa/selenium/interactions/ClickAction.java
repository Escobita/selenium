package org.openqa.selenium.interactions;

import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

/**
 * clicks an element.
 */
public class ClickAction implements Action {
  private final Mouse mouse;
  private final WebElement onElement;
  public ClickAction(Mouse mouse, WebElement onElement) {
    this.mouse = mouse;
    this.onElement = onElement;
  }

  public void perform() {
    mouse.click(onElement);
  }
}

