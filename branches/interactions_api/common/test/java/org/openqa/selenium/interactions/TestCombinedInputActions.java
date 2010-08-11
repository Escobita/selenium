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

package org.openqa.selenium.interactions;

import java.util.List;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

/**
 * @author eran.mes@google.com (Eran Mes)
 */
public class TestCombinedInputActions extends AbstractDriverTestCase {

  @JavascriptEnabled
  public void testClickingOnFormElements() {
    driver.get(pages.formSelectionPage);

    List<WebElement> options = driver.findElements(By.tagName("option"));

    Keyboard keyboard = ((HasInputDevices) driver).getKeyboard();
    Mouse mouse = ((HasInputDevices) driver).getMouse();

    CompositeAction selectThreeOptions = new CompositeAction();
    selectThreeOptions.addAction(new ClickAction(mouse, options.get(1)))
        .addAction(new KeyDownAction(keyboard, Keys.SHIFT))
        .addAction(new ClickAction(mouse, options.get(2)))
        .addAction(new ClickAction(mouse, options.get(3)))
        .addAction(new KeyUpAction(keyboard, Keys.SHIFT));

    selectThreeOptions.perform();

    WebElement showButton = driver.findElement(By.name("showselected"));
    showButton.click();

    WebElement resultElement = driver.findElement(By.id("result"));
    assertEquals("Should have picked the last three options.", "roquefort parmigiano cheddar",
        resultElement.getText());
  }
}
