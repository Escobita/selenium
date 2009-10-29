package org.openqa.selenium.chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ChromeWebElement implements RenderedWebElement, Locatable, 
FindsByXPath, FindsByLinkText, FindsById, FindsByName, FindsByTagName, FindsByClassName {

  private final ChromeDriver parent;
  private final String elementId;

  public ChromeWebElement(ChromeDriver parent, String elementId) {
      this.parent = parent;
      this.elementId = elementId;
  }
  
  String getElementId() {
    return elementId;
  }

  private ChromeResponse execute(DriverCommand driverCommand) {
    return execute(driverCommand, ImmutableMap.<String, Object>of());
  }

  ChromeResponse execute(DriverCommand driverCommand, Map<String, ?> parameters) {
    return parent.execute(driverCommand,
        ImmutableMap.<String, Object>builder()
            .put("elementId", elementId)
            .putAll(parameters)
            .build());
  }
  
  public void dragAndDropBy(int moveRightBy, int moveDownBy) {
    throw new UnsupportedOperationException("Not yet supported in Chrome");
  }

  public void dragAndDropOn(RenderedWebElement element) {
    throw new UnsupportedOperationException("Not yet supported in Chrome");
  }

  public Point getLocation() {
    return (Point) execute(DriverCommand.GET_ELEMENT_LOCATION).getValue();
  }

  public Dimension getSize() {
    return (Dimension) execute(DriverCommand.GET_ELEMENT_SIZE).getValue();
  }

  public String getValueOfCssProperty(String propertyName) {
    Map<String, ?> params = ImmutableMap.of("css", propertyName);
    return execute(DriverCommand.GET_ELEMENT_VALUE_OF_CSS_PROPERTY, params)
        .getValue().toString();
  }

  public boolean isDisplayed() {
    ChromeResponse r = execute(DriverCommand.IS_ELEMENT_DISPLAYED);
    return (Boolean)r.getValue();
  }

  public void clear() {
    execute(DriverCommand.CLEAR_ELEMENT);
  }

  public void click() {
    execute(DriverCommand.CLICK_ELEMENT);
  }

  public WebElement findElement(By by) {
    return by.findElement(this);
  }

  public List<WebElement> findElements(By by) {
    return by.findElements(this);
  }

  public String getAttribute(String name) {
    Object value = execute(DriverCommand.GET_ELEMENT_ATTRIBUTE,
        ImmutableMap.of("attribute", name)).getValue();
    return (value == null) ? null : value.toString();
  }

  public String getTagName() {
    return execute(DriverCommand.GET_ELEMENT_TAG_NAME).getValue().toString();
  }

  public String getText() {
    return execute(DriverCommand.GET_ELEMENT_TEXT).getValue().toString();
  }

  public String getValue() {
    return execute(DriverCommand.GET_ELEMENT_VALUE).getValue().toString();
  }

  public boolean isEnabled() {
    return Boolean.parseBoolean(execute(DriverCommand.IS_ELEMENT_ENABLED).getValue().toString());
  }

  public boolean isSelected() {
    return Boolean.parseBoolean(execute(DriverCommand.IS_ELEMENT_SELECTED)
        .getValue().toString());
  }

  public void sendKeys(CharSequence... keysToSend) {
    StringBuilder builder = new StringBuilder();
    for (CharSequence seq : keysToSend) {
      builder.append(seq);
    }
    execute(DriverCommand.SEND_KEYS_TO_ELEMENT, ImmutableMap.of("keys", builder.toString()));
  }

  public void setSelected() {
    execute(DriverCommand.SET_ELEMENT_SELECTED);
  }

  public void submit() {
    execute(DriverCommand.SUBMIT_ELEMENT);
  }

  public boolean toggle() {
    return Boolean.parseBoolean(execute(DriverCommand.TOGGLE_ELEMENT)
        .getValue().toString());
  }

  public Point getLocationOnScreenOnceScrolledIntoView() {
    return (Point) execute(DriverCommand.GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW)
        .getValue();
  }

  private WebElement findElementInternal(String using, String value) {
    ChromeResponse response = parent.execute(
        DriverCommand.FIND_CHILD_ELEMENT,
        ImmutableMap.of("id", elementId,
                        "using", using,
                        "value", value));
    return parent.getElementFrom(response);
  }

  private List<WebElement> findElementsInternal(String using, String value) {
    ChromeResponse response = parent.execute(
        DriverCommand.FIND_CHILD_ELEMENTS,
        ImmutableMap.of("id", elementId,
                        "using", using,
                        "value", value));
    return parent.getElementsFrom(response);
  }

  public WebElement findElementByXPath(String using) {
    return findElementInternal("xpath", using);
  }

  public List<WebElement> findElementsByXPath(String using) {
    return findElementsInternal("xpath", using);
  }

  public WebElement findElementByLinkText(String using) {
    return findElementInternal("link text", using);
  }

  public WebElement findElementByPartialLinkText(String using) {
    return findElementInternal("partial link text", using);
  }

  public List<WebElement> findElementsByLinkText(String using) {
    return findElementsInternal("link text", using);
  }

  public List<WebElement> findElementsByPartialLinkText(String using) {
    return findElementsInternal("partial link text", using);
  }

  public WebElement findElementById(String using) {
    return findElementInternal("id", using);
  }

  public List<WebElement> findElementsById(String using) {
    return findElementsInternal("id", using);
  }

  public WebElement findElementByName(String using) {
    return findElementInternal("name", using);
  }

  public List<WebElement> findElementsByName(String using) {
    return findElementsInternal("name", using);
  }

  public WebElement findElementByTagName(String using) {
    return findElementInternal("tag name", using);
  }

  public List<WebElement> findElementsByTagName(String using) {
    return findElementsInternal("tag name", using);
  }

  public WebElement findElementByClassName(String using) {
    return findElementInternal("class name", using);
  }

  public List<WebElement> findElementsByClassName(String using) {
    return findElementsInternal("class name", using);
  }

  public void hover() {
    //Relies on the user not moving the mouse after the hover moves it into place 
    execute(DriverCommand.HOVER_OVER_ELEMENT);
  }

  @Override
  public int hashCode() {
    return elementId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof WebElement)) {
      return false;
    }

    WebElement other = (WebElement) obj;
    if (other instanceof WrapsElement) {
      other = ((WrapsElement) obj).getWrappedElement();
    }

    if (!(other instanceof ChromeWebElement)) {
      return false;
    }

    return elementId.equals(((ChromeWebElement)other).elementId);
  }
}
