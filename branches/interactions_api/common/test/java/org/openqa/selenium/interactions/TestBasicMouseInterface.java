package org.openqa.selenium.interactions;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.JavascriptEnabled;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Tests operations that involve mouse and keyboard.
 */
public class TestBasicMouseInterface extends AbstractDriverTestCase {
  @JavascriptEnabled
  public void testDraggingElementWithMouse() {
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

    System.out.println(driver.getPageSource());
    assertEquals("Nothing happened. DragOut", dragReporter.getText());
    drop.perform();

    System.out.println(driver.getPageSource());
    assertEquals(6, dragInto.findElements(By.tagName("li")).size());
    //TODO: This is failing under HtmlUnit. Follow up.
    //assertEquals("Nothing happened. DragOut DropIn RightItem 3", dragReporter.getText());
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
