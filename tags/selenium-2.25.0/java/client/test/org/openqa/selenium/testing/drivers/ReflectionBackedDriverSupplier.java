/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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

package org.openqa.selenium.testing.drivers;

import static org.openqa.selenium.Platform.WINDOWS;
import static org.openqa.selenium.testing.DevMode.isInDevMode;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.testing.InProject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class ReflectionBackedDriverSupplier implements Supplier<WebDriver> {

  private final static Logger log = 
      Logger.getLogger(ReflectionBackedDriverSupplier.class.getName());
  private final Capabilities desiredCapabilities;
  private final Capabilities requiredCapabilities;

  public ReflectionBackedDriverSupplier(Capabilities desiredCapabilities, 
      Capabilities requiredCapabilities) {
    this.desiredCapabilities = desiredCapabilities;
    this.requiredCapabilities = requiredCapabilities;
  }

  public WebDriver get() {
    try {
      DesiredCapabilities desiredCapsToUse = new DesiredCapabilities(desiredCapabilities);

      Class<? extends WebDriver> driverClass = mapToClass(desiredCapsToUse);
      if (driverClass == null) {
        return null;
      }

      if (DesiredCapabilities.firefox().getBrowserName().
          equals(desiredCapsToUse.getBrowserName())) {
        if (isInDevMode()) {
          copyFirefoxDriverDefaultsToOutputDir();
        }

        FirefoxProfile profile = new FirefoxProfile();
        boolean enableNativeEvents = Boolean.getBoolean("selenium.browser.native_events") ||
                               Platform.getCurrent().is(WINDOWS);
        profile.setEnableNativeEvents(enableNativeEvents);
        desiredCapsToUse.setCapability(FirefoxDriver.PROFILE, profile);
        
        return driverClass.getConstructor(Capabilities.class, 
            Capabilities.class).newInstance(desiredCapsToUse, requiredCapabilities);
      }

      //TODO: Call constructor with two Capabilities arguments for all driver classes
      return driverClass.getConstructor(Capabilities.class).newInstance(desiredCapsToUse);
    } catch (InvocationTargetException e) {
      throw Throwables.propagate(e.getTargetException());
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private void copyFirefoxDriverDefaultsToOutputDir() throws IOException {
    File defaults = InProject.locate("javascript/firefox-driver/webdriver.json");
    File out = InProject.locate("java/client/build/production/org/openqa/selenium/firefox/FirefoxProfile.class").getParentFile();
    out = new File(out, "webdriver.json");
    Files.copy(defaults, out);
  }

  // Cover your eyes
  private Class<? extends WebDriver> mapToClass(Capabilities caps) {
    String name = caps == null ? "" : caps.getBrowserName();
    String className = null;

    if (DesiredCapabilities.android().getBrowserName().equals(name)) {
      className = "org.openqa.selenium.android.AndroidDriver";
    } else if (DesiredCapabilities.chrome().getBrowserName().equals(name)) {
      className = "org.openqa.selenium.testing.drivers.TestChromeDriver";
    } else if (DesiredCapabilities.firefox().getBrowserName().equals(name)) {
      className = getFirefoxClassName();
    } else if (DesiredCapabilities.htmlUnit().getBrowserName().equals(name)) {
      if (caps.isJavascriptEnabled()) {
        className = "org.openqa.selenium.htmlunit.JavascriptEnabledHtmlUnitDriverTests$HtmlUnitDriverForTest";
      } else {
        className = "org.openqa.selenium.htmlunit.HtmlUnitDriver";
      }
    } else if (DesiredCapabilities.internetExplorer().getBrowserName().equals(name)) {
      if (isInDevMode()) {
        className = "org.openqa.selenium.testing.drivers.TestInternetExplorerDriver";
      } else {
        className = "org.openqa.selenium.ie.InternetExplorerDriver";
      }
    } else if (DesiredCapabilities.ipad().getBrowserName().equals(name)) {
    	// for now using the iphone sim... TODO need to make the sim launch in ipad mode
    	className = "org.openqa.selenium.iphone.IPhoneDriverTests$TestIPhoneSimulatorDriver";
    } else if (DesiredCapabilities.iphone().getBrowserName().equals(name)) {
    	className = "org.openqa.selenium.iphone.IPhoneDriverTests$TestIPhoneSimulatorDriver";
    } else if (DesiredCapabilities.safari().getBrowserName().equals(name)) {
      className = "org.openqa.selenium.safari.SafariDriver";
    } else {
      // The last chance saloon.
      className = System.getProperty("selenium.browser.class_name");
    }

    if (className == null) {
      log.fine("Unsure how to create: " + caps);
      return null;
    }

    try {
      return Class.forName(className).asSubclass(WebDriver.class);
    } catch (ClassNotFoundException e) {
      throw Throwables.propagate(e);
    }
  }

  private String getFirefoxClassName() {
    if (isInDevMode()) {
      return "org.openqa.selenium.testing.drivers.SynthesizedFirefoxDriver";
    } else {
      return "org.openqa.selenium.firefox.FirefoxDriver";
    }
  }
}
