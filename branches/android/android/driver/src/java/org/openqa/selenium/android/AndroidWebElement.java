/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.
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

/*
 * Copyright 2007 ThoughtWorks, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.openqa.selenium.android;

import android.util.Log;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

import java.util.ArrayList;
import java.util.List;

public class AndroidWebElement implements WebElement,
    FindsById, FindsByLinkText, FindsByXPath, FindsByTagName, SearchContext {

  private String toString;
  private AndroidDriver mDriver;
  private By mBy;
  private Element mElement;
  
  public AndroidWebElement(AndroidDriver driver, By by) {
    mDriver = driver;
    mBy = by;
  }

  public void click() {
    // TODO(abergman): Implement
  }

  public void submit() {
    // TODO(abergman): Implement
  }

  public String getValue() {
    // Uses extractor to fetch element from the current page source
    this.findElement(mBy);
    if (mElement == null)
      return "";
    
    if (mElement.getStartTag().getName().equalsIgnoreCase("textarea")) {
      return mElement.getTextExtractor().toString();
    }
    String value = mElement.getAttributeValue("value");
    return (value == null) ? "" : value;
  }

  public void clear() {
    // TODO(abergman): implement
  }

  public void sendKeys(CharSequence... value) {
    // TODO(abergman): implement
  }

  @Deprecated
  public String getElementName() {
    return mElement.getAttributeValue("name");
  }

  public String getTagName() {
    return mElement.getStartTag().getName();
  }

  public String getAttribute(String name) {
    return mElement.getAttributeValue(name);
  }

  public boolean toggle() {
    // TODO(abergman): implement
    return false;
  }

  public boolean isSelected() {
    // TODO(abergman): implement
    return false;
  }

  public void setSelected() {
    // TODO(abergman): implement
  }

  public boolean isEnabled() {
    // TODO(abergman): implement
    return false;
  }

  public String getText() {
    this.findElement(mBy);
    if (mElement == null || mElement.getTextExtractor() == null) {
      Log.e("AndroidWebElement:getText", "Element is Null!");
      return "";
    }
    return mElement.getTextExtractor().toString();
  }

  public List<WebElement> getElementsByTagName(String tagName) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElement(By by) {
    return by.findElement(this);
  }

  public List<WebElement> findElements(By by) {
    return by.findElements(this);
  }

  public WebElement findElementById(String id) {
    // Updates mElement according to the current extracted page source
    mElement = mDriver.getExtractor().getElementById(id);
    return this;
  }

  public List<WebElement> findElementsById(String id) {
      return findElementsByXPath(".//*[@id = \\'" + id + "\\']");
  }

  public WebElement findElementByXPath(String xpathExpr) {
    mElement = mDriver.getExtractor().getElementByXPath(xpathExpr);
    return this;
  }

  public List<WebElement> findElementsByXPath(String xpathExpr) {
    List<Element> elements = mDriver.getExtractor().getElementsByXpath(xpathExpr);
    AndroidWebElement androidElement = null;
    List<WebElement> androidElements = new ArrayList<WebElement>();
    for (Element el : elements) {
      androidElement = new AndroidWebElement(mDriver, By.xpath(xpathExpr));
      androidElement.mElement = el;
      androidElements.add(androidElement);
    }
    return androidElements;
  }

  public WebElement findElementByLinkText(String using) {
    mElement = mDriver.getExtractor().getElementByLinkText(using);
    return this;
  }

  public List<WebElement> findElementsByLinkText(String using) {
    List<Element> elements = mDriver.getExtractor().getElementsByLinkText(using);
    List<WebElement> result = new ArrayList<WebElement>();
    AndroidWebElement androidElement = null;
    for (Element el : elements) {
      androidElement = new AndroidWebElement(mDriver, By.linkText(using));
      androidElement.mElement = el;
      result.add(androidElement);
    }
    return result;
  }

  public WebElement findElementByPartialLinkText(String using) {
    mElement = mDriver.getExtractor().getElementByPartialLinkText(using);
    return this;
  }

  public List<WebElement> findElementsByPartialLinkText(String using) {
    List<Element> elements = mDriver.getExtractor().getElementsByPartialLinkText(using);
    List<WebElement> result = new ArrayList<WebElement>();
    AndroidWebElement androidElement = null;
    for (Element el : elements) {
      androidElement = new AndroidWebElement(mDriver, By.linkText(using));
      androidElement.mElement = el;
      result.add(androidElement);
    }
    return result;
  }

  public WebElement findElementByTagName(String name) {
    mElement = mDriver.getExtractor().getElementByTagName(name);
    return this;
  }

  public List<WebElement> findElementsByTagName(String using) {
    List<Element> elements = mDriver.getExtractor().getElementsByTagName(using);
    List<WebElement> result = new ArrayList<WebElement>();
    AndroidWebElement androidElement = null;
    for (Element el : elements) {
      androidElement = new AndroidWebElement(mDriver, By.linkText(using));
      androidElement.mElement = el;
      result.add(androidElement);
    }
    return result;
  }
  
  public WebElement findElementByName(String using) {
    mElement = mDriver.getExtractor().getElementByName(using);
    return this;
  }

  public List<WebElement> findElementsByName(String using) {
    List<Element> elements = mDriver.getExtractor().getElementsByName(using);
    List<WebElement> result = new ArrayList<WebElement>();
    AndroidWebElement androidElement = null;
    for (Element el : elements) {
      androidElement = new AndroidWebElement(mDriver, By.linkText(using));
      androidElement.mElement = el;
      result.add(androidElement);
    }
    return result;
  }
  
  @Override
  public String toString() {
      if (toString == null) {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(mElement.getStartTag().getName());
        Attributes attributes = mElement.getAttributes();
        int n = attributes.length();
        for (int i = 0; i < n; ++i) {
          Attribute a = attributes.get(i);
          sb.append(' ').append(a.getName()).append("=\"")
              .append(a.getValue().replace("\"", "&quot;")).append("\"");
        }
        if (!mElement.getChildElements().isEmpty()) {
          sb.append('>');
        } else {
          sb.append(" />");
        }
        toString = sb.toString();
      }
      return toString;
  }
}
