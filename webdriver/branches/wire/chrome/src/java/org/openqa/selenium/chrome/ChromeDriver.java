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
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Capabilities;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Context;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import static org.openqa.selenium.remote.DriverCommand.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.URL;

public class ChromeDriver extends RemoteWebDriver {

  /**
   * Starts up a new instance of Chrome, with the required extension loaded,
   * and has it connect to a new ChromeCommandExecutor on port 9700
   */
  public ChromeDriver() {
    this(new ChromeProfile(), new ChromeExtension());
  }

  public ChromeDriver(ChromeProfile profile, ChromeExtension extension) {
    // TODO(danielwh): Remove explicit port (blocked on crbug.com 11547)
    this(new ChromeCommandExecutor(9700, new ChromeBinary(profile, extension)));
  }

  private ChromeDriver(ChromeCommandExecutor executor) {
    super(executor, /*redundant, but null safe*/DesiredCapabilities.chrome());
  }

  /**
   * By default will try to load Chrome from system property
   * webdriver.chrome.bin and the extension from
   * webdriver.chrome.extensiondir.  If the former fails, will try to guess the
   * path to Chrome.  If the latter fails, will try to unzip from the JAR we
   * hope we're in.  If these fail, throws exceptions.
   */
  @Override
  protected void startClient() {
    ((ChromeCommandExecutor) getExecutor()).start();
  }

  /**
   * Kills the started Chrome process and ChromeCommandExecutor if they exist
   */
  @Override
  protected void stopClient() {
    ((ChromeCommandExecutor) getExecutor()).stop();
  }

  @Override
  protected void startSession(Capabilities desiredCapabilities) {
    // ChromeDriver is currently sessionless, so just make this a no-op.
  }

  @Override
  protected ChromeResponse execute(DriverCommand driverCommand) {
    return execute(driverCommand, ImmutableMap.<String, Object>of());
  }

  /**
   * Executes a passed command using the current ChromeCommandExecutor
   * @param driverCommand command to execute
   * @param parameters parameters of command being executed
   * @return response to the command (a Response wrapping a null value if none)
   */
  @Override
  protected ChromeResponse execute(DriverCommand driverCommand, Map<String, ?> parameters) {
    Command command = new Command(new SessionId("[No sessionId]"),
                                  new Context("[No context]"),
                                  driverCommand,
                                  parameters);
    try {
      return ((ChromeCommandExecutor) getExecutor()).execute(command);
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

  @Override
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

  @Override
  protected WebElement getElementFrom(Response response) {
    Object result = response.getValue();
    List<?> elements = (List<?>)result;
    return new ChromeWebElement(this, (String)elements.get(0));
  }

  @Override
  protected List<WebElement> getElementsFrom(Response response) {
    Object result = response.getValue();
    List<WebElement> elements = new ArrayList<WebElement>();
    for (Object element : (List<?>)result) {
      elements.add(new ChromeWebElement(this, (String)element));
    }
    return elements;
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
