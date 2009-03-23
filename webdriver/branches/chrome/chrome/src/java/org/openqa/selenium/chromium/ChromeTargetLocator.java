package org.openqa.selenium.chromium;

import static org.openqa.selenium.chromium.ExportedWebDriver.SUCCESS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

public class ChromeTargetLocator implements WebDriver.TargetLocator {
  private ExportedWebDriver lib;
  private Pointer driver;
  private ChromeDriver chromeDriver;

  public ChromeTargetLocator(ExportedWebDriver lib, Pointer driver, ChromeDriver chromeDriver) {
    this.lib = lib;
    this.driver = driver;
    this.chromeDriver = chromeDriver;
  }

  public WebDriver frame(int frameIndex) {
    lib.wdSwitchToFrameIndex(driver, frameIndex);
    return chromeDriver;
  }

  public WebDriver frame(String frameName) {
    lib.wdSwitchToFrame(driver, new WString(frameName));
    return chromeDriver;
  }

  public WebDriver window(String windowName) {
    lib.wdSwitchToWindow(driver, new WString(windowName));
    return chromeDriver;
  }

  public WebDriver defaultContent() {
    lib.wdSwitchToWindow(driver, new WString(""));
    lib.wdSwitchToFrame(driver, new WString(""));
    return chromeDriver;
  }

  public WebElement activeElement() {
    PointerByReference element = new PointerByReference();
    if (SUCCESS == lib.wdSwitchToActiveElement(driver, element)) {
      return new ChromeElement(lib, driver, element.getValue());
    }
    return null;
  }
}
