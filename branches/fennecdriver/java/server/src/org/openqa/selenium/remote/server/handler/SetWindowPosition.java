/*
Copyright 2011 WebDriver committers
Copyright 2011 Software Freedom Conservancy.

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

import org.openqa.selenium.Point;
import org.openqa.selenium.remote.server.JsonParametersAware;
import org.openqa.selenium.remote.server.Session;
import org.openqa.selenium.remote.server.rest.ResultType;

import java.util.Map;

public class SetWindowPosition extends WebDriverHandler implements JsonParametersAware {

  private Point position;

  public SetWindowPosition(Session session) {
    super(session);
  }

  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    int x = ((Number) allParameters.get("x")).intValue();
    int y = ((Number) allParameters.get("y")).intValue();

    position = new Point(x, y);
  }

  public ResultType call() throws Exception {
    getDriver().manage().window().setPosition(position);
    return ResultType.SUCCESS;
  }

  @Override
  public String toString() {
    return "[set window position]";
  }
}
