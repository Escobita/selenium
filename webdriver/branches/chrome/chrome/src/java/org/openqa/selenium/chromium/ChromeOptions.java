package org.openqa.selenium.chromium;

import static org.openqa.selenium.chromium.ExportedWebDriver.SUCCESS;

import org.openqa.selenium.chromium.ExportedWebDriver.StringWrapper;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.ReturnedCookie;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.net.MalformedURLException;
import java.net.URL;

public class ChromeOptions implements WebDriver.Options {
  private ExportedWebDriver lib;
  private Pointer driver;

  public ChromeOptions(ExportedWebDriver lib, Pointer driver) {
    this.lib = lib;
    this.driver = driver;
  }

  public void deleteCookie(Cookie cookie) {
    Date dateInPast = new Date(0);
    Cookie toDelete = new ReturnedCookie(cookie.getName(), cookie.getValue(),
        cookie.getDomain(), cookie.getPath(), dateInPast, false);
    addCookie(toDelete);
  }

  public void deleteCookieNamed(String name) {
    deleteCookie(new ReturnedCookie(name, "", getCurrentHost(), "", null, false));
  }

  public void deleteAllCookies() {
    Set<Cookie> cookies = getCookies();
    for (Cookie cookie : cookies) {
      deleteCookie(cookie);
    }
  }

  public Set<Cookie> getCookies() {
    String currentUrl = getCurrentHost();
    PointerByReference wrapper = new PointerByReference();
    int result = lib.wdGetCookies(driver, wrapper);
    if (result != SUCCESS) {
      throw new RuntimeException("Unable to get current URL: " + result);
    }

    Set<Cookie> toReturn = new HashSet<Cookie>();
    String domainCookies = new StringWrapper(lib, wrapper).toString(); 
			
		String[] cookies = domainCookies.split("; ");
    for (String cookie : cookies) {
      int index = cookie.indexOf("=");
      if (index < 0) continue;
      String name = cookie.substring(0, index);
      String value = cookie.substring(index + 1);
      toReturn.add(new ReturnedCookie(name, value, currentUrl, "", null, false));
    }
    return toReturn;
  }

  public void addCookie(Cookie cookie) {
    int result = lib.wdAddCookie(driver, new WString(cookie.toString()));
    if (result != SUCCESS) {
      throw new RuntimeException("Unable to get current URL: " + result);
    }
  }

  public Speed getSpeed() {
    throw new RuntimeException("Not supported.");
  }

  public void setSpeed(Speed speed) {
    throw new RuntimeException("Not supported.");
  }

  private String getCurrentHost() {
    try {
      PointerByReference ptr = new PointerByReference();
      int result = lib.wdGetCurrentUrl(driver, ptr);
      if (result != SUCCESS) {
        throw new RuntimeException("Unable to get current URL: " + result);
      }
      return new URL(new StringWrapper(lib, ptr).toString()).getHost();
    } catch (MalformedURLException e) {
      return "";
    }
  }
}
