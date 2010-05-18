package org.openqa.selenium.support.interactions;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

/**
 * Tests the CompositeAction class
 */
public class TestCompositeAction extends MockObjectTestCase {
  public void testAddingActions() {
    CompositeAction sequence = new CompositeAction();
    final Action dummyAction1 = mock(Action.class);
    final Action dummyAction2 = mock(Action.class, "dummy2");
    final Action dummyAction3 = mock(Action.class, "dummy3");

    sequence.addAction(dummyAction1)
        .addAction(dummyAction2)
        .addAction(dummyAction3);
    
    assertEquals(3, sequence.getNumberOfActions());
  }

  public void testInvokingActions() {
    CompositeAction sequence = new CompositeAction();
    final Action dummyAction1 = mock(Action.class);
    final Action dummyAction2 = mock(Action.class, "dummy2");
    final Action dummyAction3 = mock(Action.class, "dummy3");

    sequence.addAction(dummyAction1);
    sequence.addAction(dummyAction2);
    sequence.addAction(dummyAction3);

    checking(new Expectations() {{
      one(dummyAction1).perform();
      one(dummyAction2).perform();
      one(dummyAction3).perform();
    }});
    
    sequence.perform();
  }


}
