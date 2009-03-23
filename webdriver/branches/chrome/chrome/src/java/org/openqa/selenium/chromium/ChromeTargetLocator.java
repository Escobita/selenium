package org.openqa.selenium.chromium;

import org.openqa.selenium.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;

public class ChromeTargetLocator implements WebDriver.TargetLocator {
  private ExportedWebDriver lib;

  public ChromeTargetLocator(ExportedWebDriver lib) {
    this.lib = lib;
  }

  public WebDriver frame(int frameIndex) {
    return new ChromeDriver();
  }

  public WebDriver frame(String frameName) {
    return new ChromeDriver();
  }

  public WebDriver window(String windowName) {
    return new ChromeDriver();
  }

  public WebDriver defaultContent() {
    return null;
  }

  public WebElement activeElement() {
    return null;
  }
}
