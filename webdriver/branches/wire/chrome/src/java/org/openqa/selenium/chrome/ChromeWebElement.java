package org.openqa.selenium.chrome;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.RenderedRemoteWebElement;

public class ChromeWebElement extends RenderedRemoteWebElement {

  public ChromeWebElement(ChromeDriver parent, String elementId) {
    setParent(parent);
    setId(elementId);
  }

  @Override
  public void dragAndDropBy(int moveRightBy, int moveDownBy) {
    throw new UnsupportedOperationException("Not yet supported in Chrome");
  }

  @Override
  public void dragAndDropOn(RenderedWebElement element) {
    throw new UnsupportedOperationException("Not yet supported in Chrome");
  }

  /**
   * Tests for WebElement equality by comparing internal opaque IDs.
   *
   * <p>Since the ChromeDriver will always return the same ID for an element
   * located on the page, the equality check and be computed inline without
   * sending an extra command through the CommandExecutor.
   *
   * @param obj The object to test for equality.
   * @return Whether the given object is equal to this instance.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof WebElement)) {
      return false;
    }

    WebElement other = (WebElement) obj;
    if (other instanceof WrapsElement) {
      other = ((WrapsElement) obj).getWrappedElement();
    }

    return other instanceof ChromeWebElement && id.equals(((ChromeWebElement) other).id);
  }
}
