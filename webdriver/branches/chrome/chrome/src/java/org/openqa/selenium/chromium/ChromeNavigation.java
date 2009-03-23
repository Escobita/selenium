package org.openqa.selenium.chromium;

import org.openqa.selenium.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;

import java.net.URL;

public class ChromeNavigation implements WebDriver.Navigation {
  private ExportedWebDriver lib;
  private Pointer driver;

  public ChromeNavigation(ExportedWebDriver lib, Pointer driver) {
    this.lib = lib;
    this.driver = driver;
  }

  public void back() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGoBack(driver);
  }

  public void forward() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGoForward(driver);
  }

  public void to(String url) {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGet(driver, new WString(url));
  }

  public void to(URL url) {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGet(driver, new WString(url.toString()));
  }

  public void refresh() {
    throw new RuntimeException("Not implemented for this browser.");
  }
}
