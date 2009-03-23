package org.openqa.selenium.chromium;

import static org.openqa.selenium.chromium.ExportedWebDriver.SUCCESS;

import org.openqa.selenium.chromium.ExportedWebDriver.StringWrapper;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

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
  }

  public void submit() {
    lib.wdSubmit(element);
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
    // TODO(amitabh)
    return false;
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
    // TODO(amitabh)
    return false;
  }

  public boolean isSelected() {
    IntByReference retValue = new IntByReference();
    lib.wdIsSelected(element, retValue);
    // TODO(amitabh)
    return false;
  }

  public void setSelected() {
    lib.wdSetSelected(element);
  }

  public boolean isEnabled() {
    IntByReference retValue = new IntByReference();
    lib.wdIsEnabled(element, retValue);
    // TODO(amitabh)
    return false;
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
      List<WebElement> toReturn = new ArrayList<WebElement>(1);
      //toReturn.add(new ChromeElement(lib, driver, query));

      // TODO(amitabh): Free the collection memory here.
      return toReturn;
    }
  }
}
