package org.openqa.selenium.htmlunit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;

/**
 * Implements keyboard operations using the HtmlUnit WebDriver.
 */
public class HtmlUnitKeyboardInput implements Keyboard {
  private final HtmlUnitDriver parent;

  HtmlUnitKeyboardInput(HtmlUnitDriver parent) {
    this.parent = parent;
  }

  @Override
  public void sendKeys(WebElement toElement, CharSequence... keysToSend) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    htmlElem.sendKeys(keysToSend);
  }

  @Override
  public void pressKey(WebElement toElement, CharSequence keyToPress) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void releaseKey(WebElement toElement, CharSequence keyToRelease) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
