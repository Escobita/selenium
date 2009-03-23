package org.openqa.selenium.chromium;

import static org.openqa.selenium.chromium.ExportedWebDriver.SUCCESS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

import java.util.List;

class Finder implements SearchContext,
      FindsById, FindsByTagName, FindsByClassName, FindsByLinkText, FindsByName, FindsByXPath {

  private final ExportedWebDriver lib;
  private final Pointer driver;

  public Finder(ExportedWebDriver lib, Pointer driver) {
    this.lib = lib;
    this.driver = driver;
  }

  public WebElement findElementById(String id) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementById(driver, new WString(id), retValue)) {
      throw new NoSuchElementException("Failed to find element with id=" + id);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsById(String id) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsById(driver, new WString(id), retValue)) {
      throw new NoSuchElementException("Failed to find element with id=" + id);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElementByTagName(String tag) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByTagName(driver, new WString(tag), retValue)) {
      throw new NoSuchElementException("Failed to find element with tagName=" + tag);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByTagName(String tag) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByTagName(driver, new WString(tag), retValue)) {
      throw new NoSuchElementException("Failed to find element with tagName=" + tag);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElementByClassName(String className) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByClassName(driver, new WString(className), retValue)) {
      throw new NoSuchElementException("Failed to find element with className=" + className);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByClassName(String className) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByClassName(driver, new WString(className), retValue)) {
      throw new NoSuchElementException("Failed to find element with className=" + className);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElementByLinkText(String text) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByLinkText(driver, new WString(text), retValue)) {
      throw new NoSuchElementException("Failed to find element with link text=" + text);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByLinkText(String text) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByLinkText(driver, new WString(text), retValue)) {
      throw new NoSuchElementException("Failed to find element with link text=" + text);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElementByPartialLinkText(String pattern) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByPartialLinkText(driver, new WString(pattern), retValue)) {
      throw new NoSuchElementException("Failed to find element with partial link text=" + pattern);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByPartialLinkText(String pattern) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByPartialLinkText(driver, new WString(pattern), retValue)) {
      throw new NoSuchElementException("Failed to find element with partial link text=" + pattern);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }  

  public WebElement findElementByName(String name) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByName(driver, new WString(name), retValue)) {
      throw new NoSuchElementException("Failed to find element with name=" + name);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByName(String name) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByName(driver, new WString(name), retValue)) {
      throw new NoSuchElementException("Failed to find element with name=" + name);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElementByXPath(String xpath) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementByXPath(driver, new WString(xpath), retValue)) {
      throw new NoSuchElementException("Failed to find element with XPath=" + xpath);
    }
    return new ChromeElement(lib, driver, retValue.getValue());
  }

  public List<WebElement> findElementsByXPath(String xpath) {
    PointerByReference retValue = new PointerByReference();
    if (SUCCESS != lib.wdFindElementsByXPath(driver, new WString(xpath), retValue)) {
      throw new NoSuchElementException("Failed to find element with XPath=" + xpath);
    }
    return ChromeElement.createCollection(lib, driver, retValue.getValue());
  }

  public WebElement findElement(By by) {
    return by.findElement(this);
  }

  public List<WebElement> findElements(By by) {
    return by.findElements(this);
  }
}
