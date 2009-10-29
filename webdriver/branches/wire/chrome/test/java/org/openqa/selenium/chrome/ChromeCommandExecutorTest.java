package org.openqa.selenium.chrome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Context;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.Cookie;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Date;

/**
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class ChromeCommandExecutorTest extends TestCase {

  public void testFillingInCommandWithNoArgs() throws Exception {
    String expectedString = new JSONObject()
        .put("request", DriverCommand.CLOSE.toString())
        .toString();
    String commandString = ChromeCommandExecutor.fillArgs(command(DriverCommand.CLOSE));
    assertEquals(expectedString, commandString);
  }

  public void testFillingInCommandWithSimpleArg() throws Exception {
    String expectedString = new JSONObject()
        .put("request", DriverCommand.GET.toString())
        .put("url", "http://www.google.com")
        .toString();
    String commandString = ChromeCommandExecutor.fillArgs(
        command(DriverCommand.GET, ImmutableMap.of("url", "http://www.google.com")));
    assertEquals(expectedString, commandString);
  }

  public void testFillingInCommandWithACookieArg() throws Exception {
    String expectedString = new JSONObject()
        .put("request", DriverCommand.ADD_COOKIE.toString())
        .put("cookie", new JSONObject()
            .put("name", "foo")
            .put("value", "bar")
            .put("domain", "google.com")
            .put("path", "/")
            .put("secure", false)
            .put("expiry", new Date(0).toString()))
        .toString();
    String commandString = ChromeCommandExecutor.fillArgs(
        command(DriverCommand.ADD_COOKIE,
            ImmutableMap.of("cookie", new Cookie("foo", "bar", "google.com", "/", new Date(0)))));
    assertEquals(expectedString, commandString);
  }

  public void testShouldThrowIfRequiredParamsAreMissing() throws Exception {
    try {
      ChromeCommandExecutor.fillArgs(command(DriverCommand.GET));
      fail("Should have thrown");
    } catch (WebDriverException expected) {
      // Do nothing
    }

    try {
      ChromeCommandExecutor.fillArgs(
          command(DriverCommand.GET, ImmutableMap.of("food", "cheese")));
      fail("Should have thrown");
    } catch (WebDriverException expected) {
      // Do nothing
    }
  }

  public void testFillingInExecuteScriptArgs() throws Exception {
    String expectedString = new JSONObject()
        .put("request", DriverCommand.EXECUTE_SCRIPT.toString())
        .put("script", "return document.body;")
        .put("args", new JSONArray()
            .put(new JSONObject()
                .put("type", "STRING")
                .put("value", "arg1"))
            .put(new JSONObject()
                .put("type", "BOOLEAN")
                .put("value", true))
            .put(new JSONObject()
                .put("type", "NUMBER")
                .put("value", 3.1456)))
        .toString();
    String commandString = ChromeCommandExecutor.fillArgs(
        command(DriverCommand.EXECUTE_SCRIPT, ImmutableMap.of(
            "script", "return document.body;",
            "args", Lists.newArrayList("arg1", true, 3.1456))));
    assertEquals(expectedString, commandString);
  }

  public void testShouldBeAbleToWrapAStringScriptArgument() throws Exception {
    assertEquals(
        new JSONObject().put("type", "STRING").put("value", "hello").toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution("hello").toString());
  }

  public void testShouldBeAbleToWrapAnIntegerScriptArgument() throws Exception {
    assertEquals(
        new JSONObject().put("type", "NUMBER").put("value", 12345).toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(12345).toString());
  }

  public void testShouldBeAbleToWrapALongScriptArgument() throws Exception {
    assertEquals(
        new JSONObject().put("type", "NUMBER").put("value", Long.MAX_VALUE).toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(Long.MAX_VALUE).toString());
  }

  public void testShouldBeAbleToWrapAFloatScriptArgument() throws Exception {
    assertEquals(
        new JSONObject().put("type", "NUMBER").put("value", (Number) Float.MAX_VALUE).toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(Float.MAX_VALUE).toString());
  }

  public void testShouldBeAbleToWrapADoubleScriptArgument() throws Exception {
    assertEquals(
        new JSONObject().put("type", "NUMBER").put("value", (Number) Double.MIN_VALUE).toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(Double.MIN_VALUE).toString());
  }

  public void testShouldBeAbleToWrapACollectionOfScriptArguments() throws Exception {
    assertEquals(
        new JSONArray()
            .put(new JSONObject().put("type", "STRING").put("value", "abc"))
            .put(new JSONObject().put("type", "NUMBER").put("value", 123))
            .put(new JSONObject().put("type", "BOOLEAN").put("value", false))
            .toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(ImmutableList.of("abc", 123, false))
            .toString());
  }

  public void testShouldBeAbleToWrapAChromeWebElement() throws Exception {
    assertEquals(
        new JSONObject().put("type", "ELEMENT").put("value", "abc123").toString(),
        ChromeCommandExecutor.wrapArgumentForScriptExecution(new ChromeWebElement(null, "abc123"))
            .toString());
  }

  public void testShouldRejectUnsupportedScriptArgumentTypes() throws Exception {
    try {
      ChromeCommandExecutor.wrapArgumentForScriptExecution(new Object());
      fail("Should reject invalid input");
    } catch (IllegalArgumentException expected) {
      // Do nothing
    }
  }

  private static Command command(DriverCommand driverCommand) {
    return new Command(new SessionId("nada"), new Context("nope"), driverCommand);
  }

  private static Command command(DriverCommand driverCommand, Map<String, ?> parameters) {
    return new Command(new SessionId("nada"), new Context("nope"), driverCommand, parameters);
  }
}
