package org.openqa.selenium.support.interactions;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Keyboard;
import org.openqa.selenium.internal.Mouse;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests operations that involve mouse and keyboard.
 */
public class TestBasicMouseInterface extends AbstractDriverTestCase {
  public void testDraggingElementWithMouse() {
    driver.get(pages.draggableLists);

    WebElement dragReporter = driver.findElement(By.id("dragging_reports"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    WebElement toDrag = driver.findElement(By.id("rightitem-3"));
    WebElement dragInto = driver.findElement(By.id("sortable1")); 

    MouseClickAndHoldAction holdDrag = new MouseClickAndHoldAction(mouse, toDrag);

    MouseMoveAction move = new MouseMoveAction(mouse, toDrag, dragInto);
    
    MouseReleaseAction drop = new MouseReleaseAction(mouse, dragInto);

    assertEquals("Nothing happened.", dragReporter.getText());

    holdDrag.perform();
    move.perform();

    doSleep(2);
    assertEquals("Nothing happened. DragOut", dragReporter.getText());

    drop.perform();
    doSleep(2);

    assertEquals("Nothing happened. DragOut DropIn RightItem 3", dragReporter.getText());
  }

  public void testDragAndDrop() {
    driver.get(pages.droppableItems);

    WebDriverWait wait = new WebDriverWait(driver, 15);

    ExpectedCondition pageLoaded = new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        driver.findElement(By.id("draggable"));
        return true;
      }
    };

    wait.until(pageLoaded);

    WebElement toDrag = driver.findElement(By.id("draggable"));
    WebElement dropInto = driver.findElement(By.id("droppable"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();

    MouseClickAndHoldAction holdDrag = new MouseClickAndHoldAction(mouse, toDrag);

    MouseMoveAction move = new MouseMoveAction(mouse, toDrag, dropInto);

    MouseReleaseAction drop = new MouseReleaseAction(mouse, dropInto);

    holdDrag.perform();
    move.perform();
    drop.perform();

    dropInto = driver.findElement(By.id("droppable"));
    String text = dropInto.findElement(By.tagName("p")).getText();

    assertEquals("Dropped!", text);
  }

  private void doSleep(int nseconds) {
    try {
      Thread.sleep(nseconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
