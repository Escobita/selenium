/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium.chrome;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Context;
import org.openqa.selenium.remote.DriverCommand;
import static org.openqa.selenium.remote.DriverCommand.*;
import org.openqa.selenium.remote.SessionId;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChromeDriver implements WebDriver, SearchContext, JavascriptExecutor,
FindsById, FindsByClassName, FindsByLinkText, FindsByName, FindsByTagName, FindsByXPath {

  private final ChromeCommandExecutor executor;
  private final ChromeBinary chromeBinary;
  private final ChromeProfile profile;
  private final ChromeExtension extension;
  
  /**
   * Starts up a new instance of Chrome, with the required extension loaded,
   * and has it connect to a new ChromeCommandExecutor on port 9700
   */
  public ChromeDriver() {
    this(new ChromeProfile(), new ChromeExtension());
  }

  public ChromeDriver(ChromeProfile profile, ChromeExtension extension) {
    // TODO(danielwh): Remove explicit port (blocked on crbug.com 11547)
    this(new ChromeCommandExecutor(9700), new ChromeBinary(), profile, extension);
  }

  private ChromeDriver(ChromeCommandExecutor executor, ChromeBinary chromeBinary,
                       ChromeProfile profile, ChromeExtension extension) {
    this.executor = executor;
    this.chromeBinary = chromeBinary;
    this.profile = profile;
    this.extension = extension;
    startClient();
  }

  /**
   * By default will try to load Chrome from system property
   * webdriver.chrome.bin and the extension from
   * webdriver.chrome.extensiondir.  If the former fails, will try to guess the
   * path to Chrome.  If the latter fails, will try to unzip from the JAR we 
   * hope we're in.  If these fail, throws exceptions.
   */
  protected void startClient() {
    while (!executor.hasClient()) {
      stopClient();
      executor.startListening();
      try {
        chromeBinary.start(profile, extension);
      } catch (IOException e) {
        throw new WebDriverException(e);
      }
      //In case this attempt fails, we increment how long we wait before sending a command
      chromeBinary.incrementBackoffBy(1);
    }
    //The last one attempt succeeded, so we reduce back to that time
    chromeBinary.incrementBackoffBy(-1);
  }
  
  /**
   * Kills the started Chrome process and ChromeCommandExecutor if they exist
   */
  protected void stopClient() {
    chromeBinary.kill();
    executor.stopListening();
  }

  private ChromeResponse execute(DriverCommand driverCommand) {
    return execute(driverCommand, ImmutableMap.<String, Object>of());
  }
  
  /**
   * Executes a passed command using the current ChromeCommandExecutor
   * @param driverCommand command to execute
   * @param parameters parameters of command being executed
   * @return response to the command (a Response wrapping a null value if none) 
   */
  ChromeResponse execute(DriverCommand driverCommand,
                         Map<String, ?> parameters) {
    Command command = new Command(new SessionId("[No sessionId]"),
                                  new Context("[No context]"),
                                  driverCommand,
                                  parameters);
    try {
      return executor.execute(command);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException ||
          e instanceof FatalChromeException) {
        //These exceptions may leave the extension hung, or in an
        //inconsistent state, so we restart Chrome
        stopClient();
        startClient();
      }
      if (e instanceof RuntimeException) {
        throw (RuntimeException)e;
      } else {
        throw new WebDriverException(e);
      }
    }
  }
  
  public void close() {
    execute(CLOSE);
  }

  public WebElement findElement(By by) {
    return by.findElement(this);
  }

  public List<WebElement> findElements(By by) {
    return by.findElements(this);
  }

  public void get(String url) {
    execute(GET, ImmutableMap.of("url", url));
  }

  public String getCurrentUrl() {
    return execute(GET_CURRENT_URL).getValue().toString();
  }

  public String getPageSource() {
    return execute(GET_PAGE_SOURCE).getValue().toString();
  }

  public String getTitle() {
    return execute(GET_TITLE).getValue().toString();
  }

  public String getWindowHandle() {
    return execute(GET_CURRENT_WINDOW_HANDLE).getValue().toString();
  }

  public Set<String> getWindowHandles() {
    List<?> windowHandles = (List<?>)execute(GET_WINDOW_HANDLES).getValue();
    Set<String> setOfHandles = new HashSet<String>();
    for (Object windowHandle : windowHandles) {
      setOfHandles.add((String)windowHandle);
    }
    return setOfHandles;
  }

  public Options manage() {
    return new ChromeOptions();
  }

  public Navigation navigate() {
    return new ChromeNavigation();
  }

  public void quit() {
    try {
      execute(QUIT);
    } finally {
      stopClient();
    }
  }

  public TargetLocator switchTo() {
    return new ChromeTargetLocator();
  }

  public Object executeScript(String script, Object... args) {
    ChromeResponse response;
    response = execute(EXECUTE_SCRIPT, ImmutableMap.of("script", script, "args", args));
    if (response.getStatusCode() == -1) {
      return new ChromeWebElement(this, response.getValue().toString());
    } else {
      return response.getValue();
    }
  }

  public boolean isJavascriptEnabled() {
    return true;
  }

  private WebElement findElementInternal(String using, String value) {
    return getElementFrom(execute(FIND_ELEMENT, ImmutableMap.of("using", using, "value", value)));
  }

  private List<WebElement> findElementsInternal(String using, String value) {
    return getElementsFrom(execute(FIND_ELEMENTS, ImmutableMap.of("using", using, "value", value)));
  }

  public WebElement findElementById(String using) {
    return findElementInternal("id", using);
  }

  public List<WebElement> findElementsById(String using) {
    return findElementsInternal("id", using);
  }

  public WebElement findElementByClassName(String using) {
    return findElementInternal("class name", using);
  }

  public List<WebElement> findElementsByClassName(String using) {
    return findElementsInternal("class name", using);
  }

  public WebElement findElementByLinkText(String using) {
    return findElementInternal("link text", using);
  }

  public List<WebElement> findElementsByLinkText(String using) {
    return findElementsInternal("link text", using);
  }

  public WebElement findElementByName(String using) {
    return findElementInternal("name", using);
  }

  public List<WebElement> findElementsByName(String using) {
    return findElementsInternal("name", using);
  }

  public WebElement findElementByTagName(String using) {
    return findElementInternal("tag name", using);
  }

  public List<WebElement> findElementsByTagName(String using) {
    return findElementsInternal("tag name", using);
  }

  public WebElement findElementByXPath(String using) {
    return findElementInternal("xpath", using);
  }

  public List<WebElement> findElementsByXPath(String using) {
    return findElementsInternal("xpath", using);
  }

  public WebElement findElementByPartialLinkText(String using) {
    return findElementInternal("partial link text", using);
  }
  
  public List<WebElement> findElementsByPartialLinkText(String using) {
    return findElementsInternal("partial link text", using);
  }
  
  WebElement getElementFrom(ChromeResponse response) {
    Object result = response.getValue();
    List<?> elements = (List<?>)result;
    return new ChromeWebElement(this, (String)elements.get(0));
  }

  List<WebElement> getElementsFrom(ChromeResponse response) {
    Object result = response.getValue();
    List<WebElement> elements = new ArrayList<WebElement>();
    for (Object element : (List<?>)result) {
      elements.add(new ChromeWebElement(this, (String)element));
    }
    return elements;
  }
  
  private class ChromeOptions implements Options {

    public void addCookie(Cookie cookie) {
      execute(ADD_COOKIE, ImmutableMap.of("cookie", cookie));
    }

    public void deleteAllCookies() {
      execute(DELETE_ALL_COOKIES);
    }

    public void deleteCookie(Cookie cookie) {
      deleteCookieNamed(cookie.getName());
      execute(DELETE_COOKIE, ImmutableMap.of("name", cookie.getName()));
    }

    public void deleteCookieNamed(String name) {
      execute(DELETE_COOKIE, ImmutableMap.of("name", name));
    }

    public Set<Cookie> getCookies() {
      List<?> result = (List<?>)execute(GET_ALL_COOKIES).getValue();
      Set<Cookie> cookies = new HashSet<Cookie>();
      for (Object cookie : result) {
        cookies.add((Cookie)cookie);
      }
      return cookies;
    }

    public Cookie getCookieNamed(String name) {
      return (Cookie)execute(GET_COOKIE, ImmutableMap.of("name", name)).getValue();
    }
    
    public Speed getSpeed() {
      throw new UnsupportedOperationException("Not yet supported in Chrome");
    }

    public void setSpeed(Speed speed) {
      throw new UnsupportedOperationException("Not yet supported in Chrome");
    }
  }
  
  private class ChromeNavigation implements Navigation {
    public void back() {
      execute(GO_BACK);
    }

    public void forward() {
      execute(GO_FORWARD);
    }

    public void to(String url) {
      get(url);
    }

    public void to(URL url) {
      get(String.valueOf(url));
    }

    public void refresh() {
      execute(REFRESH);
    }
  }
  
  private class ChromeTargetLocator implements TargetLocator {
    public WebElement activeElement() {
      return getElementFrom(execute(GET_ACTIVE_ELEMENT));
    }

    public WebDriver defaultContent() {
      execute(SWITCH_TO_DEFAULT_CONTENT);
      return ChromeDriver.this;
    }

    public WebDriver frame(int frameIndex) {
      execute(SWITCH_TO_FRAME_BY_INDEX, ImmutableMap.of("index", frameIndex));
      return ChromeDriver.this;
    }

    public WebDriver frame(String frameName) {
      execute(SWITCH_TO_FRAME_BY_NAME, ImmutableMap.of("name", frameName));
      return ChromeDriver.this;
    }

    public WebDriver window(String windowName) {
      execute(SWITCH_TO_WINDOW, ImmutableMap.of("windowName", windowName));
      return ChromeDriver.this;
    }
    
  }
}
