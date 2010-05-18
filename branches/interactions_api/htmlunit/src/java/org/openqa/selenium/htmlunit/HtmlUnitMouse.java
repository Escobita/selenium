package org.openqa.selenium.htmlunit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Mouse;

/**
 * Implements mouse operations using the HtmlUnit WebDriver.
 */
public class HtmlUnitMouse implements Mouse {
  public void click(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.click();
  }

  public void doubleClick(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.doubleClick();    
  }

  public void mouseDown(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseDown();
  }

  public void mouseUp(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseUp();
  }

  public void mouseMove(WebElement fromElement, WebElement toElement) {
    //((HtmlUnitWebElement) fromElement).leaveHere();

    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    htmlElem.moveToHere();
  }


  public void mouseMove(long xOffset, long yOffset) {
    throw new UnsupportedOperationException("Moving to arbitrary X,Y coordinates not supported.");
  }

  public void contextClick(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseContextClick();
  }
}
