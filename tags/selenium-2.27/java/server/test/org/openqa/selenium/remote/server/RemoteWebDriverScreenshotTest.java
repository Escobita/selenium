// Copyright 2011 Software Freedom Conservancy
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.openqa.selenium.remote.server;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.OutputType.BASE64;
import static org.openqa.selenium.testing.Ignore.Driver.HTMLUNIT;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.testing.drivers.WebDriverBuilder;

@Ignore(HTMLUNIT)
public class RemoteWebDriverScreenshotTest extends JUnit4TestBase {
  @Test
  public void testShouldBeAbleToGrabASnapshotOnException() {
    if (!(driver instanceof RemoteWebDriver)) {
      System.out.println("Skipping test: driver is not a remote webdriver");
      return;
    }

    driver.get(pages.simpleTestPage);

    try {
      driver.findElement(By.id("doesnayexist"));
      fail();
    } catch (NoSuchElementException e) {
      assertTrue(((ScreenshotException) e.getCause()).getBase64EncodedScreenshot().length() > 0);
    }
  }

  @Test
  public void testCanAugmentWebDriverInstanceIfNecessary() {
    if (!(driver instanceof RemoteWebDriver)) {
      System.out.println("Skipping test: driver is not a remote webdriver");
      return;
    }

    RemoteWebDriver remote = (RemoteWebDriver) driver;
    Boolean screenshots = (Boolean) remote.getCapabilities()
        .getCapability(CapabilityType.TAKES_SCREENSHOT);
    if (screenshots == null || !screenshots) {
      System.out.println("Skipping test: remote driver cannot take screenshots");
    }

    driver.get(pages.formPage);
    WebDriver toUse = new Augmenter().augment(driver);
    String screenshot = ((TakesScreenshot) toUse).getScreenshotAs(BASE64);

    assertTrue(screenshot.length() > 0);
  }

  @Test
  public void testShouldBeAbleToDisableSnapshotOnException() {
    if (!(driver instanceof RemoteWebDriver)) {
      System.out.println("Skipping test: driver is not a remote webdriver");
      return;
    }

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability("webdriver.remote.quietExceptions", true);

    WebDriver noScreenshotDriver = new WebDriverBuilder().setDesiredCapabilities(caps).get();

    noScreenshotDriver.get(pages.simpleTestPage);

    try {
        noScreenshotDriver.findElement(By.id("doesnayexist"));
      fail();
    } catch (NoSuchElementException e) {
      Throwable t = e;
      while (t != null) {
    	  assertFalse(t instanceof ScreenshotException);
    	  t = t.getCause();
      }
    }
  }

}
