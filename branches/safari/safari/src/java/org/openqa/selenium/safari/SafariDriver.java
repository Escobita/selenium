package org.openqa.selenium.safari;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * An implementation of the {#link WebDriver} interface that drives Safari.
 * This works through a safari extension (in ObjC) which is plugged into
 * safari through some hook mechanism.
 * 
 * @author kurniady@google.com (Andrian Kurniady)
 */
public class SafariDriver implements WebDriver, JavascriptExecutor {
  private SafariBinary safariBinary;
  private RemoteWebDriver rwd;
  
  public SafariDriver() throws Exception {
    SafariBinary safariBinary = new SafariBinary();
    safariBinary.startSafari();
    try {
      this.rwd = new RemoteWebDriver(new URL(safariBinary.getUrl()), DesiredCapabilities.iphone());
      this.safariBinary = safariBinary;
    } catch (Exception e) {
      safariBinary.quit();
      throw e;
    }
  }

  public void close() {
    safariBinary.quit();
  }

  public WebElement findElement(By by) {
    return rwd.findElement(by);
  }

  public List<WebElement> findElements(By by) {
    return rwd.findElements(by);
  }

  public void get(String url) {
    rwd.get(url);
  }

  public String getCurrentUrl() {
    return rwd.getCurrentUrl();
  }

  public String getPageSource() {
    return rwd.getPageSource();
  }

  public String getTitle() {
    return rwd.getTitle();
  }

  public String getWindowHandle() {
    return rwd.getWindowHandle();
  }

  public Set<String> getWindowHandles() {
    return rwd.getWindowHandles();
  }

  public Options manage() {
    return rwd.manage();
  }

  public Navigation navigate() {
    return rwd.navigate();
  }

  public void quit() {
    safariBinary.quit();
  }

  public TargetLocator switchTo() {
    return rwd.switchTo();
  }

  public Object executeScript(String script, Object... args) {
	return rwd.executeScript(script, args);
  }

  public boolean isJavascriptEnabled() {
	return rwd.isJavascriptEnabled();
  }
}
