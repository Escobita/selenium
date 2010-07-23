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

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebElement;

/**
 * Unit test for all simple keyboard actions.
 *
 * @author eran.mes@gmail.com (Eran Mes)
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
