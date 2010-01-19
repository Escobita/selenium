/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

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

package org.openqa.selenium.android;

import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.Session.Actions;
import com.android.webdriver.sessions.SessionCookieManager.CookieActions;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.android.intents.*;
import org.openqa.selenium.android.Callback;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

public class AndroidDriver implements WebDriver, SearchContext,
  JavascriptExecutor, FindsById, FindsByLinkText, FindsByXPath, FindsByName,
  FindsByTagName {

  private boolean enableJavascript = true;
  private static Context context = null;

  public static final String DEFAULT_SESSION_CONTEXT = "foo";
  public static final long COMMAND_TIMEOUT = 3000L; // in milliseconds
  
  private int sessionId = -1;
  private String pageSource = "", jsResult = "", title = "", url = "";

  PageElementExtractor extractor = null;


  public AndroidDriver() {
    super();
    Log.e("AndroidDriver", "Context: " + getContext().getPackageName());

    try {
      // Creating new session
      createNewSession();
      Log.w("AndroidDriver:ctor", "session created with id: " + sessionId);
      extractor = new PageElementExtractor(this);
    } catch (Exception e) {
        Log.e("AndroidDriver", "Unable to register receiver: " +
            e.toString() + "\n" + Log.getStackTraceString(e));
    }
  }

  public static void setContext(Context _context)
  {
    context = _context;
  }
  
  public static Context getContext()
  {
    return context;
  }

  private void createNewSession() {
    final Object syncObj_ = new Object();
    AddSessionIntent.getInstance().broadcast(DEFAULT_SESSION_CONTEXT,
        getContext(),
        new Callback() {
          @Override
          public void getInt(int arg0) {
            sessionId = arg0;
            synchronized (syncObj_) {
              syncObj_.notifyAll();
            }
          }
        }
    );
    synchronized (syncObj_) {
      while(sessionId == -1) {
        try {
          syncObj_.wait(COMMAND_TIMEOUT);
        } catch (InterruptedException ie) { }
      }
    }
  }
  
  public void setProxy(String host, int port) {
    Log.d("AndroidDriver:setProxy", "Inside setProxy, host: " + host +
        " port:" + port);
    
    SetProxyIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        host, String.valueOf(port), getContext());
  }

  public String getCurrentUrl() {
    url = "";
    final Object syncObj_ = new Object();
    GetUrlIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        getContext(), new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              url = arg0;
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        }
    );

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) { }
    }
    
    return url;
  }

  public String getTitle() {
    title = "";
    final Object syncObj_ = new Object();
    GetTitleIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        getContext(), new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              title = arg0;
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        }
    );

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) { }
    }
    
    return title;
  }

  public void get(String url) {
    Log.d("Navigation", "Session: " + sessionId + " to URL: " + url);

    boolean navigateOk = NavigateIntent.getInstance().broadcastSync(sessionId,
        DEFAULT_SESSION_CONTEXT, getContext(), url, true);

    if (!navigateOk) {
      String message = "Error navigating session: " +
          sessionId + " to URL: " + url;
      Log.e("AndroidDriver:navigate", message);
      throw new WebDriverException(message);
    }
  }

  public WebElement findElement(By by) {
    Log.d("AndroidDriver:findElement", "by: " + by.toString());
    return by.findElement((SearchContext)this);
  }

  public List<WebElement> findElements(By by) {
    Log.d("AndroidDriver:findElements", "by: " + by.toString());
    return by.findElements((SearchContext)this);
  }

  public String getPageSource() {
    Log.d("AndroidDriver:getPageSource", "Inside!");
    final Object syncObj_ = new Object();
    pageSource = "";
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.GET_PAGESOURCE, null, getContext(),
        new Callback() {
          @Override
          public void getString(String arg0) {
            pageSource = arg0;
            synchronized (syncObj_) {
              syncObj_.notifyAll();
            }
          }
        }
    );
    synchronized (syncObj_) {
      while(pageSource.length() == 0) {
        try {
          syncObj_.wait(COMMAND_TIMEOUT);
        } catch (InterruptedException ie) { }
      }
    }
    return pageSource;
  }

  public void close() {
    // Delete the current session
    quit();
    
    // Create a new session
    createNewSession();
  }

  public void quit() {
    Log.d("Quit", "Session: " + sessionId);
    final Object syncObj_ = new Object();
    DeleteSessionIntent.getInstance().broadcast(sessionId,
      DEFAULT_SESSION_CONTEXT, getContext(),
      new Callback() {
        @Override
        public void getInt(int resultOk) {
          synchronized (this) {
            if (resultOk == 0)
              Log.w("AndroidDriver:Quit", "Error deleting session: " +
                  sessionId);
            synchronized (syncObj_) {
              syncObj_.notifyAll();
            }
          }
        }
      }
    );

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) { }
    }
    sessionId = -1;
  }

  public Set<String> getWindowHandles() {
    Set<String> allHandles = new HashSet<String>();
    // TODO(abergman): implement
    return allHandles;
  }

  public String getWindowHandle() {
    // TODO(abergman): implement
    return null;
  }

  public void executeScriptNoResults(String script) {
    String funcName = "func_" + Math.round(Math.random() * 1000000); 
    String scriptFunction = "var " + funcName + " = function() {" + script +
      "}; " + funcName + "(); delete " + funcName;
    String[] arguments = {scriptFunction};
    Log.d("AndroidDriver:executeScriptNoResults",
        "Inside executeScript, session id: " + sessionId + ", script: " +
        (arguments.length > 0 ? arguments[0].toString() : ""));
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.EXECUTE_JAVASCRIPT, arguments, getContext(), null);
  }
  
  public Object executeScript(String script, Object... args) {
    final Object syncjsObj_ = new Object();

    String funcName = "func_" + Math.round(Math.random() * 1000000); 
    String scriptFunction = "var " + funcName + " = function() {" + script +
      "}; window.webdriver.resultMethod(" + funcName +
      "()); delete " + funcName;
    String[] arguments = {scriptFunction};
    Log.d("AndroidDriver:executeScript", "Inside executeScript, session id: " +
        sessionId + ", script: " +
        (arguments.length > 0 ? arguments[0].toString() : ""));
    jsResult = "";
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.EXECUTE_JAVASCRIPT, arguments, getContext(),
        new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              jsResult = arg0;
              synchronized (syncjsObj_) {
                syncjsObj_.notifyAll();
              }
            }
          }
        }
    );
    synchronized (syncjsObj_) {
      try {
        syncjsObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) { }
    }
    Runtime.getRuntime().gc();  // Cleaning memory after we're done
    return jsResult;
  }

  public TargetLocator switchTo() {
    // TODO(abergman): implement
    return null;
  }

  public Navigation navigate() {
    return new AndroidNavigation();
  }

  public WebElement findElementByLinkText(String selector) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByLinkText(String selector) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementById(String id) {
    Log.d("Android:findElementById", "Searching for element by Id: " + id);

    try {
      AndroidWebElement element = new AndroidWebElement(this, By.id(id));
      return element.findElementById(id);
    } catch (Exception e) {
      Log.e("Android:findElementById", "Exception: " + e.getMessage() +
          ", id: " + id);
    }
    return null;
  }

  public List<WebElement> findElementsById(String id) {
    Log.d("Android:findElementById", "Searching for elements by Id: " + id);
    return findElementsByXPath("//*[@id='" + id + "']");
  }

  public WebElement findElementByName(String name) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByName(String using) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementByTagName(String name) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByTagName(String using) {
    // TODO(abergman): implement
    return null;
  }
  
  public WebElement findElementByXPath(String selector) {
    // TODO(abergman): implement
    return null;
  }

  public WebElement findElementByPartialLinkText(String using) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByPartialLinkText(String using) {
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsByXPath(String selector) {
    // TODO(abergman): implement
    return null;
  }

  public boolean isJavascriptEnabled() {
    return enableJavascript;
  }

  public void setJavascriptEnabled(boolean enableJavascript) {
    this.enableJavascript = enableJavascript;
    // TODO(abergman): implement
  }

  private class AndroidNavigation implements Navigation {

    public void back() {
      DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          Actions.NAVIGATE_BACK, null, getContext(), null);
    }

    public void forward() {
      DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          Actions.NAVIGATE_FORWARD, null, getContext(), null);
    }

    public void refresh() {
      DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          Actions.REFRESH, null, getContext(), null);
    }

    public void to(String url) {
      get(url);
    }

    public void to(URL url) {
      get(url.toString());
    }
  }
  
  public Options manage() {
    return new AndroidOptions();
  }

  private class AndroidOptions implements Options {
    public void addCookie(Cookie cookie) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.ADD, new String[] {cookie.getName(), cookie.getValue()},
          getContext(), null);
    }

    public void deleteCookieNamed(String name) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE, new String[] { name }, getContext(), null);
    }

    public void deleteCookie(Cookie cookie) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE, new String[] { cookie.getName() },
          getContext(), null);
    }

    public void deleteAllCookies() {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE_ALL, null, getContext(), null);
    }

    public Set<Cookie> getCookies() {
      Set<Cookie> cookies = new HashSet<Cookie>();
      String cookieString = CookiesIntent.getInstance().broadcastSync(sessionId,
          DEFAULT_SESSION_CONTEXT, CookieActions.GET_ALL, null, getContext());

      for(String cookie : cookieString.split(";")) {
        String[] cookieValues = cookie.split("=");
        if (cookieValues.length != 2)
          throw new RuntimeException("Invalid cookie: " + cookie);
        cookies.add(new Cookie(cookieValues[0], cookieValues[1]));
      }

      return cookies;
    }

    public Cookie getCookieNamed(String name) {
      String cookieValue = CookiesIntent.getInstance().broadcastSync(sessionId,
          DEFAULT_SESSION_CONTEXT, CookieActions.GET, new String[] { name },
          getContext());
      
      if (cookieValue.length() > 0)
        return new Cookie(name, cookieValue);
      else
        return null;
    }

    public Speed getSpeed() {
        throw new UnsupportedOperationException();
    }

    public void setSpeed(Speed speed) {
        throw new UnsupportedOperationException();
    }
  }
}
