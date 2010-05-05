package org.openqa.selenium.support.interactions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

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

