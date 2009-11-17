package com.android.webdriver.sessions;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.LinkedList;
import java.util.List;

public class SessionCookieManager {

  /** Actions that are supported by Cookie Manager */
  public enum CookieActions {ADD, REMOVE, REMOVE_ALL, GET, GET_ALL}
  
  
  private SessionCookieManager(Context context) {
    CookieSyncManager.createInstance(context);
    mCookieManager = CookieManager.getInstance();
  }
  
  /**
   * Returns a Singleton instance of SessionCookieManager class
   */
  public static SessionCookieManager getInstance() {
    if (mInstance == null || mContext == null) {
      throw new RuntimeException("Class SessionCookieManager must be" +
        " initialized with createInstance() before calling getInstance()");
    }
    return mInstance;
  }

  /**
   * Create a singleton SessionCookieManager within a context
   * @param context Android context to use to access Cookies
   */
  public static void createInstance(Context context) {
    mContext = context;
    mInstance = new SessionCookieManager(mContext);
  }

  /**
   * Get all cookies for given domain name
   * @param domain Domain name to fetch cookies for
   * @return Set of cookie objects for given domain
   */
  public List<Cookie> getCookies(String domain) {
    List<Cookie> result = new LinkedList<Cookie>();
    for (String cookie : mCookieManager.getCookie(domain).split(";")) {
      String[] cookieValues = cookie.split("=");
      if (cookieValues.length != 2)
        throw new RuntimeException("Invalid cookie: " + cookie);
      result.add(new Cookie(domain, cookieValues[0], cookieValues[1]));
    }
    return result;
  }
  
  /**
   * Returns list of cookies for given domain as a semicolon-separated string
   * @param domain Domain name to fetch cookies for
   * @return Cookie string in form: name=value[;name=value...]
   */
  public String getCookiesAsString(String domain) {
    return mCookieManager.getCookie(domain);
  }
  
  /**
   * Get cookie with specific name
   * @param domain Domain name to fetch cookie for
   * @param name Cookie name to search
   * @return Cookie object (if found) or null
   */
  public Cookie getCookie(String domain, String name) {
    List<Cookie> cookies = getCookies(domain);
    if (cookies == null || cookies.size() == 0)
      return null;  // No cookies for given domain
    
    for(Cookie cookie : cookies)
      if (cookie.getName().equals(name))
        return cookie;
    
    return null;    // No cookie with given name
  }
  
  /**
   * Removes all cookies of a given domain
   * @param domain Domain name to remove all cookies for
   */
  public void removeAllCookies(String domain) {
    // Setting domain cookie to an empty string effectively removes
    // all its cookies. 
    mCookieManager.setCookie(domain, "");
  }
  
  /**
   * Remove domain cookie by name
   * @param domain Domain name to remove cookie for
   * @param name Name of the cookie to remove
   */
  public void remove(String domain, String name) {
    List<Cookie> cookies = getCookies(domain);
    for(Cookie c : cookies)
      if (c.getName().equals(name)) {
        cookies.remove(c);
        break;
      }
    mCookieManager.setCookie(domain, cookiesToString(cookies));
  }
  
  /**
   * Add domain cookie
   * @param domain Domain name to add cookie to
   * @param name Name of the cookie to add
   * @param value String value of the cookie 
   */
  public void addCookie(String domain, String name, String value) {
    String cookies = mCookieManager.getCookie(domain);
    // TODO: Check if cookie with this name already exists
    mCookieManager.setCookie(domain, mCookieManager.getCookie(domain) +
        ";" + name + "=" + value);
  }
  
  private String cookiesToString(List<Cookie> cookies) {
    String res = "";
    for (Cookie c : cookies)
      res += c.getName() + "=" + c.getValue() + ";";
    return res;
  }
  
  static SessionCookieManager mInstance = null;
  CookieManager mCookieManager = null;
  static Context mContext = null;
}
