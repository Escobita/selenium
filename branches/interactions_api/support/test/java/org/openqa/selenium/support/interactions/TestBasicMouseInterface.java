package org.openqa.selenium.support.interactions;

import java.util.List;

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

  /*
  public void testDraggingElementWithMouseRaw() {
    driver.get("http://jqueryui.com/demos/sortable/connect-lists.html");
    doSleep(3);
    WebElement sortable2 = driver.findElement(By.id("sortable2"));
    List<WebElement> sortable2Items = sortable2.findElements(By.tagName("li"));
    WebElement toDrag = sortable2Items.get(2);

    WebElement dragInto = driver.findElement(By.id("sortable1"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    MouseClickAndHoldAction holdDrag = new MouseClickAndHoldAction(mouse, toDrag);

    MouseMoveAction move = new MouseMoveAction(mouse, toDrag, dragInto);

    MouseReleaseAction drop = new MouseReleaseAction(mouse, dragInto);


    holdDrag.perform();
    move.perform();

    doSleep(2);
    System.out.println(driver.getPageSource());

    drop.perform();
    doSleep(2);
    System.out.println(driver.getPageSource());

    fail();    
  }
  */
  
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
    System.out.println(driver.getPageSource());
    assertEquals("Nothing happened. DragOut", dragReporter.getText());

    drop.perform();
    doSleep(2);

    System.out.println(driver.getPageSource());
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

  public void testDoubleClick() {
    driver.get(pages.javascriptPage);

    WebElement toDoubleClick = driver.findElement(By.id("doubleClickField"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    DoubleClickAction dblClick = new DoubleClickAction(mouse, toDoubleClick);

    dblClick.perform();
    assertEquals("Value should change to DoubleClicked.", "DoubleClicked",
        toDoubleClick.getValue());
  }

  public void testContextClick() {
    driver.get(pages.javascriptPage);

    WebElement toContextClick = driver.findElement(By.id("doubleClickField"));

    Mouse mouse = ((HasInputDevices) driver).getMouse();
    ContextClickAction contextClick = new ContextClickAction (mouse, toContextClick );

    contextClick.perform();
    assertEquals("Value should change to ContextClicked.", "ContextClicked",
        toContextClick.getValue());
  }


  private void doSleep(int nseconds) {
    try {
      Thread.sleep(nseconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
