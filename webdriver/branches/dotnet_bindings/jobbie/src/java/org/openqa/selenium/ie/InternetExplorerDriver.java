/*
 * Copyright 2007 ThoughtWorks, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.openqa.selenium.ie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.ReturnedCookie;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import static org.openqa.selenium.ie.ErrorCode.SUCCESS;

public class InternetExplorerDriver implements WebDriver, SearchContext,
    JavascriptExecutor {
  private final static WebDriverLibrary lib = JobbieLibraryInstance
      .getLibraryInstance();
  private final Helpers helpers;
  private Pointer driver;

  public InternetExplorerDriver() {
    PointerByReference ptr = new PointerByReference();
    ErrorCode result = ErrorCode.fromCode(lib.wdNewDriverInstance(ptr));
    if (result != SUCCESS) {
      throw new IllegalStateException("Cannot create new browser instance: " + result);
    }
    helpers = new Helpers(lib);
    driver = ptr.getValue();
  }

  public String getPageSource() {
    PointerByReference ptr = new PointerByReference();
    ErrorCode result = ErrorCode.fromCode(lib.wdGetPageSource(driver, ptr));
    if (result != SUCCESS) {
      throw new IllegalStateException("Unable to get page source: " + result);
    }
    return helpers.convertToString(ptr);
  }

  public void close() {
    ErrorCode result = ErrorCode.fromCode(lib.wdClose(driver));
    if (result != SUCCESS) {
      throw new IllegalStateException("Unable to close driver: " + result);
    }
  }

  public void quit() {
    close(); // Not a good implementation, but better than nothing
  }

  public Set<String> getWindowHandles() {
    return Collections.singleton("1");
  }

  public String getWindowHandle() {
    return "1";
  }

  public Object executeScript(String script, Object... args) {
    for (Object arg : args) {
      if (!(arg instanceof String || arg instanceof Boolean
          || arg instanceof Number || arg instanceof InternetExplorerElement))
        throw new IllegalArgumentException(
            "Parameter is not of recognized type: " + arg);
    }

    script = "(function() { return function(){" + script + "};})();";

    throw new UnsupportedOperationException("executeScript");
  }

  public void get(String url) {
    ErrorCode result = ErrorCode.fromCode(lib.wdGet(driver, new WString(url)));
    if (result != SUCCESS) {
      throw new IllegalStateException(String.format("Cannot get \"%s\": %s", url, result));
    }
  }

  public String getCurrentUrl() {
    PointerByReference ptr = new PointerByReference();
    ErrorCode result = ErrorCode.fromCode(lib.wdGetCurrentUrl(driver, ptr));
    if (result != SUCCESS) {
      throw new IllegalStateException("Unable to get current URL: " + result);
    }
    
    return helpers.convertToString(ptr);
  }

  public String getTitle() {
    PointerByReference ptr = new PointerByReference();
    ErrorCode result = ErrorCode.fromCode(lib.wdGetTitle(driver, ptr));
    if (result != SUCCESS) {
      throw new IllegalStateException("Unable to get the title of the current page: " + result);
    }
    return helpers.convertToString(ptr);
  }

  public boolean getVisible() {
    IntByReference result = new IntByReference();
    ErrorCode res = ErrorCode.fromCode(lib.wdGetVisible(driver, result));
    if (res != SUCCESS) {
      throw new IllegalStateException("Cannot determine visibility of browser: " + res);
    }
    return result.getValue() == 1;
  }

  public void setVisible(boolean visible) {
    ErrorCode result = ErrorCode.fromCode(lib.wdSetVisible(driver, visible ? 1 : 0));
    if (result != SUCCESS) {
      throw new IllegalStateException("Cannot set the visibility of the browser: " + result);
    }
  }

  public List<WebElement> findElements(By by) {
    return new Finder(lib, driver, null).findElements(by);
  }

  public WebElement findElement(By by) {
    return new Finder(lib, driver, null).findElement(by);
  }

  @Override
  public String toString() {
     return getClass().getName() + ":" + driver;
  }

  public TargetLocator switchTo() {
    return new InternetExplorerTargetLocator();
  }

  public Navigation navigate() {
    return new InternetExplorerNavigation();
  }

  public Options manage() {
    return new InternetExplorerOptions();
  }

  protected void waitForLoadToComplete() {
    ErrorCode result = ErrorCode.fromCode(lib.wdWaitForLoadToComplete(driver));
    if (result != SUCCESS) {
      throw new IllegalStateException("Unable to wait for load to complete: " + result);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    lib.wdFreeDriver(driver);  // We should check the error code, but there's nothing sane to do.
  }

  private class InternetExplorerTargetLocator implements TargetLocator {
    public WebDriver frame(int frameIndex) {
      return frame(String.valueOf(frameIndex));
    }

    public WebDriver frame(String frameName) {
      ErrorCode result = ErrorCode.fromCode(lib.wdSwitchToFrame(driver, new WString(frameName)));

      if (result == ErrorCode.NO_SUCH_FRAME) {
        throw new NoSuchFrameException("Unable to find frame with name or id: "
            + frameName);
      }
      if (result != SUCCESS) {
        throw new IllegalStateException(
            String.format("Cannot switch to frame \"%s\": %s", frameName, result));
      }
      return InternetExplorerDriver.this;
    }

    public WebDriver window(String windowName) {
      return null; // For the sake of getting us off the ground
    }

    public Iterable<WebDriver> windowIterable() {
      throw new UnsupportedOperationException("windowIterable");
    }

    public WebDriver defaultContent() {
      return frame("");
    }

    public WebElement activeElement() {
      PointerByReference element = new PointerByReference();

      ErrorCode result = ErrorCode.fromCode(lib.wdSwitchToActiveElement(driver, element));
      if (result == SUCCESS)
        return new InternetExplorerElement(lib, driver, element.getValue());
      
      if (result == ErrorCode.NO_SUCH_ELEMENT) {
        throw new NoSuchElementException("Cannot find active element");
      }
      
      throw new IllegalStateException("Cannot find active element: " + result);
    }

    public Alert alert() {
      throw new UnsupportedOperationException("alert");
    }
  }

  private class InternetExplorerNavigation implements Navigation {
    public void back() {
      ErrorCode result = ErrorCode.fromCode(lib.wdGoBack(driver));
      if (result != SUCCESS) {
        throw new IllegalStateException("Cannot navigate backwards: " + result);
      }
      
      result = ErrorCode.fromCode(lib.wdWaitForLoadToComplete(driver));
      if (result != SUCCESS) {
        throw new IllegalStateException("Cannot wait for back navigation to end: " + result);
      }
    }

    public void forward() {
      ErrorCode result = ErrorCode.fromCode(lib.wdGoForward(driver));
      if (result != SUCCESS) {
        throw new IllegalStateException("Cannot navigate forward: " + result);
      }
      
      result = ErrorCode.fromCode(lib.wdWaitForLoadToComplete(driver));
      if (result != SUCCESS) {
        throw new IllegalStateException("Cannot wait for forward navigation to end: " + result);
      }
    }

    public void to(String url) {
      get(url);
    }
  }

  private class InternetExplorerOptions implements Options {
    public void addCookie(Cookie cookie) {
      ErrorCode result = ErrorCode.fromCode(lib.wdAddCookie(driver, new WString(cookie.toString())));
      if (result != SUCCESS) {
        throw new IllegalStateException(
            String.format("Unable to add cookie: %s (%s)", cookie, result));
      }
    }

    public void deleteAllCookies() {
      Set<Cookie> cookies = getCookies();
      for (Cookie cookie : cookies) {
        deleteCookie(cookie);
      }
    }

    public void deleteCookie(Cookie cookie) {
      Date dateInPast = new Date(0);
      Cookie toDelete = new ReturnedCookie(cookie.getName(), cookie.getValue(),
          cookie.getDomain(), cookie.getPath(), dateInPast, false);
      addCookie(toDelete);
    }

    public void deleteCookieNamed(String name) {
      deleteCookie(new ReturnedCookie(name, "", getCurrentHost(), "", null,
          false));
    }

    public Set<Cookie> getCookies() {
      String currentUrl = getCurrentHost();

      Set<Cookie> toReturn = new HashSet<Cookie>();
      PointerByReference rawCookie = new PointerByReference();
      ErrorCode result = ErrorCode.fromCode(lib.wdGetCookies(driver, rawCookie));
      if (result != SUCCESS) {
        throw new IllegalStateException("Unable to get all available cookies: " + result);
      }
      String allDomainCookies = helpers.convertToString(rawCookie);

      String[] cookies = allDomainCookies.split("; ");
      for (String cookie : cookies) {
        String[] parts = cookie.split("=");
        if (parts.length != 2) {
          continue;
        }

        toReturn.add(new ReturnedCookie(parts[0], parts[1], currentUrl, "",
            null, false));
      }

      return toReturn;
    }

    private String getCurrentHost() {
      try {
        URL url = new URL(getCurrentUrl());
        return url.getHost();
      } catch (MalformedURLException e) {
        return "";
      }
    }

    public Speed getSpeed() {
      throw new UnsupportedOperationException();
    }

    public void setSpeed(Speed speed) {
      new UnsupportedOperationException("setSpeed");
    }
  }
}
