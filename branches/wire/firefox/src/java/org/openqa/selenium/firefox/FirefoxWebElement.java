/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.openqa.selenium.firefox;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.DriverCommand;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Map;

public class FirefoxWebElement implements RenderedWebElement, Locatable, 
        FindsByXPath, FindsByLinkText, FindsById, FindsByCssSelector,
    FindsByName, FindsByTagName, FindsByClassName {
    private final FirefoxDriver parent;
    private final String elementId;

    public FirefoxWebElement(FirefoxDriver parent, String elementId) {
        this.parent = parent;
        this.elementId = elementId;
    }

    public void click() {
      sendMessage(UnsupportedOperationException.class, DriverCommand.CLICK_ELEMENT);
    }

    public void hover() {
      sendMessage(WebDriverException.class, DriverCommand.HOVER_OVER_ELEMENT);
    }

    public void submit() {
        sendMessage(WebDriverException.class, DriverCommand.SUBMIT_ELEMENT);
    }

    public String getValue() {
        try {
          return sendMessage(WebDriverException.class, DriverCommand.GET_ELEMENT_VALUE);
        } catch (WebDriverException e) {
            return null;
        }
    }

    public void clear() {
    	sendMessage(UnsupportedOperationException.class, DriverCommand.CLEAR_ELEMENT);
    }

    public void sendKeys(CharSequence... value) {
        sendMessage(UnsupportedOperationException.class, DriverCommand.SEND_KEYS_TO_ELEMENT,
            ImmutableMap.of("value", value));
    }

    public String getTagName() {
        String name = sendMessage(WebDriverException.class, DriverCommand.GET_ELEMENT_TAG_NAME);
        return name;
    }

  public String getAttribute(String name) {
        return sendMessage(WebDriverException.class, DriverCommand.GET_ELEMENT_ATTRIBUTE,
            ImmutableMap.of("name", name));
    }

    public boolean toggle() {
        sendMessage(UnsupportedOperationException.class, DriverCommand.TOGGLE_ELEMENT);
        return isSelected();
    }

    public boolean isSelected() {
        String value = sendMessage(WebDriverException.class, DriverCommand.IS_ELEMENT_SELECTED);
        return Boolean.parseBoolean(value);
    }

    public void setSelected() {
        sendMessage(UnsupportedOperationException.class, DriverCommand.SET_ELEMENT_SELECTED);
    }

    public boolean isEnabled() {
        String value = getAttribute("disabled");
        return !Boolean.parseBoolean(value);
    }

    public String getText() {
        return sendMessage(WebDriverException.class, DriverCommand.GET_ELEMENT_TEXT);
    }

  public boolean isDisplayed() {
    return Boolean.parseBoolean(sendMessage(WebDriverException.class,
        DriverCommand.IS_ELEMENT_DISPLAYED));
    }

    public Point getLocation() {
        @SuppressWarnings("unchecked")
        Map<String, Number> result = (Map<String, Number>) executeCommand(WebDriverException.class,
            DriverCommand.GET_ELEMENT_LOCATION);
        return new Point(result.get("x").intValue(), result.get("y").intValue());
    }

    public Dimension getSize() {
        @SuppressWarnings("unchecked")
        Map<String, Number> result = (Map<String, Number>) executeCommand(WebDriverException.class,
            DriverCommand.GET_ELEMENT_SIZE);
        return new Dimension(result.get("width").intValue(), result.get("height").intValue());
    }

    public void dragAndDropBy(int moveRight, int moveDown) {
        sendMessage(UnsupportedOperationException.class, DriverCommand.DRAG_ELEMENT,
            ImmutableMap.of("x", moveRight, "y", moveDown));
    }

    public void dragAndDropOn(RenderedWebElement element) {
        Point currentLocation = getLocation();
        Point destination = element.getLocation();
        dragAndDropBy(destination.x - currentLocation.x, destination.y - currentLocation.y);
    }

    public WebElement findElement(By by) {
        return by.findElement(this);
    }

    public List<WebElement> findElements(By by) {
        return by.findElements(this);
    }

    public WebElement findElementByXPath(String xpath) {
      return findChildElement("xpath", xpath);
    }

    public List<WebElement> findElementsByXPath(String xpath) {
      return findChildElements("xpath", xpath);
    }

    public WebElement findElementByLinkText(String linkText) {
      return findChildElement("link text", linkText);
    }

    public List<WebElement> findElementsByLinkText(String linkText) {
      return findChildElements("link text", linkText);
    }

    public WebElement findElementByPartialLinkText(String text) {
      return findChildElement("partial link text", text);
    }

    public List<WebElement> findElementsByPartialLinkText(String text) {
      return findChildElements("partial link text", text);
    }

    public WebElement findElementById(String id) {
      return findChildElement("id", id);
    }

    public List<WebElement> findElementsById(String id) {
      return findChildElements("id", id);
    }

    public WebElement findElementByName(String name) {
      return findChildElement("name", name);
    }

    public List<WebElement> findElementsByName(String name) {
      return findChildElements("name", name);
    }

    public WebElement findElementByTagName(String tagName) {
      return findChildElement("tag name", tagName);
    }
    
    public List<WebElement> findElementsByTagName(String tagName) {
      return findChildElements("tag name", tagName);
    }
    
    public WebElement findElementByClassName(String className) {
      return findChildElement("class name", className);
    }

    public List<WebElement> findElementsByClassName(String className) {
      return findChildElements("class name", className);
    }

    public WebElement findElementByCssSelector(String using) {
      return findChildElement("css selector", using);
    }

    public List<WebElement> findElementsByCssSelector(String using) {
      return findChildElements("css selector", using);
    }

  private WebElement findChildElement(String using, String value) {
      String id = sendMessage(NoSuchElementException.class,
          DriverCommand.FIND_CHILD_ELEMENT, buildSearchParamsMap(using, value));
      return new FirefoxWebElement(parent, id);
    }

    private List<WebElement> findChildElements(String using, String value) {
      @SuppressWarnings("unchecked")
      List<String> ids = (List<String>) executeCommand(WebDriverException.class,
          DriverCommand.FIND_CHILD_ELEMENTS, buildSearchParamsMap(using, value));

      List<WebElement> elements = Lists.newArrayListWithExpectedSize(ids.size());

      for (String id : ids) {
        elements.add(new FirefoxWebElement(parent, id));
      }
      return elements;
    }

    private Map<String, String> buildSearchParamsMap(String using, String value) {
      return ImmutableMap.of("using", using, "value", value);
    }

    public String getValueOfCssProperty(String propertyName) {
      return sendMessage(WebDriverException.class, DriverCommand.GET_ELEMENT_VALUE_OF_CSS_PROPERTY,
          ImmutableMap.of("propertyName", propertyName));
    }

    private String sendMessage(Class<? extends RuntimeException> throwOnFailure,
                               DriverCommand methodName) {
      return sendMessage(throwOnFailure, methodName, ImmutableMap.<String, Object>of());
    }

    private String sendMessage(Class<? extends RuntimeException> throwOnFailure,
                               DriverCommand methodName, Map<String, ?> parameters) {
      Object result = executeCommand(throwOnFailure, methodName, parameters);
      return result == null ? null : String.valueOf(result);
    }

    private Object executeCommand(Class<? extends RuntimeException> throwOnFailure,
                                  DriverCommand methodName, Map<String, ?> parameters) {
      return parent.executeCommand(throwOnFailure, new Command(parent.sessionId, methodName,
          ImmutableMap.<String, Object>builder()
              .putAll(parameters)
              .put("id", elementId)
              .build()));
    }

    private Object executeCommand(Class<? extends RuntimeException> throwOnFailure,
                                  DriverCommand methodName) {
      return executeCommand(throwOnFailure, methodName, ImmutableMap.<String, Object>of());
    }

    public String getElementId() {
        return elementId;
    }

    public Point getLocationOnScreenOnceScrolledIntoView() {
            @SuppressWarnings("unchecked")
            Map<String, Number> mapped = (Map<String, Number>) executeCommand(WebDriverException.class,
                DriverCommand.GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW);

            return new Point(mapped.get("x").intValue(), mapped.get("y").intValue());
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

    if (!(other instanceof FirefoxWebElement)) {
      return false;
    }
    return elementId.equals(((FirefoxWebElement)other).elementId);
  }

  @Override
  public int hashCode() {
    return elementId.hashCode();
  }
}
