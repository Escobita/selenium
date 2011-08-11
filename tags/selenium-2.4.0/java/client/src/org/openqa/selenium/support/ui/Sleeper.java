/*
Copyright 2011 WebDriver committers
Copyright 2011 Google Inc.

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

package org.openqa.selenium.support.ui;

import java.util.concurrent.TimeUnit;

/**
 * Abstraction around {@link Thread#sleep(long)} to permit better testability.
 */
public interface Sleeper {

  public static final Sleeper SYSTEM_SLEEPER = new Sleeper() {
    public void sleep(Duration duration) throws InterruptedException {
      Thread.sleep(duration.in(TimeUnit.MILLISECONDS));
    }
  };

  /**
   * Sleeps for the specified duration of time.
   *
   * @param duration How long to sleep.
   * @throws InterruptedException If hte thread is interrupted while sleeping.
   */
  void sleep(Duration duration) throws InterruptedException;
}
