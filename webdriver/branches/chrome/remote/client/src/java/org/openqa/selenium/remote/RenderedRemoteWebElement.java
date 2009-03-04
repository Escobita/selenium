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

// Copyright 2008 Google Inc.  All Rights Reserved.

package org.openqa.selenium.remote;

import org.openqa.selenium.RenderedWebElement;
import static org.openqa.selenium.remote.MapMaker.map;

import java.awt.*;
import java.util.Map;

public class RenderedRemoteWebElement extends RemoteWebElement implements RenderedWebElement {
  public boolean isDisplayed() {
    Response response = parent.execute("isElementDisplayed", map("id", id));
    return (Boolean) response.getValue();
  }

  @SuppressWarnings({"unchecked"})
  public Point getLocation() {
    Response response = parent.execute("getElementLocation", map("id", id));
    Map<String, Object> rawPoint = (Map<String, Object>) response.getValue();
    int x = ((Long) rawPoint.get("x")).intValue();
    int y = ((Long) rawPoint.get("y")).intValue();
    return new Point(x, y);
  }

  @SuppressWarnings({"unchecked"})
  public Dimension getSize() {
    Response response = parent.execute("getElementSize", map("id", id));
    Map<String, Object> rawSize = (Map<String, Object>) response.getValue();
    int width = ((Long) rawSize.get("width")).intValue();
    int height = ((Long) rawSize.get("height")).intValue();
    return new Dimension(width, height);
  }

  public void dragAndDropBy(int moveRightBy, int moveDownBy) {
    parent.execute("dragElement", map("id", id), moveRightBy, moveDownBy);
  }

  public void dragAndDropOn(RenderedWebElement element) {
    Point currentLocation = getLocation();
    Point destination = element.getLocation();
    dragAndDropBy(destination.x - currentLocation.x, destination.y - currentLocation.y);
  }

  public String getValueOfCssProperty(String propertyName) {
    Response response = parent.execute("getValueOfCssProperty", map("id", id, "propertyName", propertyName));
    return (String) response.getValue();
  }
}
