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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

public class AndroidWebElement implements WebElement,
    FindsById, FindsByLinkText, FindsByXPath, FindsByTagName, SearchContext {

  private String toString;

  public void click() {
    // TODO(abergman): Implement
  }

  public void submit() {
    // TODO(abergman): Implement
  }

	public String getValue() {
    // TODO(abergman): implement
    return null;
  }

  public void clear() {
    // TODO(abergman): implement
  }

  public void sendKeys(CharSequence... value) {
    // TODO(abergman): implement
  }

  public String getElementName() {
    // TODO(abergman): implement
    return null;
  }

  public String getTagName() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getAttribute(String name) {
    // TODO(abergman): implement
    return null;
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
    // TODO(abergman): implement
    return null;
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
      return findElementByXPath(".//*[@id = '" + id + "']");
  }

  public List<WebElement> findElementsById(String id) {
      return findElementsByXPath(".//*[@id = '" + id + "']");
  }

  public WebElement findElementByXPath(String xpathExpr) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByXPath(String xpathExpr) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementByLinkText(String linkText) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByLinkText(String linkText) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementByPartialLinkText(String linkText) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByPartialLinkText(String linkText) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementByTagName(String name) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByTagName(String name) {
    // TODO(abergman): implement
    return null;
  }

  @Override
  public String toString() {
      if (toString == null) {
        // TODO(abergman): return meaningful description
      }
      return toString;
  }
}
