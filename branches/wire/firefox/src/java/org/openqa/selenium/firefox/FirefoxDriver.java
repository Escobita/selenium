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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.IllegalLocatorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Speed;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.internal.ExtensionConnectionFactory;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.internal.FileHandler;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.ReturnedCookie;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import static org.openqa.selenium.OutputType.FILE;


/**
 * An implementation of the {#link WebDriver} interface that drives Firefox. This works through a firefox extension,
 * which gets installed automatically if necessary. Important system variables are:
 * <ul>
 *  <li><b>webdriver.firefox.bin</b> - Which firefox binary to use (normally "firefox" on the PATH).</li>
 *  <li><b>webdriver.firefox.profile</b> - The name of the profile to use (normally "WebDriver").</li>
 * </ul>
 *
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
    // Accept untrusted SSL certificates.
    public static final boolean ACCEPT_UNTRUSTED_CERTIFICATES = true;
    // Assume that the untrusted certificates will come from untrusted issuers
    // or will be self signed.
    public static final boolean ASSUME_UNTRUSTED_ISSUER = true;

    // Commands we can execute with needing to dismiss an active alert
    private final Set<DriverCommand> alertWhiteListedCommands = new HashSet<DriverCommand>() {{
      add(DriverCommand.DISMISS_ALERT);
    }};

    private final ErrorHandler errorHandler = new ErrorHandler();
    private final ExtensionConnection extension;
    protected SessionId sessionId;
    private FirefoxAlert currentAlert;

  public FirefoxDriver() {
      this(new FirefoxBinary(), null);
    }

  public FirefoxDriver(FirefoxProfile profile) {
      this(new FirefoxBinary(), profile);
    }

  public FirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
    FirefoxProfile profileToUse = profile;
    String suggestedProfile = System.getProperty("webdriver.firefox.profile");
    if (profileToUse == null && suggestedProfile != null) {
      profileToUse = new ProfilesIni().getProfile(suggestedProfile);
    } else if (profileToUse == null) {
      profileToUse = new FirefoxProfile();
      profileToUse.addWebDriverExtensionIfNeeded(false);
    } else {
      profileToUse.addWebDriverExtensionIfNeeded(false);
    }
    prepareEnvironment();

    extension = connectTo(binary, profileToUse, "localhost");
    fixId();
  }

    private FirefoxDriver(ExtensionConnection extension, SessionId sessionId) {
      this.extension = extension;
      this.sessionId = sessionId;
    }

    protected ExtensionConnection connectTo(FirefoxBinary binary, FirefoxProfile profile, String host) {
        return ExtensionConnectionFactory.connectTo(binary, profile, host);
    }

    protected void prepareEnvironment() {
      // Does nothing, but provides a hook for subclasses to do "stuff"
    }

    public void close() {
        try {
            sendMessage(DriverCommand.CLOSE);
        } catch (Exception e) {
            // All good
        }
    }

    public String getPageSource() {
        return sendMessage(DriverCommand.GET_PAGE_SOURCE);
    }

    public void get(String url) {
        sendMessage(DriverCommand.GET, ImmutableMap.of("url", url));
    }

    public String getCurrentUrl() {
        return sendMessage(DriverCommand.GET_CURRENT_URL);
    }

    public String getTitle() {
        return sendMessage(DriverCommand.GET_TITLE);
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
    if (using == null)
     throw new IllegalArgumentException("Cannot find elements when the class name expression is null.");

    if (using.matches(".*\\s+.*")) {
      throw new IllegalLocatorException(
          "Compound class names are not supported. Consider searching for one class name and filtering the results.");
    }
        
    return findElements("class name", using);
  }

  public WebElement findElementByClassName(String using) {
    if (using == null)
     throw new IllegalArgumentException("Cannot find elements when the class name expression is null.");

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
    String elementId = sendMessage(DriverCommand.FIND_ELEMENT,
        ImmutableMap.of("using", method, "value", selector));

    return new FirefoxWebElement(this, elementId);
  }

  private List<WebElement> findElements(String method, String selector) {
    @SuppressWarnings("unchecked")
    List<String> returnedIds = (List<String>) executeCommand(
        DriverCommand.FIND_ELEMENTS, ImmutableMap.of("using", method, "value", selector));

    List<WebElement> elements = Lists.newArrayListWithExpectedSize(returnedIds.size());

    for (String id : returnedIds) {
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
        String response = sendMessage(DriverCommand.NEW_SESSION);
        return new FirefoxDriver(extension, new SessionId(response));
    }

    private String sendMessage(DriverCommand methodName) {
      return sendMessage(methodName, ImmutableMap.<String, Object>of());
    }

    private String sendMessage(DriverCommand methodName, Map<String, ?> parameters) {
        return sendMessage(new Command(sessionId, methodName, parameters));
    }

    protected String sendMessage(Command command) {
      return String.valueOf(executeCommand(command));
    }

    protected Object executeCommand(DriverCommand methodName) {
      return executeCommand(methodName, ImmutableMap.<String, Object>of());
    }

    protected Object executeCommand(DriverCommand methodName, Map<String, ?> parameters) {
      return executeCommand(new Command(sessionId, methodName, parameters));
    }

    protected Object executeCommand(Command command) {
      if (currentAlert != null) {
        if (!alertWhiteListedCommands.contains(command.getName())) {
          ((FirefoxTargetLocator) switchTo()).alert().dismiss();
          throw new UnhandledAlertException(command.getName().toString());
        }
      }

      Response response = extension.execute(command);
      errorHandler.throwIfResponseFailed(response);

      Object rawResponse = response.getValue();
      if (rawResponse instanceof Map) {
        Map map = (Map) rawResponse;
        if (map.containsKey("__webdriverType")) {
          // Looks like have an alert. construct it
          currentAlert = new FirefoxAlert((String) map.get("text"));
          return null;
        }
      }
      return rawResponse;
    }

    private void fixId() {
        this.sessionId = new SessionId(
            String.valueOf(executeCommand(DriverCommand.NEW_SESSION)));
    }

    public void quit() {
        extension.quit();
    }

  public String getWindowHandle() {
    return sendMessage(DriverCommand.GET_CURRENT_WINDOW_HANDLE);
  }

  public Set<String> getWindowHandles() {
    @SuppressWarnings("unchecked")
    List<String> allHandles = (List<String>) executeCommand(DriverCommand.GET_WINDOW_HANDLES);
    Set<String> toReturn = Sets.newHashSetWithExpectedSize(allHandles.size());
    for (String handle : allHandles) {
      if (handle != null) {
        toReturn.add(handle);
      }
    }
    return toReturn;
  }

  public Object executeScript(String script, Object... args) {
        // Escape the quote marks
        script = script.replaceAll("\"", "\\\"");

        Iterable<Object> convertedArgs = Iterables.transform(
            Lists.newArrayList(args),
            new Function<Object, Object>() {
              public Object apply(Object o) {
                return convertToJsObject(o);
              }
            });

        Map<String, ?> params = ImmutableMap.of(
            "script", script,
            "args", Lists.newArrayList(convertedArgs));

        Map rawResponse = (Map) executeCommand(DriverCommand.EXECUTE_SCRIPT, params);
        return parseJavascriptObjectFromResponse(
            String.valueOf(rawResponse.get("type")),
            rawResponse.get("value"));
  }
  
  public Object parseJavascriptObjectFromResponse(String resultType, Object response) {
    if ("NULL".equals(resultType) || response == null) {
      return null;
    }

    if (response instanceof Iterable) {
      List<?> responseAsList = Lists.newArrayList((Iterable<?>) response);
      return Lists.newArrayList(Iterables.transform(
          responseAsList, new Function<Object, Object>() {
            public Object apply(Object value) {
              Map subObject = (Map) value;
              return parseJavascriptObjectFromResponse(
                  String.valueOf(subObject.get("type")),
                  subObject.get("value"));
            }
          }));
    }

    if ("ELEMENT".equals(resultType)) {
      return new FirefoxWebElement(this, (String)response);
    }

    if (response instanceof Double || response instanceof Float) {
      return ((Number) response).doubleValue();
    }

    if (response instanceof Number) {
      return ((Number) response).longValue();
    }

    return response;
  }

  public boolean isJavascriptEnabled() {
    return true;
  }

  private Object[] convertToJsObjects(Object[] args) {
    if (args.length == 0)
      return null;

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
    } else if (arg instanceof Number) {
      converted.put("type", "NUMBER");
      if (arg instanceof Float || arg instanceof Double) {
        converted.put("value", ((Number) arg).doubleValue());
      } else {
        converted.put("value", ((Number) arg).longValue());
      }
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
      Collection<?> args = ((Collection<?>)arg);
      return Lists.newArrayList(Iterables.transform(args,
          new Function<Object, Object>() {
            public Object apply(Object o) {
              return convertToJsObject(o);
            }
          }));
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
        private final List<String> fieldNames = Arrays.asList("domain", "expiry", "name", "path", "value", "secure");
        private final DateFormat RFC_1123_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'Z", Locale.US);
        //TODO: need to refine the values here
        private final int SLOW_SPEED = 1;
        private final int MEDIUM_SPEED = 10;
        private final int FAST_SPEED = 100;

        public void addCookie(Cookie cookie) {
            sendMessage(DriverCommand.ADD_COOKIE, ImmutableMap.of("cookie", cookie));
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
            @SuppressWarnings("unchecked")
            List<String> response = (List<String>) executeCommand(DriverCommand.GET_COOKIE);
            Set<Cookie> cookies = new HashSet<Cookie>();

              for (String cookieString : response) {
                cookieString = cookieString.trim();
                if ("".equals(cookieString)) {
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
                    if(attribute.contains("=")) {
                        String[] tokens = attribute.trim().split("=", 2);
                        if(attributesMap.get("name").equals("")) {
                            attributesMap.put("name", tokens[0]);
                            attributesMap.put("value", tokens[1]);
                        } else if("domain".equals(tokens[0])
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
                        expires, Boolean.parseBoolean(attributesMap.get("secure")), getCurrentUrl()));
              }

          return cookies;
        }

        public void deleteCookieNamed(String name) {
            sendMessage(DriverCommand.DELETE_COOKIE, ImmutableMap.of("name", name));
        }

        public void deleteCookie(Cookie cookie) {
            deleteCookieNamed(cookie.getName());
        }

        public void deleteAllCookies() {
            sendMessage(DriverCommand.DELETE_ALL_COOKIES);
        }

        public Speed getSpeed() {
            Number pixelSpeed = (Number) executeCommand(DriverCommand.GET_SPEED);
            Speed speed;

            // TODO: simon 2007-02-01; Delegate to the enum
            switch (pixelSpeed.intValue()) {
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
            switch(speed) {
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
            sendMessage(DriverCommand.SET_SPEED, ImmutableMap.of("speed", pixelSpeed));
        }
    }

    private class FirefoxTargetLocator implements TargetLocator {
        public WebDriver frame(int frameIndex) {
            sendMessage(DriverCommand.SWITCH_TO_FRAME, ImmutableMap.of("id", frameIndex));
            return FirefoxDriver.this;
        }

        public WebDriver frame(String frameName) {
            Map<String, Object> frameId = Maps.newHashMap();
            frameId.put("id", frameName);
            sendMessage(DriverCommand.SWITCH_TO_FRAME, frameId);
            return FirefoxDriver.this;
        }

        public WebDriver window(String windowName) {
          String response = sendMessage(DriverCommand.SWITCH_TO_WINDOW,
              ImmutableMap.of("name", windowName));
            if (response == null || "No window found".equals(response)) {
                throw new NoSuchWindowException("Cannot find window: " + windowName);
            }
            return FirefoxDriver.this;
        }

      public WebDriver defaultContent() {
            return frame(null);
        }


        public WebElement activeElement() {
          String elementId = sendMessage(DriverCommand.GET_ACTIVE_ELEMENT);
          return new FirefoxWebElement(FirefoxDriver.this, elementId);
        }

        public Alert alert() {
          if (currentAlert != null) {
            return currentAlert;
          }

          throw new NoAlertPresentException();
        }
    }

    private class FirefoxNavigation implements Navigation {
      public void back() {
        sendMessage(DriverCommand.GO_BACK);
      }

      public void forward() {
        sendMessage(DriverCommand.GO_FORWARD);
      }

      public void to(String url) {
        get(url);
      }

      public void to(URL url) {
        get(String.valueOf(url));
      }

      public void refresh() {
        sendMessage(DriverCommand.REFRESH);
      }
    }
    
    public <X> X getScreenshotAs(OutputType<X> target) {
      // Get the screenshot as base64.
      String base64 = sendMessage(DriverCommand.SCREENSHOT);
      // ... and convert it.
      return target.convertFromBase64Png(base64);
    }

    /**
     * Saves a screenshot of the current page into the given file.
     *
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

  private class FirefoxAlert implements Alert {
    private String text;

    public FirefoxAlert(String text) {
      this.text = text;
    }

    public void dismiss() {
      sendMessage(DriverCommand.DISMISS_ALERT, ImmutableMap.of("text", text));
      currentAlert = null;
    }

    public void accept() {
    }

    public String getText() {
      return text;
    }
  }
}
