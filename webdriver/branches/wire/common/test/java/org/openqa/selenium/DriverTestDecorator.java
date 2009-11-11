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

package org.openqa.selenium;

import junit.extensions.TestSetup;
import junit.framework.Test;

import java.util.logging.Logger;
import static java.util.logging.Level.SEVERE;

public class DriverTestDecorator extends TestSetup {

  private static final Logger LOG = Logger.getLogger(DriverTestDecorator.class.getName());

  private final Class<? extends WebDriver> driverClass;
  private final boolean keepDriver;
  private final boolean freshDriver;

  private static WebDriver driver;
  private final boolean restartDriver;

  public DriverTestDecorator(Test test, Class<? extends WebDriver> driverClass, boolean keepDriver,
                             boolean freshDriver, boolean restartDriver) {
    super(test);
    this.driverClass = driverClass;
    this.keepDriver = keepDriver;
    this.freshDriver = freshDriver;
    this.restartDriver = restartDriver;
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    try {
      if (driver != null && freshDriver) {
        driver.quit();
        driver = null;
      }

      if (getTest() instanceof NeedsDriver) {
        driver = instantiateDriver();
        ((NeedsDriver) getTest()).setDriver(driver);
      }
    } catch(Exception e) {
      LOG.log(SEVERE, "Decorator setup error", e);
      throw e;
    }
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      if (!keepDriver || restartDriver) {
        try {
          driver.quit();

        } catch (Exception e) {
          // this is okay --- the driver could be quit by the test
        }
        driver = null;
      }
    } catch(Exception e) {
      LOG.log(SEVERE, "Decorator teardown error", e);
      throw e;
    }
    super.tearDown();
  }

  public static WebDriver getDriver() {
    return driver;
  }

  private WebDriver instantiateDriver() {
    if (keepDriver && driver != null) {
      return driver;
    }

    try {
      return driverClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Cannot instantiate driver", e);
    }
  }
}
