package org.openqa.selenium.chromium;

import org.openqa.selenium.chromium.ExportedWebDriver.StringWrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Set;
import java.util.List;

public class ChromeDriver implements WebDriver, SearchContext, JavascriptExecutor {
  public static final int SUCCESS = 0;
  private static ExportedWebDriver lib = null;
  private Pointer driver;

  public ChromeDriver(ExportedWebDriver lib, Pointer driver) {
    this.lib = lib;
    this.driver = driver;
  }

  public ChromeDriver() {
    initializeLibrary();

    PointerByReference ptr = new PointerByReference();
    lib.wdNewDriverInstance(ptr);
    if (ptr != null) {
      driver = ptr.getValue();
    } else {
      throw new RuntimeException("Cannot load the driver.");
    }
  }

  private void initializeLibrary() {
    if (lib != null) {
      return;
    }

    StringBuilder jnaPath = new StringBuilder();
    jnaPath.append(System.getProperty("java.class.path"));
    jnaPath.append(File.pathSeparator);

    File dll = writeResourceToDisk("ChromeDriver.dll");
    dll.deleteOnExit();
    String driverLib = dll.getName().replace(".dll", "");
    jnaPath.append(dll.getParent());

    System.setProperty("jna.library.path", jnaPath.toString());

    try {
      lib = (ExportedWebDriver)  Native.loadLibrary("ChromeDriver", ExportedWebDriver.class);
    } catch (UnsatisfiedLinkError e) {
      lib = (ExportedWebDriver)  Native.loadLibrary(driverLib, ExportedWebDriver.class);
    }
  }

  private File writeResourceToDisk(String resourceName) throws UnsatisfiedLinkError {
    InputStream is = ChromeDriver.class.getResourceAsStream(resourceName);
    if (is == null) 
      is = ChromeDriver.class.getResourceAsStream("/" + resourceName);

    FileOutputStream fos = null;

    try {
      File dll = File.createTempFile("webdriver", ".dll");
      dll.deleteOnExit();
      fos = new FileOutputStream(dll);

      int count;
      byte[] buf = new byte[4096];
      while ((count = is.read(buf, 0, buf.length)) > 0) {
        fos.write(buf, 0, count);
      }

      return dll;
    } catch(IOException e2) {
      throw new UnsatisfiedLinkError("Cannot create temporary DLL: " + e2.getMessage());
    }
    finally {
      try { is.close(); } catch(IOException e2) { }
      if (fos != null) {
        try { fos.close(); } catch(IOException e2) { }
      }
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (driver != null) {
      System.out.println("Finalizing the driver.");
      lib.wdFreeDriverInstance(driver);
      driver = null;
    }
    super.finalize();
  }

  public void setVisible(boolean visible) {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    int is_visible_ = (visible) ? 1 : 0;
    lib.wdSetVisible(driver, is_visible_);
  }

  public boolean getVisible() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");

    IntByReference toReturn = new IntByReference();
    int result = lib.wdGetVisible(driver, toReturn);
    if (toReturn == null) {
      throw new RuntimeException("Unable to determine if browser is visible: " + result);
    }
    return toReturn.getValue() == 1;
  }

  public void quit() {
    // TODO(amitabh): Implement this.
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdClose(driver);
  }

  public void close() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdClose(driver);
  }

  public void back() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGoBack(driver);
    try { Thread.sleep(100); } catch(Exception e) {}
  }

  public void forward() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGoForward(driver);
    try { Thread.sleep(100); } catch(Exception e) {}
  }

  public void get(String url) {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    lib.wdGet(driver, new WString(url));
    try { Thread.sleep(100); } catch(Exception e) {}
  }

  public String getCurrentUrl() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");

    PointerByReference ptr = new PointerByReference();
    int result = lib.wdGetCurrentUrl(driver, ptr);
    if (result != SUCCESS) {
      throw new RuntimeException("Unable to get current URL: " + result);
    }
    return new StringWrapper(lib, ptr).toString();
  }

  public String getTitle() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    PointerByReference ptr = new PointerByReference();
    int result = lib.wdGetTitle(driver, ptr);
    if (result != SUCCESS) {
      throw new RuntimeException("Unable to get current URL: " + result);
    }
    return new StringWrapper(lib, ptr).toString();
  }

  public String getPageSource() {
    if (driver == null) throw new RuntimeException("Driver was not loaded.");
    PointerByReference ptr = new PointerByReference();
    int result = lib.wdGetPageSource(driver, ptr);
    if (result != SUCCESS) {
      throw new RuntimeException("Unable to get current URL: " + result);
    }
    return new StringWrapper(lib, ptr).toString();
  }

  // -------------------------------------------------------------------------
  // Implements navigation interface
  // -------------------------------------------------------------------------
  public Navigation navigate() {
    return new ChromeNavigation(lib, driver);
  }

  // -------------------------------------------------------------------------
  // Implements options interface
  // -------------------------------------------------------------------------
  public Options manage() {
    return new ChromeOptions(lib, driver);
  }

  // -------------------------------------------------------------------------
  // Implements SearchContext.
  // -------------------------------------------------------------------------
  public List<WebElement> findElements(By by) {
    return new Finder(lib, driver).findElements(by);
  }

  public WebElement findElement(By by) {
    return new Finder(lib, driver).findElement(by);
  }

  // -------------------------------------------------------------------------
  // Implements TargetLocator interface
  // -------------------------------------------------------------------------
  public TargetLocator switchTo() {
    return new ChromeTargetLocator(lib, driver, this);
  }

  public Set<String> getWindowHandles() {
    // TODO(amitabh): Implement this.
    return null;
  }

  public String getWindowHandle() {
    // TODO(amitabh): Implement this.
    return "";
  }

  // Implements JavascriptExecutor interface
  public Object executeScript(String script, Object... args) {
    // TODO(amitabh): Implement this.
    return null;
  }
}
