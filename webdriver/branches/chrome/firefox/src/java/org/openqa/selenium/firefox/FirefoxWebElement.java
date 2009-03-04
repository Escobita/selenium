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

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.Locatable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FirefoxWebElement implements RenderedWebElement, Locatable, 
        FindsByXPath, FindsByLinkText, FindsById, FindsByName, FindsByTagName, FindsByClassName, SearchContext {
    private final FirefoxDriver parent;
    private final String elementId;

    public FirefoxWebElement(FirefoxDriver parent, String elementId) {
        this.parent = parent;
        this.elementId = elementId;
    }

    public void click() {
      try {
        sendMessage(UnsupportedOperationException.class, "click");
      } catch (UnsupportedOperationException e) {
        // Looks like we need to do the decent thing and rethink error messages in the firefox driver
        if ("Unable to click on an obsolete element".equals(e.getMessage())) {
          throw new WebDriverException(e.getMessage());
        }
        throw e;
      }
    }

    public void submit() {
        sendMessage(WebDriverException.class, "submitElement");
    }

    public String getValue() {
        try {
            String toReturn = sendMessage(WebDriverException.class, "getElementValue");
            return toReturn.replace("\n", Platform.getCurrent().getLineEnding());
        } catch (WebDriverException e) {
            return null;
        }
    }

    public void clear() {
    	sendMessage(UnsupportedOperationException.class, "clear");
    }

    public void sendKeys(CharSequence... value) {
    	StringBuilder builder = new StringBuilder();
    	for (CharSequence seq : value) {
    		builder.append(seq);
    	}
        sendMessage(UnsupportedOperationException.class, "sendKeys", builder.toString());
    }

    public String getElementName() {
        String name = sendMessage(WebDriverException.class, "getElementName");
        return name;
    }

    public String getAttribute(String name) {
        try {
            return sendMessage(WebDriverException.class, "getElementAttribute", name);
        } catch (WebDriverException e) {
            return null;
        }
    }

    public boolean toggle() {
        sendMessage(UnsupportedOperationException.class, "toggleElement");
        return isSelected();
    }

    public boolean isSelected() {
        String value = sendMessage(WebDriverException.class, "getElementSelected");
        return Boolean.parseBoolean(value);
    }

    public void setSelected() {
        sendMessage(UnsupportedOperationException.class, "setElementSelected");
    }

    public boolean isEnabled() {
        String value = getAttribute("disabled");
        return !Boolean.parseBoolean(value);
    }

    public String getText() {
        String toReturn = sendMessage(WebDriverException.class, "getElementText");
        return toReturn.replace("\n", Platform.getCurrent().getLineEnding());
    }

  public List<WebElement> getElementsByTagName(String tagName) {
        String response = sendMessage(WebDriverException.class, "getElementChildren", tagName);

        return getElementsFromIndices(response);
    }

  public boolean isDisplayed() {
    return Boolean.parseBoolean(sendMessage(WebDriverException.class, "isElementDisplayed"));
    }

    public Point getLocation() {
        String result = sendMessage(WebDriverException.class, "getElementLocation");

        String[] parts = result.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());

        return new Point(x, y);
    }

    public Dimension getSize() {
        String result = sendMessage(WebDriverException.class, "getElementSize");

        String[] parts = result.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());

        return new Dimension(x, y);
    }

    public void dragAndDropBy(int moveRight, int moveDown) {
        sendMessage(UnsupportedOperationException.class, "dragAndDrop", moveRight, moveDown);
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
        List<WebElement> elements = findElementsByXPath(xpath);
        if (elements.size() == 0) {
            throw new NoSuchElementException(
                    "Unable to find element with xpath " + xpath);
        }
        return elements.get(0);
    }

    public List<WebElement> findElementsByXPath(String xpath) {
        String indices = sendMessage(WebDriverException.class,
                "findElementsByXPath", xpath);
        return getElementsFromIndices(indices);
    }

    public WebElement findElementByLinkText(String linkText) {
        List<WebElement> elements = findElementsByLinkText(linkText);
        if (elements.size() == 0) {
            throw new NoSuchElementException(
                    "Unable to find element with linkText" + linkText);
        }
        return elements.get(0);
    }

    public List<WebElement> findElementsByLinkText(String linkText) {
        String indices = sendMessage(WebDriverException.class,
                "findElementsByLinkText", linkText);
        return getElementsFromIndices(indices);
    }

    public WebElement findElementByPartialLinkText(String using) {
      String id = sendMessage(WebDriverException.class,
                "findElementByPartialLinkText", using);
      return new FirefoxWebElement(parent, id);
    }

    public List<WebElement> findElementsByPartialLinkText(String using) {
        String indices = sendMessage(WebDriverException.class,
                "findElementsByPartialLinkText", using);
        return getElementsFromIndices(indices);
    }


    private List<WebElement> getElementsFromIndices(String indices) {
        List<WebElement> elements = new ArrayList<WebElement>();

        if (indices.length() == 0)
            return elements;

        String[] ids = indices.split(",");
        for (String id : ids) {
            elements.add(new FirefoxWebElement(parent, id));
        }
        return elements;
    }

    public WebElement findElementById(String id) {
    	String response = sendMessage(WebDriverException.class, "findElementById", id);
    	if (response.equals("-1"))
    		throw new NoSuchElementException("Unable to find element with id" + id);
    	return new FirefoxWebElement(parent, response);
    }

    public List<WebElement> findElementsById(String id) {
    	return findElementsByXPath(".//*[@id = '" + id + "']");
    }

    public WebElement findElementByName(String name) {
        return findElementByXPath(".//*[@name = '" + name + "']");
    }

    public List<WebElement> findElementsByName(String name) {
        return findElementsByXPath(".//*[@name = '" + name + "']");
    }

    public WebElement findElementByClassName(String using) {
        List<WebElement> elements = findElementsByClassName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException(
                    "Unable to find element by class name " + using);
        }
        return elements.get(0);
    }

    public WebElement findElementByTagName(String using) {
      String response = sendMessage(NoSuchElementException.class, "findElementByTagName", using);
      return new FirefoxWebElement(parent, response);
    }
    
    public List<WebElement> findElementsByTagName(String using) {
      String indices = sendMessage(WebDriverException.class, "findElementsByTagName", using);
      return getElementsFromIndices(indices);
    }
    
    public List<WebElement> findElementsByClassName(String using) {
    	String indices = sendMessage(WebDriverException.class, "findChildElementsByClassName", using);
        return getElementsFromIndices(indices);
    }

    public String getValueOfCssProperty(String propertyName) {
    	return sendMessage(WebDriverException.class,"getElementCssProperty", propertyName);
    }

    private String sendMessage(Class<? extends RuntimeException> throwOnFailure, String methodName, Object... parameters) {
        return parent.sendMessage(throwOnFailure, new Command(parent.context, elementId, methodName, parameters));
    }

    public String getElementId() {
        return elementId;
    }

    public Point getLocationOnScreenOnceScrolledIntoView() {
        String json = sendMessage(WebDriverException.class, "getLocationOnceScrolledIntoView");
        if (json == null) {
            return null;
        }

        try {
            JSONObject mapped = new JSONObject(json);

            return new Point(mapped.getInt("x"), mapped.getInt("y"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
