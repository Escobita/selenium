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
import com.android.webdriver.sessions.intents.IntentReceiverRegistrar;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.android.intents.AddSessionIntent;
import org.openqa.selenium.android.intents.DeleteSessionIntent;
import org.openqa.selenium.android.intents.DoActionIntent;
import org.openqa.selenium.android.intents.NavigateIntent;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

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
  private int sessionId = -1;
  private String dom = "";

  public AndroidDriver() {
    super();
    Log.e("AndroidDriver", "ctor " + getContext().getPackageName());

    try {
      IntentReceiverRegistrar intentReg =
        new IntentReceiverRegistrar(getContext());
      // Registering all intent receivers
//      intentReg.RegisterReceiver(AddSessionIntent.getInstance(),
//          Intents.INTENT_ADDSESSION);
//      intentReg.RegisterReceiver(new AddSessionIntent(),
//          Intents.INTENT_DELETESESSION);
      
      // Creating new session
      createNewSession();
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
    AddSessionIntent.getInstance().broadcast(DEFAULT_SESSION_CONTEXT,
        getContext(),
        new Callback() {
          public void getInt(int arg0) {
            sessionId = arg0;
          }

          public void getString(String arg0) { }
        }
    );
  }
  
  private String getDOM() {
    Log.d("AndroidDriver:getDOM", "Inside getDOM, session id: " + sessionId);
    DoActionIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        Session.Actions.GET_DOM, null, getContext(),
        new Callback() {
          public void getInt(int arg0) { }

          public void getString(String arg0) {
            Log.d("AndroidDriver:getDOM:Callback", "Callback dom: " + arg0);
            dom = arg0;
          }
        }
    );
    return dom;
  }
  
  public void setProxy(String host, int port) {
    // TODO(abergman): implement: proxyConfig = new ProxyConfig(host, port);
  }

  public String getCurrentUrl() {
    // TODO(abergman): implement
    return "http://some.url.com/";
  }

  public String getTitle() {
    // TODO(abergman): implement
    return "This is a title";
  }

  public void get(String url) {
    Log.d("Navigation", "Session: " + sessionId + " to URL: " + url);
    
    NavigateIntent.getInstance().broadcast(sessionId, DEFAULT_SESSION_CONTEXT,
        getContext(), url);
  }

  public WebElement findElement(By by) {
      return by.findElement((SearchContext)this);
  }

  public List<WebElement> findElements(By by) {
      return by.findElements((SearchContext)this);
  }

  public String getPageSource() {
    // TODO(abergman): we return DOM rather than page source!
    return getDOM();
  }

  public void close() {
    Log.d("Close", "Session: " + sessionId);
    DeleteSessionIntent.getInstance().broadcast(sessionId,
        DEFAULT_SESSION_CONTEXT, getContext());
    sessionId = -1;
    // Create a new session
    createNewSession();
  }

  public void quit() {
    Log.d("Quit", "Session: " + sessionId);
    DeleteSessionIntent.getInstance().broadcast(sessionId,
        DEFAULT_SESSION_CONTEXT, getContext());
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

  public Object executeScript(String script, Object... args) {
    // TODO(abergman): implement
    return null;
  }

  public TargetLocator switchTo() {
    // TODO(abergman): implement
    return null;
  }

  public Navigation navigate() {
    // TODO(abergman): implement
    return null;
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
    // TODO(abergman): implement
    return null;
  }

  public List<WebElement> findElementsById(String id) {
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

  public Options manage() {
    return new AndroidOptions();
  }

  private class AndroidOptions implements Options {
    public void addCookie(Cookie cookie) {
      // TODO(abergman): implement
    }

    public void deleteCookieNamed(String name) {
      // TODO(abergman): implement
    }

    public void deleteCookie(Cookie cookie) {
      // TODO(abergman): implement
    }

    public void deleteAllCookies() {
      // TODO(abergman): implement
    }

    public Set<Cookie> getCookies() {
      // TODO(abergman): implement
      return null;
    }

    public Speed getSpeed() {
        throw new UnsupportedOperationException();
    }

    public void setSpeed(Speed speed) {
        throw new UnsupportedOperationException();
    }
  }

  // Generic interface for integer callbacks
  public interface Callback {
    void getInt(int arg0);
    void getString(String arg0);
  }
 
}
