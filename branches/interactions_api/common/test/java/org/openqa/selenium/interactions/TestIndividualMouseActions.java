package org.openqa.selenium.interactions;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

/**
 * Unit test for all simple keyboard actions.
 */
public class TestIndividualMouseActions extends MockObjectTestCase {
  private Mouse dummyMouse;
  private WebElement dummyElement;

  public void setUp() {
    dummyMouse = mock(Mouse.class);
    dummyElement = mock(WebElement.class);
  }

  public void testMouseClickAndHoldAction() {
    checking(new Expectations() {{
      one(dummyMouse).mouseDown(dummyElement);
    }});

    MouseClickAndHoldAction action = new MouseClickAndHoldAction(dummyMouse, dummyElement);
    action.perform();
  }

  public void testMouseReleaseAction() {
    checking(new Expectations() {{
      one(dummyMouse).mouseUp(dummyElement);
    }});

    MouseReleaseAction action = new MouseReleaseAction(dummyMouse, dummyElement);
    action.perform();
  }


  public void testMouseClickAction() {
    checking(new Expectations() {{
      one(dummyMouse).click(dummyElement);
    }});

    ClickAction action = new ClickAction(dummyMouse, dummyElement);
    action.perform();
  }

  public void testMouseDoubleClickAction() {
    checking(new Expectations() {{
      one(dummyMouse).doubleClick(dummyElement);
    }});

    DoubleClickAction action = new DoubleClickAction(dummyMouse, dummyElement);
    action.perform();
  }

  public void testMouseMoveAction() {
    checking(new Expectations() {{
      one(dummyMouse).mouseMove(dummyElement);
    }});

    MouseMoveAction action = new MouseMoveAction(dummyMouse, dummyElement);
    action.perform();
  }

}
