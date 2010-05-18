package org.openqa.selenium.internal;

import org.openqa.selenium.WebElement;

/**
 * Interface reperesenting basic Mouse operations.
 */
public interface Mouse {
  void click(WebElement onElement);
  void doubleClick(WebElement onElement);
  void mouseDown(WebElement onElement);
  void mouseUp(WebElement onElement);
  void mouseMove(WebElement toElement);
  void mouseMove(long xOffset, long yOffset);
  // Right-clicks an element. 
  void contextClick(WebElement onElement);
}
