/*
 * Copyright 2007-2010 WebDriver committers Copyright 2007-2009 Google Inc.
 * Portions copyright 2007 ThoughtWorks, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Copyright 2007 ThoughtWorks, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.openqa.selenium.android;

import android.content.Context;
import android.util.Log;

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
import org.openqa.selenium.android.intents.AddSessionIntent;
import org.openqa.selenium.android.intents.CookiesIntent;
import org.openqa.selenium.android.intents.DeleteSessionIntent;
import org.openqa.selenium.android.intents.DoActionIntent;
import org.openqa.selenium.android.intents.GetTitleIntent;
import org.openqa.selenium.android.intents.GetUrlIntent;
import org.openqa.selenium.android.intents.NavigateIntent;
import org.openqa.selenium.android.intents.SetProxyIntent;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO(berrada): Add missing implementation for FindsByClassName.
// TODO(berrada): This class needs to extend RemoteWebDriver.
public class AndroidDriver implements WebDriver, SearchContext, FindsByTagName, JavascriptExecutor,
    FindsById, FindsByLinkText, FindsByName, FindsByXPath {

  public static final String DEFAULT_SESSION_CONTEXT = "foo";
  public static final long COMMAND_TIMEOUT = 7000L; // in milliseconds

  private static Context context = null;
  private boolean enableJavascript = true;
  private int sessionId = -1;
  private String pageSource = "";
  private String jsResult = "";
  private String title = "";
  private String url = "";
  private PageElementExtractor extractor = null;


  public AndroidDriver() {
    super();
    Log.e("AndroidDriver", "Context: " + getContext().getPackageName());

    try {
      // Creating new session
      createNewSession();
      Log.w("AndroidDriver:ctor", "session created with id: " + sessionId);
      extractor = new PageElementExtractor(this);
    } catch (Exception e) {
      Log.e("AndroidDriver", "Unable to register receiver: " + e.toString() + "\n"
          + Log.getStackTraceString(e));
    }
  }

  @Override
  public String getCurrentUrl() {
    url = "";
    final Object syncObj_ = new Object();
    GetUrlIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, getContext(),
        new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              url = arg0;
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        });

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) {
      }
    }

    return url;
  }

  @Override
  public String getTitle() {
    title = "";
    final Object syncObj_ = new Object();
    GetTitleIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, getContext(),
        new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              title = arg0;
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        });

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) {
      }
    }

    return title;
  }

  @Override
  public void get(String url) {
    Log.d("Navigation", "Session: " + sessionId + " to URL: " + url);

    boolean navigateOk =
        NavigateIntent.getInstance().broadcastSync(sessionId, DEFAULT_SESSION_CONTEXT,
            getContext(), url, true);

    if (!navigateOk) {
      String message = "Error navigating session: " + sessionId + " to URL: " + url;
      Log.e("AndroidDriver:navigate", message);
      throw new WebDriverException(message);
    }
  }

  @Override
  public String getPageSource() {
    Log.d("AndroidDriver:getPageSource", "Inside!");
    final Object syncObj_ = new Object();
    pageSource = "";
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.GET_PAGESOURCE, null, getContext(), new Callback() {
      @Override
      public void getString(String arg0) {
        pageSource = arg0;
        synchronized (syncObj_) {
          syncObj_.notifyAll();
        }
      }
    });
    synchronized (syncObj_) {
      while (pageSource.length() == 0) {
        try {
          syncObj_.wait(COMMAND_TIMEOUT);
        } catch (InterruptedException ie) {
        }
      }
    }
    return pageSource;
  }

  @Override
  public void close() {
    // Delete the current session
    quit();

    // Create a new session
    createNewSession();
  }

  @Override
  public void quit() {
    Log.d("Quit", "Session: " + sessionId);
    final Object syncObj_ = new Object();
    DeleteSessionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, getContext(),
        new Callback() {
          @Override
          public void getInt(int resultOk) {
            synchronized (this) {
              if (resultOk == 0)
                Log.w("AndroidDriver:Quit", "Error deleting session: " + sessionId);
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        });

    synchronized (syncObj_) {
      try {
        syncObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) {
      }
    }
    sessionId = -1;
  }
  
  @Override
  public WebElement findElement(By by) {
    Log.d("AndroidDriver:findElement", "by: " + by.toString());
    return by.findElement((SearchContext) this);
  }

  @Override
  public List<WebElement> findElements(By by) {
    Log.d("AndroidDriver:findElements", "by: " + by.toString());
    return by.findElements((SearchContext) this);
  }

  @Override
  public WebElement findElementByLinkText(String using) {
    Log.d("Android:findElementByLinkText", "Searching for element by link text: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.linkText(using));
    return element.findElementByLinkText(using);
  }

  @Override
  public List<WebElement> findElementsByLinkText(String using) {
    Log.d("Android:findElementsByLinkText", "Searching for elements by link text: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.linkText(using));
    return element.findElementsByLinkText(using);
  }

  @Override
  public WebElement findElementById(String id) {
    Log.d("Android:findElementById", "Searching for element by Id: " + id);
    AndroidWebElement element = new AndroidWebElement(this, By.id(id));
    return element.findElementById(id);
  }

  @Override
  public List<WebElement> findElementsById(String id) {
    Log.d("Android:findElementById", "Searching for elements by Id: " + id);
    return findElementsByXPath("//*[@id='" + id + "']");
  }

  @Override
  public WebElement findElementByName(String using) {
    Log.d("Android:findElementByName", "Searching for element by name: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.name(using));
    return element.findElementByName(using);
  }

  @Override
  public List<WebElement> findElementsByName(String using) {
    Log.d("Android:findElementsByName", "Searching for elements by name: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.name(using));
    return element.findElementsByName(using);
  }

  public WebElement findElementByTagName(String using) {
    Log.d("Android:findElementByName", "Searching for element by tag name: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.tagName(using));
    return element.findElementByTagName(using);
  }

  public List<WebElement> findElementsByTagName(String using) {
    Log.d("Android:findElementsByTagName", "Searching for elements by tag name: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.tagName(using));
    return element.findElementsByTagName(using);
  }

  @Override
  public WebElement findElementByXPath(String selector) {
    Log.d("Android:findElementByXPath", "Searching for element by XPath: " + selector);
    AndroidWebElement element = new AndroidWebElement(this, By.xpath(selector));
    return element.findElementByXPath(selector);
  }

  @Override
  public List<WebElement> findElementsByXPath(String using) {
    Log.d("Android:findElementsByXPath", "Searching for elements by XPath: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.xpath(using));
    return element.findElementsByXPath(using);
  }

  @Override
  public WebElement findElementByPartialLinkText(String using) {
    Log.d("Android:findElementByPartialLinkText", "Searching for element by partial link text: "
        + using);
    AndroidWebElement element = new AndroidWebElement(this, By.partialLinkText(using));
    return element.findElementByPartialLinkText(using);
  }

  @Override
  public List<WebElement> findElementsByPartialLinkText(String using) {
    Log.d("Android:findElementsByPartialLinkText",
        "Searching for elements by partial link text: " + using);
    AndroidWebElement element = new AndroidWebElement(this, By.linkText(using));
    return element.findElementsByPartialLinkText(using);
  }

  @Override
  public Set<String> getWindowHandles() {
    Set<String> allHandles = new HashSet<String>();
    // TODO(abergman): implement
    return allHandles;
  }

  @Override
  public String getWindowHandle() {
    // TODO(abergman): implement
    return null;
  }
  
  @Override
  public TargetLocator switchTo() {
    // TODO(abergman): implement
    return null;
  }

  @Override
  public Navigation navigate() {
    return new AndroidNavigation();
  }
  
  @Override
  public boolean isJavascriptEnabled() {
    return enableJavascript;
  }
  
  /**
   * Execute the given Javascript within the context of the currently selected window. This does
   * not wait for the returned value of the script if any.
   * 
   * @param script The Javascript to execute
   * @param args The arguments to the script. May be empty
   */
  public void executeScriptNoResults(String script, Object... args) {
    
    String funcName = "func_" + Math.round(Math.random() * 1000000);
    StringBuilder jsFunction = new StringBuilder();
    jsFunction.append("var ").append(funcName).append(" = function(){")
        .append(script).append("}; window.webdriver.resultMethod(").append(funcName).append("(");
    
    for(int i = 0; i < args.length; i++) {
      jsFunction.append(JsUtil.convertArgumentToJsObject(args[i]))
          .append(( i == args.length-1) ? ")); delete " : ",");
    }
    jsFunction.append(funcName);

    String[] intentArgs = {jsFunction.toString()};
    
    Log.d("AndroidDriver:executeScriptNoResults", "Inside executeScript, session id: " + sessionId
        + ", script: " + (intentArgs.length > 0 ? intentArgs[0].toString() : ""));
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.EXECUTE_JAVASCRIPT, intentArgs, getContext(), null);
  }
  
  // TODO(berrada): Right now this does not take arguments of type WebElement, this should
  // be added to be consistent with the JavascriptExecutor API.
  public Object executeScript(String script, Object... args) {
    final Object syncjsObj_ = new Object();

    String funcName = "func_" + Math.round(Math.random() * 1000000);
    StringBuilder jsFunction = new StringBuilder();
    jsFunction.append("var ").append(funcName).append(" = function(){")
        .append(script).append("}; window.webdriver.resultMethod(").append(funcName).append("(");
    
    for(int i = 0; i < args.length; i++) {
      jsFunction.append(JsUtil.convertArgumentToJsObject(args[i]))
          .append(( i == args.length-1) ? ")); delete " : ",");
    }
    jsFunction.append(funcName);

    String[] intentArgs = {jsFunction.toString()};
    
    Log.d("AndroidDriver:executeScript", "Inside executeScript, session id: " + sessionId
        + ", script: " + (intentArgs.length > 0 ? intentArgs[0].toString() : ""));
    jsResult = "";
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.EXECUTE_JAVASCRIPT, intentArgs, getContext(), new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              jsResult = arg0;
              synchronized (syncjsObj_) {
                syncjsObj_.notifyAll();
              }
            }
          }
        });
    synchronized (syncjsObj_) {
      try {
        syncjsObj_.wait(COMMAND_TIMEOUT);
      } catch (InterruptedException ie) {
        Log.d("Android:executeScript", "InterruptedException : " + ie.getStackTrace());
      }
    }
    Runtime.getRuntime().gc(); // Cleaning memory after we're done
    return jsResult;
  }

  public void setProxy(String host, int port) {
    Log.d("AndroidDriver:setProxy", "Inside setProxy, host: " + host + " port:" + port);
    SetProxyIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, host,
        String.valueOf(port), getContext());
  }
  
  public void setJavascriptEnabled(boolean enableJavascript) {
    this.enableJavascript = enableJavascript;
    // TODO(abergman): implement
  }

  public static void setContext(Context _context) {
    context = _context;
  }

  public static Context getContext() {
    return context;
  }
  
  public void setExtractor(PageElementExtractor extractor) {
    this.extractor = extractor;
  }

  public PageElementExtractor getExtractor() {
    return extractor;
  }
  
  private void createNewSession() {
    final Object syncObj_ = new Object();
    AddSessionIntent.getInstance().broadcast(DEFAULT_SESSION_CONTEXT, getContext(), new Callback() {
      @Override
      public void getInt(int arg0) {
        sessionId = arg0;
        synchronized (syncObj_) {
          syncObj_.notifyAll();
        }
      }
    });
    synchronized (syncObj_) {
      while (sessionId == -1) {
        try {
          syncObj_.wait(COMMAND_TIMEOUT);
        } catch (InterruptedException ie) {
        }
      }
    }
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
      DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, Actions.REFRESH,
          null, getContext(), null);
    }

    public void to(String url) {
      get(url);
    }

    public void to(URL url) {
      get(url.toString());
    }
  }

  @Override
  public Options manage() {
    return new AndroidOptions();
  }

  private class AndroidOptions implements Options {
    public void addCookie(Cookie cookie) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT, CookieActions.ADD,
          new String[] {cookie.getName(), cookie.getValue()}, getContext(), null);
    }

    public void deleteCookieNamed(String name) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE, new String[] {name}, getContext(), null);
    }

    public void deleteCookie(Cookie cookie) {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE, new String[] {cookie.getName()}, getContext(), null);
    }

    public void deleteAllCookies() {
      CookiesIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
          CookieActions.REMOVE_ALL, null, getContext(), null);
    }

    public Set<Cookie> getCookies() {
      Set<Cookie> cookies = new HashSet<Cookie>();
      String cookieString =
          CookiesIntent.getInstance().broadcastSync(sessionId, DEFAULT_SESSION_CONTEXT,
              CookieActions.GET_ALL, null, getContext());

      for (String cookie : cookieString.split(";")) {
        String[] cookieValues = cookie.split("=");
        if (cookieValues.length != 2) throw new RuntimeException("Invalid cookie: " + cookie);
        cookies.add(new Cookie(cookieValues[0], cookieValues[1]));
      }

      return cookies;
    }

    public Cookie getCookieNamed(String name) {
      String cookieValue =
          CookiesIntent.getInstance().broadcastSync(sessionId, DEFAULT_SESSION_CONTEXT,
              CookieActions.GET, new String[] {name}, getContext());

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
