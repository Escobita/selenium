package org.openqa.selenium.chromium;

import static org.openqa.selenium.chromium.ExportedWebDriver.SUCCESS;

import org.openqa.selenium.chromium.ExportedWebDriver.StringWrapper;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ChromeElement implements RenderedWebElement, SearchContext, Locatable {
  private ExportedWebDriver lib;
  private Pointer driver;
  private Pointer element;

  public static List<WebElement> createCollection(
      ExportedWebDriver lib, Pointer driver, Pointer collection) {
    return new ElementCollection(lib, driver, collection).toList();
  }

  public ChromeElement(ExportedWebDriver lib, Pointer driver, Pointer element) {
    this.lib = lib;
    this.driver = driver;
    this.element = element;
  }

  public Dimension getSize() {
    NativeLongByReference width = new NativeLongByReference();
    NativeLongByReference height = new NativeLongByReference();
    int result = lib.wdGetSize(element, width, height);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to find the size of the element.");
    }
    return new Dimension(width.getValue().intValue(), height.getValue().intValue());
  }

  public Point getLocation() {
    NativeLongByReference left = new NativeLongByReference();
    NativeLongByReference top = new NativeLongByReference();
    int result = lib.wdGetLocation(element, left, top);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to find location.");
    }
    return new Point(left.getValue().intValue(), top.getValue().intValue());
  }

  // --------------------------------------------------------------------------
  // Implements WebElement
  // --------------------------------------------------------------------------
  public void click() {
    lib.wdClick(element);
    try { Thread.sleep(100); } catch(Exception e) {}
  }

  public void submit() {
    lib.wdSubmit(element);
    try { Thread.sleep(100); } catch(Exception e) {}
  }

  public String getValue() {
    return getAttribute("value");
  }

  public String getElementName() {
    PointerByReference retValue = new PointerByReference();
    int result = lib.wdGetElementName(element, retValue);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to get element name");
    }
    return new StringWrapper(lib, retValue).toString();
  }

  public String getAttribute(String name) {
    PointerByReference retValue = new PointerByReference();
    int result = lib.wdGetAttribute(element, new WString(name), retValue);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to attribute: " + name);
    }
    return new StringWrapper(lib, retValue).toString();
  }

  public String getValueOfCssProperty(String propertyName) {
    PointerByReference retValue = new PointerByReference();
    int result = lib.wdGetValueOfCssProperty(element, new WString(propertyName), retValue);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to css property: " + propertyName);
    }
    return new StringWrapper(lib, retValue).toString();
  }

  public String getText() {
    PointerByReference retValue = new PointerByReference();
    int result = lib.wdGetText(element, retValue);
    if (result != SUCCESS) {
      throw new RuntimeException("Failed to get text");
    }
    return new StringWrapper(lib, retValue).toString();
  }

  public void dragAndDropOn(RenderedWebElement element) {
    // TODO(amitabh): Implement this.
  }

  public void dragAndDropBy(int moveRightBy, int moveDownBy) {
    // TODO(amitabh): Implement this.
  }

  public boolean isDisplayed() {
    IntByReference retValue = new IntByReference();
    lib.wdIsDisplayed(element, retValue);
    return (retValue.getValue() == 1);
  }

  // --------------------------------------------------------------------------
  // Implements SearchContext.
  // --------------------------------------------------------------------------
  public List<WebElement> findElements(By by) {
    return new Finder(lib, driver).findElements(by);
  }

  public WebElement findElement(By by) {
    return new Finder(lib, driver).findElement(by);
  }

  public void sendKeys(CharSequence... keysToSend) {
    StringBuilder builder = new StringBuilder();
    for (CharSequence seq : keysToSend) {
      builder.append(seq);
    }
    lib.wdSendKeys(element, new WString(builder.toString()));
  }

  public void clear() {
    lib.wdClear(element);
  }

  public boolean toggle() {
    IntByReference retValue = new IntByReference();
    lib.wdToggle(element, retValue);
    return (retValue.getValue() == 1);
  }

  public boolean isSelected() {
    IntByReference retValue = new IntByReference();
    lib.wdIsSelected(element, retValue);
    return (retValue.getValue() == 1);
  }

  public void setSelected() {
    lib.wdSetSelected(element);
  }

  public boolean isEnabled() {
    IntByReference retValue = new IntByReference();
    lib.wdIsEnabled(element, retValue);
    return (retValue.getValue() == 1);
  }

  // --------------------------------------------------------------------------
  // Implements Locatable
  // --------------------------------------------------------------------------
  public Point getLocationOnScreenOnceScrolledIntoView() {
    // TODO(amitabh): Implement this.
    return new Point(0, 0);
  }

  // --------------------------------------------------------------------------
  // ElementCollection implementation.
  // --------------------------------------------------------------------------
  private static class ElementCollection {
    private final List<WebElement> elements;

    public ElementCollection(ExportedWebDriver lib, Pointer driver, Pointer collection) {
      elements = extractElements(lib, driver, collection);
    }

    public List<WebElement> toList() {
      return elements;
    }

    private List<WebElement> extractElements(ExportedWebDriver lib, Pointer driver, Pointer collection) {
      IntByReference lenReturn = new IntByReference();
      lib.wdGetCollectionLength(collection, lenReturn);

      int length = lenReturn.getValue();
      List<WebElement> toReturn = new ArrayList<WebElement>(length);
      for (int i = 0; i < length; i++) {
        PointerByReference elemRef = new PointerByReference();
        toReturn.add(i, new ChromeElement(lib, driver, elemRef.getValue()));
      }

      // Free the list in the driver.
      lib.wdFreeElementCollection(collection);
      return toReturn;
    }
  }
}
