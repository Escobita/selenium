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

import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.Session;
import org.openqa.selenium.remote.server.rest.ResultType;

public class FindActiveElement extends ResponseAwareWebDriverHandler {

  public FindActiveElement(Session session) {
    super(session);
  }

  public ResultType call() throws Exception {

      WebElement element = getDriver().switchTo().activeElement();
      String elementId = getKnownElements().add(element);
      response.setValue(ImmutableMap.of("ELEMENT", elementId));

      return ResultType.SUCCESS;
    }

    @Override
    public String toString() {
      return "[find active element]";
    }
}
