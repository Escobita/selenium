package org.openqa.selenium.support.interactions;

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

  public void addAction(Action action) {
    actionsList.add(action);
  }

  public int getNumberOfActions() {
    return actionsList.size();
  }
}
