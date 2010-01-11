package org.openqa.selenium.chrome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Context;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.ErrorHandler;
import static org.openqa.selenium.remote.DriverCommand.*;

import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChromeDriver implements WebDriver, SearchContext, JavascriptExecutor, TakesScreenshot,
  FindsById, FindsByClassName, FindsByLinkText, FindsByName, FindsByTagName, FindsByXPath, FindsByCssSelector {

  private final static int MAX_START_RETRIES = 5;
  private final ChromeCommandExecutor executor;
  private final ChromeBinary chromeBinary;
  private final ErrorHandler errorHandler;

  /**
   * Starts up a new instance of Chrome using the specified profile and
   * extension.
   *
   * @param profile The profile to use.
   * @param extension The extension to use.
   */
  public ChromeDriver(ChromeProfile profile, ChromeExtension extension) {
    chromeBinary = new ChromeBinary(profile, extension);
    executor = new ChromeCommandExecutor();
    errorHandler = new ErrorHandler();
    startClient();
  }

  /**
   * Starts up a new instance of Chrome, with the required extension loaded,
   * and has it connect to a new ChromeCommandExecutor on its port
   *
   * @see ChromeDriver(ChromeProfile, ChromeExtension)
   */
  public ChromeDriver() {
    this(new ChromeProfile(), new ChromeExtension());
  }
  
  /**
   * By default will try to load Chrome from system property
   * webdriver.chrome.bin and the extension from
   * webdriver.chrome.extensiondir.  If the former fails, will try to guess the
   * path to Chrome.  If the latter fails, will try to unzip from the JAR we 
   * hope we're in.  If these fail, throws exceptions.
   */
  protected void startClient() {
    for (int retries = MAX_START_RETRIES; !executor.hasClient() && retries > 0; retries--) {
      stopClient();
      try {
        executor.startListening();
        chromeBinary.start(getServerUrl());
      } catch (IOException e) {
        throw new WebDriverException(e);
      }
      //In case this attempt fails, we increment how long we wait before sending a command
      chromeBinary.incrementBackoffBy(1);
    }
    //The last one attempt succeeded, so we reduce back to that time
    chromeBinary.incrementBackoffBy(-1);

    if (!executor.hasClient()) {
      stopClient();
      throw new FatalChromeException("Cannot create chrome driver");
    }
  }
  
  /**
   * Kills the started Chrome process and ChromeCommandExecutor if they exist
   */
  protected void stopClient() {
    chromeBinary.kill();
    executor.stopListening();
  }
  
  /**
   * Executes a passed command using the current ChromeCommandExecutor
   * @param driverCommand command to execute
   * @param parameters parameters of command being executed
   * @return response to the command (a Response wrapping a null value if none) 
   */
  Response execute(DriverCommand driverCommand, Map<String, ?> parameters) {
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

  /**
   * @param driverCommand The command to execute.
   * @return The command response.
   * @see #execute(DriverCommand, Map)
   */
  Response execute(DriverCommand driverCommand) {
    return execute(driverCommand, ImmutableMap.<String, Object>of());
  }
  
  protected String getServerUrl() {
    return "http://localhost:" + executor.getPort() + "/chromeCommandExecutor";
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
    Response response;
    response = execute(EXECUTE_SCRIPT, ImmutableMap.of("script", script, "args", args));
    if (response.getStatus() == -1) {
      return new ChromeWebElement(this, response.getValue().toString());
    } else {
      return response.getValue();
    }
  }

  public boolean isJavascriptEnabled() {
    return true;
  }

  public WebElement findElementById(String using) {
    return findElement("id", using);
  }

  public List<WebElement> findElementsById(String using) {
    return findElements("id", using);
  }

  public WebElement findElementByClassName(String using) {
    return findElement("class name", using);
  }

  public List<WebElement> findElementsByClassName(String using) {
    return findElements("class name", using);
  }

  public WebElement findElementByLinkText(String using) {
    return findElement("link text", using);
  }

  public List<WebElement> findElementsByLinkText(String using) {
    return findElements("link text", using);
  }

  public WebElement findElementByName(String using) {
    return findElement("name", using);
  }

  public List<WebElement> findElementsByName(String using) {
    return findElements("name", using);
  }

  public WebElement findElementByTagName(String using) {
    return findElement("tag name", using);
  }

  public List<WebElement> findElementsByTagName(String using) {
    return findElements("tag name", using);
  }

  public WebElement findElementByXPath(String using) {
    return findElement("xpath", using);
  }

  public List<WebElement> findElementsByXPath(String using) {
    return findElements("xpath", using);
  }

  public WebElement findElementByPartialLinkText(String using) {
    return findElement("partial link text", using);
  }

  public List<WebElement> findElementsByPartialLinkText(String using) {
    return findElements("partial link text", using);
  }
  
  public WebElement findElementByCssSelector(String using) {
    return findElement("css", using);
  }

  public List<WebElement> findElementsByCssSelector(String using) {
    return findElements("css", using);
  }

  private WebElement findElement(String by, String using) {
    Response response = execute(FIND_ELEMENT, ImmutableMap.of("using", by, "value", using));
    return getElementFrom(response);
  }

  private List<WebElement> findElements(String by, String using) {
    Response response = execute(FIND_ELEMENTS, ImmutableMap.of("using", by, "value", using));
    return getElementsFrom(response);
  }


  WebElement getElementFrom(Response response) {
    Object result = response.getValue();
    List<?> elements = (List<?>)result;
    return new ChromeWebElement(this, (String)elements.get(0));
  }

  List<WebElement> getElementsFrom(Response response) {
    Object result = response.getValue();
    List<WebElement> elements = new ArrayList<WebElement>();
    for (Object element : (List<?>)result) {
      elements.add(new ChromeWebElement(this, (String)element));
    }
    return elements;
  }
  
  public <X> X getScreenshotAs(OutputType<X> target) {
    return target.convertFromBase64Png(execute(SCREENSHOT).getValue().toString());
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
      Set<Cookie> allCookies = getCookies();
      for (Cookie cookie : allCookies) {
        if (name.equals(cookie.getName())) {
          return cookie;
        }
      }
      return null;
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
      Map<String, Object> frameId = Maps.newHashMapWithExpectedSize(1);
      frameId.put("id", null);
      execute(SWITCH_TO_FRAME, frameId);
      return ChromeDriver.this;
    }

    public WebDriver frame(int frameIndex) {
      execute(SWITCH_TO_FRAME, ImmutableMap.of("id", frameIndex));
      return ChromeDriver.this;
    }

    public WebDriver frame(String frameName) {
      execute(SWITCH_TO_FRAME, ImmutableMap.of("id", frameName));
      return ChromeDriver.this;
    }

    public WebDriver window(String windowName) {
      execute(SWITCH_TO_WINDOW, ImmutableMap.of("name", windowName));
      return ChromeDriver.this;
    }
    
  }
}
