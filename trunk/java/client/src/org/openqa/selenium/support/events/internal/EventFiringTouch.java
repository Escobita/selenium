/*
Copyright 2007-2011 WebDriver committers
Copyright 2007-2011 Google Inc.

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

package org.openqa.selenium.support.events.internal;

import org.openqa.selenium.HasTouchScreen;
import org.openqa.selenium.TouchScreen;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 * A touch screen that fires events.
 */
public class EventFiringTouch implements TouchScreen {

  private final WebDriver driver;
  private final WebDriverEventListener dispatcher;
  private final TouchScreen touchScreen;

  public EventFiringTouch(WebDriver driver, WebDriverEventListener dispatcher) {
    this.driver = driver;
    this.dispatcher = dispatcher;
    this.touchScreen = ((HasTouchScreen) this.driver).getTouch();
  }

  public void singleTap(Coordinates where) {
    touchScreen.singleTap(where);
  }

  public void down(int x, int y) {
    touchScreen.down(x, y);
  }

  public void up(int x, int y) {
    touchScreen.up(x, y);
  }

  public void move(int x, int y) {
    touchScreen.move(x, y);
  }

  public void scroll(Coordinates where, int xOffset, int yOffset) {
    touchScreen.scroll(where, xOffset, yOffset);
  }

  public void doubleTap(Coordinates where) {
    touchScreen.doubleTap(where);
  }
}
