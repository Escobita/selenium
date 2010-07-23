/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.

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

package org.openqa.selenium.htmlunit;

import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

/**
 * Implements mouse operations using the HtmlUnit WebDriver.
 *
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class HtmlUnitMouse implements Mouse {
  public void click(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.click();
  }

  public void doubleClick(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.doubleClick();    
  }

  public void mouseDown(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseDown();
  }

  public void mouseUp(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseUp();
  }

  public void mouseMove(WebElement toElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) toElement;
    htmlElem.moveToHere();
  }


  public void mouseMove(long xOffset, long yOffset) {
    throw new UnsupportedOperationException("Moving to arbitrary X,Y coordinates not supported.");
  }

  public void contextClick(WebElement onElement) {
    HtmlUnitWebElement htmlElem = (HtmlUnitWebElement) onElement;
    htmlElem.mouseContextClick();
  }
}
