/*
Copyright 2007-2009 Selenium committers

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

package org.openqa.selenium.v1;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import com.thoughtworks.selenium.BrowserConfigurationOptions;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import org.openqa.selenium.Build;
import org.openqa.selenium.testing.DevMode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.environment.TestEnvironment;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.testing.InProject;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.os.CommandLine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SeleniumTestEnvironment implements TestEnvironment {
  private static ThreadLocal<Selenium> instance = new ThreadLocal<Selenium>();
  private CommandLine command;
  private AppServer appServer;

  public SeleniumTestEnvironment(int port, String... extraArgs) {
    try {
      if (DevMode.isInDevMode()) {
        new Build().of("//java/server/test/org/openqa/selenium:server-with-tests:uber").go();
      }

      File seleniumJar = InProject.locate(
          "build/java/server/test/org/openqa/selenium/server-with-tests-standalone.jar");

      ArrayList<Object> args = Lists.newArrayList();
      if (Boolean.getBoolean("webdriver.debug")) {
        args.add("-Xdebug");
        args.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005");
      }
      
      args.add("-jar");
      args.add(seleniumJar.getAbsolutePath());
      args.add("-port");
      args.add(String.valueOf(port));
      
      if (Boolean.getBoolean("singlewindow")) {
        args.add("singlewindow");
      }
      if (Boolean.getBoolean("webdriver.debug")) {
        args.add("-browserSideLog");
      }
      
      args.addAll(Arrays.asList(extraArgs));

      command = new CommandLine("java", args.toArray(new String[args.size()]));
      command.copyOutputTo(System.out);
      command.executeAsync();

      PortProber.pollPort(port);

      URL status = new URL("http://localhost:" + port + "/wd/hub/status");
      new UrlChecker().waitUntilAvailable(60, TimeUnit.SECONDS, status);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    appServer = new SeleniumAppServer(port);
  }

  public SeleniumTestEnvironment(String... extraArgs) {
    this(4444, extraArgs);
  }

  public SeleniumTestEnvironment() {
    this(PortProber.findFreePort());
  }

  public AppServer getAppServer() {
    return appServer;
  }

  public void stop() {
    if (instance.get() != null) {
      instance.get().stop();
      instance.set(null);
    }
    command.destroy();
  }

  public Selenium getSeleniumInstance(String browserString) {
    Selenium current = instance.get();
    if (current != null) {
      return current;
    }

    current = startBrowser(browserString);
    instance.set(current);
    return current;
  }

  public static void main(String[] args) {
    new SeleniumTestEnvironment(4444);
  }

  private Selenium startBrowser(String browserString) {
    final String browser = System.getProperty("selenium.browser", browserString);

    String baseUrl = getAppServer().whereIs("/selenium-server/tests/");

    Selenium selenium;
    if (getDriverClass(browser) != null) {
      selenium = new WebDriverBackedSelenium(new Supplier<WebDriver>() {
        public WebDriver get() {
          try {
            return getDriverClass(browser).newInstance();
          } catch (Exception e) {
            throw Throwables.propagate(e);
          }
        }
      }, baseUrl);
      selenium.start();
    } else {
      int port = 0;
      try {
        port = new URL(baseUrl).getPort();
      } catch (MalformedURLException e) {
        throw Throwables.propagate(e);
      }
      selenium = new DefaultSelenium("localhost", port, browser, baseUrl);
      if (Boolean.getBoolean("webdriver.debug")) {
        BrowserConfigurationOptions options = new BrowserConfigurationOptions();
        options.set("browserSideLog", "true");
        selenium.start(options);
        selenium.setBrowserLogLevel("debug");
      } else {
        selenium.start();
      }
    }

    return selenium;
  }


  private Class<? extends WebDriver> getDriverClass(String browserString) {
    if (browserString == null) {
      return null;
    }

    try {
      return Class.forName(browserString).asSubclass(WebDriver.class);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

}
