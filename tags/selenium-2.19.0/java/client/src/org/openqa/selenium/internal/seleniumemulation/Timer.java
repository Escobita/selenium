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

package org.openqa.selenium.internal.seleniumemulation;

import com.thoughtworks.selenium.SeleniumException;

import org.openqa.selenium.WebDriver;

public class Timer {
  private volatile long timeout;
  private boolean stopped;

  public Timer(long timeout) {
    this.timeout = timeout;
  }

  public <T> T run(SeleneseCommand<T> command, WebDriver driver, String[] args) {
    if (stopped) {
      throw new IllegalStateException("Timer has already been stopped");
    }

    long start = System.currentTimeMillis();

    T value = command.apply(driver, args);

    long duration = System.currentTimeMillis() - start;

    if (duration > timeout) {
      throw new SeleniumException("Timed out waiting for action to finish");
    }

    return value;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public void stop() {
    this.stopped = true;
  }
}
