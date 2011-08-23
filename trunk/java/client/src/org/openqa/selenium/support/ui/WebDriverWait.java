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

package org.openqa.selenium.support.ui;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * A specialization of {@link FluentWait} that uses WebDriver instances.
 */
public class WebDriverWait extends FluentWait<WebDriver> {
  public final static long DEFAULT_SLEEP_TIMEOUT = 500;

  /**
   * @param driver The WebDriver instance to pass to the expected conditions
   * @param timeOutInSeconds The timeout in seconds when an expectation is
   * called
   */
  public WebDriverWait(WebDriver driver, long timeOutInSeconds) {
    this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, DEFAULT_SLEEP_TIMEOUT
    );
  }

  /**
   * @param driver The WebDriver instance to pass to the expected conditions
   * @param timeOutInSeconds The timeout in seconds when an expectation is
   * called
   * @param sleepInMillis The duration in milliseconds to sleep between polls.
   */
  public WebDriverWait(WebDriver driver, long timeOutInSeconds, long sleepInMillis) {
    this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, sleepInMillis);
  }

  /**
   * @param driver The WebDriver instance to pass to the expected conditions
   * @param clock The clock to use when measuring the timeout
   * @param sleeper Object used to make the current thread go to sleep.
   * @param timeOutInSeconds The timeout in seconds when an expectation is
   * @param sleepTimeOut The timeout used whilst sleeping. Defaults to 500ms
*     called.
   */
  protected WebDriverWait(WebDriver driver, Clock clock, Sleeper sleeper, long timeOutInSeconds,
      long sleepTimeOut) {
    super(driver, clock, sleeper);
    withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
    pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
    ignoring(NotFoundException.class);
  }
}
                       
