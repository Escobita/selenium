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
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Unit test for all simple keyboard actions.
 *
 * @author eran.mes@gmail.com (Eran Mes)
 */
public class TestIndividualKeyboardActions extends MockObjectTestCase {
  private Keyboard dummyKeyboard;
  private WebElement dummyElement;

  public void setUp() {
    dummyKeyboard = mock(Keyboard.class);
    dummyElement = mock(WebElement.class);
  }

  public void testKeyDownAction() {
    final Keys keyToPress = Keys.SHIFT;

    checking(new Expectations() {{
      one(dummyKeyboard).pressKey(dummyElement, keyToPress);
    }});
   
    KeyDownAction keyDown = new KeyDownAction(dummyKeyboard, dummyElement, keyToPress);
    keyDown.perform();
  }

  public void testKeyUpAction() {
    final Keys keyToRelease = Keys.CONTROL;

    checking(new Expectations() {{
      one(dummyKeyboard).releaseKey(dummyElement, keyToRelease);
    }});

    KeyUpAction keyUp = new KeyUpAction(dummyKeyboard, dummyElement, keyToRelease);
    keyUp.perform();
  }

  public void testSendKeysAction() {
    final String keysToSend = "hello";

    checking(new Expectations() {{
      one(dummyKeyboard).sendKeys(dummyElement, keysToSend);
    }});

    SendKeysAction sendKeys = new SendKeysAction(dummyKeyboard, dummyElement, keysToSend);
    sendKeys.perform();

  }

  public void testKeyDownActionFailsOnNonModifier() {
    final Keys keyToPress = Keys.BACK_SPACE;

    try {
      KeyDownAction keyDown = new KeyDownAction(dummyKeyboard, dummyElement, keyToPress);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("modifier keys"));
    }
  }

  public void testKeyDownActionWithoutAnElement() {
    final Keys keyToPress = Keys.SHIFT;

    checking(new Expectations() {{
      one(dummyKeyboard).pressKey(null, keyToPress);
    }});

    KeyDownAction downAction = new KeyDownAction(dummyKeyboard, keyToPress);
    downAction.perform();
  }

  public void testKeyUpActionWithoutAnElement() {
    final Keys keyToRelease = Keys.SHIFT;

    checking(new Expectations() {{
      one(dummyKeyboard).releaseKey(null, keyToRelease);
    }});

    KeyUpAction upAction = new KeyUpAction(dummyKeyboard, keyToRelease);
    upAction.perform();
  }

  public void testSendKeysActionWithoutAnElement() {
    final String keysToSend = "hello";

    checking(new Expectations() {{
      one(dummyKeyboard).sendKeys(null, keysToSend);
    }});

    SendKeysAction sendKeys = new SendKeysAction(dummyKeyboard, keysToSend);
    sendKeys.perform();
  }

}
