package org.openqa.selenium.interactions;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * An action for aggregating actions and triggering all of them at the same time.
 */
public class CompositeAction implements Action {
  private List<Action> actionsList = Lists.newLinkedList();
  public void perform() {
    for (Action action : actionsList) {
      action.perform();
    }
  }

  public CompositeAction addAction(Action action) {
    actionsList.add(action);
    return this;
  }

  public int getNumberOfActions() {
    return actionsList.size();
  }
}
