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

package org.openqa.selenium.firefox;

import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.IllegalLocatorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.internal.ExtensionConnectionFactory;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.internal.FileHandler;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.ReturnedCookie;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.remote.DriverCommand;
import static org.openqa.selenium.remote.DriverCommand.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.openqa.selenium.OutputType.FILE;


/**
 * An implementation of the {#link WebDriver} interface that drives Firefox. This works through a firefox extension,
 * which can be installed via the {#link FirefoxLauncher}. Important system variables are:
 * <ul>
 * <li><b>webdriver.firefox.bin</b> - Which firefox binary to use (normally "firefox" on the PATH).</li>
 * <li><b>webdriver.firefox.profile</b> - The name of the profile to use (normally "WebDriver").</li>
 * </ul>
 * <p/>
 * When the driver starts, it will make a copy of the profile it is using, rather than using that profile directly.
 * This allows multiple instances of firefox to be started.
 */
public class FirefoxDriver implements WebDriver, JavascriptExecutor, TakesScreenshot,
        FindsById, FindsByClassName, FindsByCssSelector,
        FindsByLinkText, FindsByName, FindsByTagName, FindsByXPath {
    public static final int DEFAULT_PORT = 7055;
    // For now, only enable native events on Windows
    public static final boolean DEFAULT_ENABLE_NATIVE_EVENTS =
      Platform.getCurrent().is(Platform.WINDOWS);

  private final ExtensionConnection extension;
  protected Context context;

  public FirefoxDriver() {
    this(new FirefoxBinary(), null);
  }

  public FirefoxDriver(String profileName) {
    this(profileName, DEFAULT_PORT);
  }

  /**
   * @deprecated Use "new ProfilesIni.getProfile(profileName)" and set the port on the returned profile
   */
  @Deprecated
  public FirefoxDriver(String profileName, int port) {
    this(new FirefoxBinary(), modifyProfile(profileName, port));
  }

  private static FirefoxProfile modifyProfile(String profileName, int port) {
    FirefoxProfile profile = new ProfilesIni().getProfile(profileName);
    profile.setPort(port);
    return profile;
  }

  public FirefoxDriver(FirefoxProfile profile) {
    this(new FirefoxBinary(), profile);
  }

  public FirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
    if (profile == null) {
      String suggestedProfile = System.getProperty("webdriver.firefox.profile");
      if (suggestedProfile != null) {
        profile = new ProfilesIni().getProfile(suggestedProfile);
      } else {
        profile = ProfileManager.getInstance().createProfile(binary, DEFAULT_PORT);
      }
    } else {
      profile.addWebDriverExtensionIfNeeded(false);
    }
    prepareEnvironment();

    extension = connectTo(binary, profile, "localhost");
    fixId();
  }

  private FirefoxDriver(ExtensionConnection extension, Context context) {
    this.extension = extension;
    this.context = context;
  }

  protected ExtensionConnection connectTo(FirefoxBinary binary, FirefoxProfile profile, String host) {
    return ExtensionConnectionFactory.connectTo(binary, profile, host);
  }

  protected void prepareEnvironment() {
    // Does nothing, but provides a hook for subclasses to do "stuff"
  }

  public void close() {
    try {
      sendMessage(WebDriverException.class, CLOSE);
    } catch (Exception e) {
      // All good
    }
  }

  public String getPageSource() {
    return sendMessage(WebDriverException.class, GET_PAGE_SOURCE);
  }

  public void get(String url) {
    sendMessage(WebDriverException.class, GET, ImmutableMap.of("url", url));
  }

  public String getCurrentUrl() {
    return sendMessage(WebDriverException.class, GET_CURRENT_URL);
  }

  public String getTitle() {
    return sendMessage(WebDriverException.class, GET_TITLE);
  }

  public List<WebElement> findElements(By by) {
    return by.findElements(this);
  }

  public WebElement findElement(By by) {
    return by.findElement(this);
  }

  public WebElement findElementById(String using) {
    return findElement("id", using);
  }

  public List<WebElement> findElementsById(String using) {
    return findElements("id", using);
  }

  public WebElement findElementByLinkText(String using) {
    return findElement("link text", using);
  }

  public List<WebElement> findElementsByLinkText(String using) {
    return findElements("link text", using);
  }

  public WebElement findElementByPartialLinkText(String using) {
    return findElement("partial link text", using);
  }

  public List<WebElement> findElementsByPartialLinkText(String using) {
    return findElements("partial link text", using);
  }

  public List<WebElement> findElementsByXPath(String using) {
    return findElements("xpath", using);
  }

  public WebElement findElementByXPath(String using) {
    return findElement("xpath", using);
  }

  public List<WebElement> findElementsByClassName(String using) {
    if (using == null) {
      throw new IllegalArgumentException(
          "Cannot find elements when the class name expression is null.");
    }

    if (using.matches(".*\\s+.*")) {
      throw new IllegalLocatorException(
          "Compound class names are not supported. Consider searching for one class name and filtering the results.");
    }

    return findElements("class name", using);
  }

  public WebElement findElementByClassName(String using) {
    if (using == null) {
      throw new IllegalArgumentException(
          "Cannot find elements when the class name expression is null.");
    }

    if (using.matches(".*\\s+.*")) {
      throw new IllegalLocatorException(
          "Compound class names are not supported. Consider searching for one class name and filtering the results.");
    }

    return findElement("class name", using);
  }

  public WebElement findElementByCssSelector(String using) {
    if (using == null)
     throw new IllegalArgumentException("Cannot find elements when the css selector is null.");

    return findElement("css selector", using);
  }

  public List<WebElement> findElementsByCssSelector(String using) {
    if (using == null)
     throw new IllegalArgumentException("Cannot find elements when the css selector is null.");

    return findElements("css selector", using);
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

  private WebElement findElement(String method, String selector) {
    String elementId = sendMessage(NoSuchElementException.class,
        FIND_ELEMENT, ImmutableMap.of("using", method, "value", selector));

    return new FirefoxWebElement(this, elementId);
  }

  private List<WebElement> findElements(String method, String selector) {
    String returnedIds = sendMessage(WebDriverException.class,
        FIND_ELEMENTS, ImmutableMap.of("using", method, "value", selector));
    List<WebElement> elements = new ArrayList<WebElement>();

    if (returnedIds.length() == 0) {
      return elements;
    }

    String[] ids = returnedIds.split(",");
    for (String id : ids) {
      elements.add(new FirefoxWebElement(this, id));
    }
    return elements;
  }

  public TargetLocator switchTo() {
    return new FirefoxTargetLocator();
  }


  public Navigation navigate() {
    return new FirefoxNavigation();
  }

  protected WebDriver findActiveDriver() {
    String response = sendMessage(WebDriverException.class, DriverCommand._NEW_SESSION);

    Context newContext = new Context(response);
    if (newContext.getDriverId().equals(newContext.getDriverId())) {
      return this;
    }
    return new FirefoxDriver(extension, newContext);
  }

  private String sendMessage(Class<? extends WebDriverException> throwOnFailure,
                             DriverCommand driverCommand) {
    Map<String, Object> noArgs = ImmutableMap.of();
    return sendMessage(throwOnFailure, driverCommand, noArgs);
  }

  private String sendMessage(Class<? extends WebDriverException> throwOnFailure,
                             DriverCommand driverCommand, Map<String, ?> parameters) {
    return sendMessage(throwOnFailure, new Command(context, driverCommand, parameters));
  }

  protected String sendMessage(Class<? extends RuntimeException> throwOnFailure, Command command) {
    Response response = extension.sendMessageAndWaitForResponse(throwOnFailure, command);
    context = response.getContext();
    response.ifNecessaryThrow(throwOnFailure);
    return response.getResponseText();
  }

  private void fixId() {
    String response = sendMessage(WebDriverException.class, _NEW_SESSION);
    this.context = new Context(response);
  }

  public void quit() {
    extension.quit();
  }

  public String getWindowHandle() {
    return sendMessage(WebDriverException.class, GET_CURRENT_WINDOW_HANDLE);
  }

  public Set<String> getWindowHandles() {
    String allHandles = sendMessage(WebDriverException.class, GET_WINDOW_HANDLES);
    String[] handles = allHandles.split(",");
    HashSet<String> toReturn = new HashSet<String>();
    toReturn.addAll(Arrays.asList(handles));
    return toReturn;
  }

  public Object executeScript(String script, Object... args) {
    // Escape the quote marks
    script = script.replaceAll("\"", "\\\"");

    Object[] convertedArgs = convertToJsObjects(args);

    Command command = new Command(context, null, EXECUTE_SCRIPT,
        ImmutableMap.of("script", script, "args", convertedArgs));
    Response response = extension.sendMessageAndWaitForResponse(WebDriverException.class, command);
    context = response.getContext();
    response.ifNecessaryThrow(WebDriverException.class);

    return parseJavascriptObjectFromResponse(
        (String) response.getExtraResult("resultType"),
        response.getExtraResult("response"));
  }

  public Object parseJavascriptObjectFromResponse(String resultType, Object response) {
    if ("NULL".equals(resultType)) {
      return null;
    }

    if ("ARRAY".equals(resultType)) {
      List<Object> list = new ArrayList<Object>();
      try {
        JSONArray array = (JSONArray) response;
        for (int i = 0; i < array.length(); ++i) {
          //They really should all be JSONObjects of form {resultType, response}
          JSONObject subObject = (JSONObject) array.get(i);
          list.add(parseJavascriptObjectFromResponse(
              subObject.getString("resultType"), subObject.get("response")));
        }
      } catch (JSONException e) {
        throw new WebDriverException(e);
      }
      return list;
    }
    if ("ELEMENT".equals(resultType)) {
      return new FirefoxWebElement(this, (String) response);
    }

    if (response instanceof Integer) {
      return new Long((Integer) response);
    }
    return response;
  }

  public boolean isJavascriptEnabled() {
    return true;
  }

  private Object[] convertToJsObjects(Object[] args) {
    if (args.length == 0) {
      return new Object[0];
    }

    Object[] converted = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      converted[i] = convertToJsObject(args[i]);
    }

    return converted;
  }

  private Object convertToJsObject(Object arg) {
    Map<String, Object> converted = new HashMap<String, Object>();

    if (arg instanceof String) {
      converted.put("type", "STRING");
      converted.put("value", arg);
    } else if (arg instanceof Double || arg instanceof Float) {
      converted.put("type", "NUMBER");
      converted.put("value", ((Number) arg).doubleValue());
    } else if (arg instanceof Number) {
      converted.put("type", "NUMBER");
      converted.put("value", ((Number) arg).longValue());
    } else if (isPrimitiveNumberType(arg)) {
      converted.put("type", "NUMBER");
      converted.put("value", getPrimitiveTypeAsLong(arg));
    } else if (arg instanceof Boolean) {
      converted.put("type", "BOOLEAN");
      converted.put("value", ((Boolean) arg).booleanValue());
    } else if (arg.getClass() == boolean.class) {
      converted.put("type", "BOOLEAN");
      converted.put("value", arg);
    } else if (arg instanceof FirefoxWebElement) {
      converted.put("type", "ELEMENT");
      converted.put("value", ((FirefoxWebElement) arg).getElementId());
    } else if (arg instanceof Collection<?>) {
      Collection<?> args = ((Collection<?>) arg);
      Object[] list = new Object[args.size()];
      int i = 0;
      for (Object o : args) {
        list[i] = convertToJsObject(o);
        i++;
      }
      return list;
    } else {
      throw new IllegalArgumentException("Argument is of an illegal type: " + arg);
    }

    return converted;
  }

  private Long getPrimitiveTypeAsLong(Object arg) {
    return Long.valueOf(String.valueOf(arg)); // Clever
  }

  private boolean isPrimitiveNumberType(Object arg) {
    if (!arg.getClass().isPrimitive()) {
      return false;
    }

    return arg.getClass() == long.class ||
        arg.getClass() == int.class ||
        arg.getClass() == short.class; // And so on. That's the common case done :)
  }

  public Options manage() {
    return new FirefoxOptions();
  }

  private class FirefoxOptions implements Options {
    private final List<String> fieldNames =
        Arrays.asList("domain", "expiry", "name", "path", "value", "secure");
    private final DateFormat RFC_1123_DATE_FORMAT =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'Z", Locale.US);
    //TODO: need to refine the values here
    private final int SLOW_SPEED = 1;
    private final int MEDIUM_SPEED = 10;
    private final int FAST_SPEED = 100;

    public void addCookie(Cookie cookie) {
      sendMessage(WebDriverException.class, ADD_COOKIE, ImmutableMap.of("cookie", cookie));
    }


    public Cookie getCookieNamed(String name) {
      Set<Cookie> allCookies = getCookies();
      for (Cookie cookie : allCookies) {
        if (cookie.getName().equals(name)) {
          return cookie;
        }
      }
      return null;
    }

    public Set<Cookie> getCookies() {
      String response = sendMessage(WebDriverException.class, GET_ALL_COOKIES).trim();
      Set<Cookie> cookies = new HashSet<Cookie>();

      if (!"".equals(response)) {
        for (String cookieString : response.split("\n")) {
          if ("".equals(cookieString.trim())) {
            continue;
          }

          HashMap<String, String> attributesMap = new HashMap<String, String>();
          attributesMap.put("name", "");
          attributesMap.put("value", "");
          attributesMap.put("domain", "");
          attributesMap.put("path", "");
          attributesMap.put("expires", "");
          attributesMap.put("secure", "false");

          for (String attribute : cookieString.split(";")) {
            if (attribute.contains("=")) {
              String[] tokens = attribute.trim().split("=", 2);
              if (attributesMap.get("name").equals("")) {
                attributesMap.put("name", tokens[0]);
                attributesMap.put("value", tokens[1]);
              } else if ("domain".equals(tokens[0])
                  && tokens[1].trim().startsWith(".")) {
                //convert " .example.com" into "example.com" format
                int offset = tokens[1].indexOf(".") + 1;
                attributesMap.put("domain", tokens[1].substring(offset));
              } else if (tokens.length > 1) {
                attributesMap.put(tokens[0], tokens[1]);
              }
            } else if (attribute.equals("secure")) {
              attributesMap.put("secure", "true");
            }
          }
          Date expires = null;
          String expiry = attributesMap.get("expires");
          if (expiry != null && !"".equals(expiry) && !expiry.equals("0")) {
            //firefox stores expiry as number of seconds
            expires = new Date(Long.parseLong(attributesMap.get("expires")) * 1000);
          }

          cookies.add(new ReturnedCookie(attributesMap.get("name"), attributesMap.get("value"),
              attributesMap.get("domain"), attributesMap.get("path"),
              expires, Boolean.parseBoolean(attributesMap.get("secure"))));
        }

      }
      return cookies;
    }

    public void deleteCookieNamed(String name) {
      sendMessage(WebDriverException.class, DELETE_COOKIE, ImmutableMap.of("name", name));
    }

    public void deleteCookie(Cookie cookie) {
      deleteCookieNamed(cookie.getName());
    }

    public void deleteAllCookies() {
      sendMessage(WebDriverException.class, DELETE_ALL_COOKIES);
    }

    public Speed getSpeed() {
      int pixelSpeed = Integer.parseInt(sendMessage(WebDriverException.class, GET_SPEED));
      Speed speed;

      // TODO: simon 2007-02-01; Delegate to the enum
      switch (pixelSpeed) {
        case SLOW_SPEED:
          speed = Speed.SLOW;
          break;
        case MEDIUM_SPEED:
          speed = Speed.MEDIUM;
          break;
        case FAST_SPEED:
          speed = Speed.FAST;
          break;
        default:
          //TODO: log a warning here
          speed = Speed.FAST;
          break;
      }
      return speed;
    }

    public void setSpeed(Speed speed) {
      int pixelSpeed;
      // TODO: simon 2007-02-01; Delegate to the enum
      switch (speed) {
        case SLOW:
          pixelSpeed = SLOW_SPEED;
          break;
        case MEDIUM:
          pixelSpeed = MEDIUM_SPEED;
          break;
        case FAST:
          pixelSpeed = FAST_SPEED;
          break;
        default:
          throw new IllegalArgumentException();
      }
      sendMessage(WebDriverException.class, SET_SPEED,
          ImmutableMap.of("speed", String.valueOf(pixelSpeed)));
    }
  }

  private class FirefoxTargetLocator implements TargetLocator {
    public WebDriver frame(int frameIndex) {
      return frame(String.valueOf(frameIndex));
    }

    public WebDriver frame(String frameName) {
      sendMessage(NoSuchFrameException.class, SWITCH_TO_FRAME,
          ImmutableMap.of("id", frameName));
      return FirefoxDriver.this;
    }

    public WebDriver window(String windowName) {
      String response = sendMessage(NoSuchWindowException.class, SWITCH_TO_WINDOW,
          ImmutableMap.of("name", windowName));
      if (response == null || "No window found".equals(response)) {
        throw new NoSuchWindowException("Cannot find window: " + windowName);
      }
      try {
        FirefoxDriver.this.context = new Context(response);
      } catch (NumberFormatException e) {
        throw new WebDriverException(
            "When switching to window: " + windowName + " ---- " + response);
      }
      return FirefoxDriver.this;
    }

    public WebDriver defaultContent() {
      sendMessage(WebDriverException.class, SWITCH_TO_DEFAULT_CONTENT);
      return FirefoxDriver.this;
    }


    public WebElement activeElement() {
      String elementId = sendMessage(NoSuchElementException.class, GET_ACTIVE_ELEMENT);
      return new FirefoxWebElement(FirefoxDriver.this, elementId);
    }

    public Alert alert() {
      throw new UnsupportedOperationException("alert");
    }
  }

  private class FirefoxNavigation implements Navigation {
    public void back() {
      sendMessage(WebDriverException.class, GO_BACK);
    }

    public void forward() {
      sendMessage(WebDriverException.class, GO_FORWARD);
    }

    public void to(String url) {
      get(url);
    }

    public void to(URL url) {
      get(String.valueOf(url));
    }

    public void refresh() {
      sendMessage(WebDriverException.class, REFRESH);
    }
  }
    
  public <X> X getScreenshotAs(OutputType<X> target) {
    // Get the screenshot as base64.
    String base64 = sendMessage(WebDriverException.class, SCREENSHOT);
    // ... and convert it.
    return target.convertFromBase64Png(base64);
  }

  /**
   * Saves a screenshot of the current page into the given file.
   *
   * @param pngFile The file to store the screenshot in.
   * @deprecated Use getScreenshotAs(file), which returns a temporary file.
   */
  @Deprecated
  public void saveScreenshot(File pngFile) {
    if (pngFile == null) {
        throw new IllegalArgumentException("Method parameter pngFile must not be null");
    }

    File tmpfile = getScreenshotAs(FILE);

    File dir = pngFile.getParentFile();
    if (dir != null && !dir.exists() && !dir.mkdirs()) {
        throw new WebDriverException("Could not create directory " + dir.getAbsolutePath());
    }

    try {
      FileHandler.copy(tmpfile, pngFile);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }
}
