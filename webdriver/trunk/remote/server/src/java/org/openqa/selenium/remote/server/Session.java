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

package org.openqa.selenium.remote.server;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Capabilities;
import org.openqa.selenium.remote.Context;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Session {
  private final WebDriver driver;
  private KnownElements knownElements = new KnownElements();
  private Capabilities capabilities;
  private Executor executor;

  public Session(final Capabilities capabilities) throws Exception {
    executor = Executors.newSingleThreadExecutor();

    // Ensure that the browser is created on the single thread.
    FutureTask<WebDriver> createBrowser = new FutureTask<WebDriver>(new Callable<WebDriver>() {
      public WebDriver call() throws Exception {
        return createNewDriverMatching(capabilities);
      }
    });
    execute(createBrowser);
    this.driver = createBrowser.get();

    boolean isRendered = isRenderingDriver(capabilities);
    DesiredCapabilities desiredCapabilities =
        new DesiredCapabilities(capabilities.getBrowserName(), capabilities.getVersion(),
                                capabilities.getPlatform());
    desiredCapabilities.setJavascriptEnabled(isRendered);

    this.capabilities = desiredCapabilities;
  }

  public <X> X execute(FutureTask<X> future) throws Exception {
    executor.execute(future);
    return future.get();
  }

  public WebDriver getDriver(Context context) {
    return driver;
  }

  public KnownElements getKnownElements() {
    return knownElements;
  }

  public Capabilities getCapabilities() {
    return capabilities;
  }

  private boolean isRenderingDriver(Capabilities capabilities) {
    String browser = capabilities.getBrowserName();

    return browser != null && !"".equals(browser) && !"htmlunit".equals(browser);
  }

  private WebDriver createNewDriverMatching(Capabilities capabilities) throws Exception {
    Platform platform = capabilities.getPlatform();
    if (platform != null && !Platform.ANY.equals(platform) && !Platform.getCurrent().is(platform)) {
      throw new RuntimeException("Desired operating system does not match current OS");
    }

    String browser = capabilities.getBrowserName();
    if (browser != null) {
      return createNewInstanceOf(browser);
    }

    if (capabilities.isJavascriptEnabled()) {
      return (WebDriver) Class.forName("org.openqa.selenium.firefox.FirefoxDriver")
          .newInstance();
    }

    return (WebDriver) Class.forName("org.openqa.selenium.htmlunit.HtmlUnitDriver")
        .newInstance();
  }

  private WebDriver createNewInstanceOf(String browser) throws Exception {
    if ("htmlunit".equals(browser)) {
      return (WebDriver) Class.forName("org.openqa.selenium.htmlunit.HtmlUnitDriver")
          .newInstance();
    } else if ("firefox".equals(browser)) {
      return (WebDriver) Class.forName("org.openqa.selenium.firefox.FirefoxDriver")
          .newInstance();
    } else if ("internet explorer".equals(browser)) {
      return (WebDriver) Class.forName("org.openqa.selenium.ie.InternetExplorerDriver")
          .newInstance();
    } else if ("safari".equals(browser)) {
      return (WebDriver) Class.forName("org.openqa.selenium.safari.SafariDriver")
          .newInstance();
    }

    throw new RuntimeException("Unable to match browser: " + browser);
  }
}
