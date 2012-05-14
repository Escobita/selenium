/*
Copyright 2007-2011 WebDriver committers

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

package org.openqa.selenium.remote.server.handler.interactions;

import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.remote.server.JsonParametersAware;
import org.openqa.selenium.remote.server.Session;
import org.openqa.selenium.remote.server.handler.WebDriverHandler;
import org.openqa.selenium.remote.server.rest.ResultType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SendKeyToActiveElement extends WebDriverHandler implements JsonParametersAware {
  private final List<CharSequence> keys = new CopyOnWriteArrayList<CharSequence>();

  public SendKeyToActiveElement(Session session) {
    super(session);
  }

  @SuppressWarnings({"unchecked"})
  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    //TODO: merge this code with the code in the SendKeys handler.
    List<String> rawKeys = (List<String>) allParameters.get("value");
    List<String> temp = new ArrayList<String>();
    for (String key : rawKeys) {
      temp.add(key);
    }
    keys.addAll(temp);
  }

  public ResultType call() throws Exception {
    Keyboard keyboard = ((HasInputDevices) getDriver()).getKeyboard();

    String[] keysToSend = keys.toArray(new String[0]);
    keyboard.sendKeys(keysToSend);

    return ResultType.SUCCESS;
  }

  @Override
  public String toString() {
    return String.format("[send keys to active: %s]", keys.toArray());
  }
}
