/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium.remote.server.handler;

import com.google.common.collect.Lists;

import org.openqa.selenium.remote.server.JsonParametersAware;
import org.openqa.selenium.remote.server.Session;
import org.openqa.selenium.remote.server.rest.ResultType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SendKeys extends WebElementHandler implements JsonParametersAware {

  private final List<CharSequence> keys = new CopyOnWriteArrayList<CharSequence>();

  public SendKeys(Session session) {
    super(session);
  }

  @SuppressWarnings({"unchecked"})
  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    List<String> rawKeys = (List<String>) allParameters.get("value");
    List<String> temp = Lists.newArrayList();
    for (String key : rawKeys) {
      temp.add(key);
    }
    keys.addAll(temp);
  }

  public ResultType call() throws Exception {
    String[] keysToSend = keys.toArray(new String[0]);
    getElement().sendKeys(keysToSend);

    return ResultType.SUCCESS;
  }

  @Override
  public String toString() {
    return String.format("[send keys: %s, %s]", getElementAsString(), keys);
  }
}
