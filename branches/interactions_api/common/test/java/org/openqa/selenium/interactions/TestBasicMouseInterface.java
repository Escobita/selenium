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

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.Ignore;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;

/**
 * Tests operations that involve mouse and keyboard.
 *
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class TestBasicMouseInterface extends AbstractDriverTestCase {
  private void performDragAndDropWithMouse() {
    driver.get(pages.draggableLists);

    WebElement dragReporter = driver.findElement(By.id("dragging_reports"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    WebElement toDrag = driver.findElement(By.id("rightitem-3"));
    WebElement dragInto = driver.findElement(By.id("sortable1"));

    MouseClickAndHoldAction holdItem = new MouseClickAndHoldAction(mouse, toDrag);

    MouseMoveAction moveToSpecificItem = new MouseMoveAction(mouse,
        driver.findElement(By.id("leftitem-4")));

    MouseMoveAction moveToOtherList = new MouseMoveAction(mouse, dragInto);

    MouseReleaseAction drop = new MouseReleaseAction(mouse, dragInto);

    assertEquals("Nothing happened.", dragReporter.getText());

    holdItem.perform();
    moveToSpecificItem.perform();
    moveToOtherList.perform();

    assertEquals("Nothing happened. DragOut", dragReporter.getText());
    drop.perform();
  }

  @JavascriptEnabled
  public void testDraggingElementWithMouseMovesItToAnotherList() {
    performDragAndDropWithMouse();
    WebElement dragInto = driver.findElement(By.id("sortable1"));
    assertEquals(6, dragInto.findElements(By.tagName("li")).size());
  }

  @JavascriptEnabled
  @Ignore({HTMLUNIT})
  // This test is very similar to testDraggingElementWithMouse. The only
  // difference is that this test also verifies the correct events were fired.
  public void testDraggingElementWithMouseFiresEvents() {
    performDragAndDropWithMouse();
    WebElement dragReporter = driver.findElement(By.id("dragging_reports"));
    // This is failing under HtmlUnit. A bug was filed.
    assertEquals("Nothing happened. DragOut DropIn RightItem 3", dragReporter.getText());
  }


  private boolean isElementAvailable(WebDriver driver, By locator) {
    try {
      driver.findElement(locator);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @JavascriptEnabled
  public void testDragAndDrop() throws InterruptedException {
    driver.get(pages.droppableItems);

    long waitEndTime = System.currentTimeMillis() + 15000;

    while (!isElementAvailable(driver, By.id("draggable")) &&
           (System.currentTimeMillis() < waitEndTime)) {
      Thread.sleep(200);
    }

    if (!isElementAvailable(driver, By.id("draggable"))) {
      throw new RuntimeException("Could not find draggable element after 15 seconds.");
    }

    WebElement toDrag = driver.findElement(By.id("draggable"));
    WebElement dropInto = driver.findElement(By.id("droppable"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();

    MouseClickAndHoldAction holdDrag = new MouseClickAndHoldAction(mouse, toDrag);

    MouseMoveAction move = new MouseMoveAction(mouse, dropInto);

    MouseReleaseAction drop = new MouseReleaseAction(mouse, dropInto);

    holdDrag.perform();
    move.perform();
    drop.perform();

    dropInto = driver.findElement(By.id("droppable"));
    String text = dropInto.findElement(By.tagName("p")).getText();

    assertEquals("Dropped!", text);
  }

  @JavascriptEnabled
  public void testDoubleClick() {
    driver.get(pages.javascriptPage);

    WebElement toDoubleClick = driver.findElement(By.id("doubleClickField"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    DoubleClickAction dblClick = new DoubleClickAction(mouse, toDoubleClick);

    dblClick.perform();
    assertEquals("Value should change to DoubleClicked.", "DoubleClicked",
        toDoubleClick.getValue());
  }

  @JavascriptEnabled
  public void testContextClick() {
    driver.get(pages.javascriptPage);

    WebElement toContextClick = driver.findElement(By.id("doubleClickField"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    ContextClickAction contextClick = new ContextClickAction (mouse, toContextClick );

    contextClick.perform();
    assertEquals("Value should change to ContextClicked.", "ContextClicked",
        toContextClick.getValue());
  }
}
