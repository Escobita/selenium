package org.openqa.selenium.chrome;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.XPathLookupException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import static org.openqa.selenium.remote.DriverCommand.*;

import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChromeCommandExecutor {
  private final ServerSocket serverSocket;
  //Whether the listening thread should listen
  private volatile boolean listen = false;
  //Whether a client is currently connected
  private boolean hadClient = false;
  //Whether a client has ever been connected
  private boolean hasClient = false;
  ListeningThread listeningThread;
  private Map<DriverCommand, JsonCommand> nameToJson;
  
  /**
   * Creates a new ChromeCommandExecutor which listens on a TCP port.
   * Doesn't return until the TCP port is connected to.
   * @param port port on which to listen for the initial connection,
   * and dispatch commands
   * @throws WebDriverException if could not bind to port
   * TODO(danielwh): Bind to a random port (blocked on crbug.com 11547)
   */
  public ChromeCommandExecutor(int port) {
    nameToJson = ImmutableMap.<DriverCommand, JsonCommand>builder()
        .put(CLOSE, new JsonCommand("{request: 'close'}"))
        .put(QUIT, new JsonCommand("QUIT"))
        .put(GET, new JsonCommand("{request: 'url', url: ?url}"))
        .put(GO_BACK, new JsonCommand("{request: 'goBack'}"))
        .put(GO_FORWARD, new JsonCommand("{request: 'goForward'}"))
        .put(REFRESH, new JsonCommand("{request: 'refresh'}"))
        .put(ADD_COOKIE, new JsonCommand("{request: 'addCookie', cookie: ?cookie}"))
        .put(GET_ALL_COOKIES, new JsonCommand("{request: 'getCookies'}"))
        .put(GET_COOKIE, new JsonCommand("{request: 'getCookieNamed', name: ?name}"))
        .put(DELETE_ALL_COOKIES, new JsonCommand("{request: 'deleteAllCookies'}"))
        .put(DELETE_COOKIE, new JsonCommand("{request: 'deleteCookie', name: ?name}"))
        .put(FIND_ELEMENT,new JsonCommand("{request: 'getElement', by: [?using, ?value]}"))
        .put(FIND_ELEMENTS, new JsonCommand("{request: 'getElements', by: [?using, ?value]}"))
        .put(FIND_CHILD_ELEMENT, new JsonCommand(
            "{request: 'getElement', by: [{id: ?element, using: ?using, value: ?value}]}"))
        .put(FIND_CHILD_ELEMENTS, new JsonCommand(
            "{request: 'getElements', by: [{id: ?element, using: ?using, value: ?value}]}"))
        .put(CLEAR_ELEMENT, new JsonCommand("{request: 'clearElement', elementId: ?elementId}"))
        .put(CLICK_ELEMENT, new JsonCommand("{request: 'clickElement', elementId: ?elementId}"))
        .put(HOVER_OVER_ELEMENT, new JsonCommand(
            "{request: 'hoverElement', elementId: ?elementId}"))
        .put(SEND_KEYS_TO_ELEMENT, new JsonCommand(
            "{request: 'sendElementKeys', elementId: ?elementId, keys: ?keys}"))
        .put(SUBMIT_ELEMENT, new JsonCommand("{request: 'submitElement', elementId: ?elementId}"))
        .put(TOGGLE_ELEMENT, new JsonCommand("{request: 'toggleElement', elementId: ?elementId}"))
        .put(GET_ELEMENT_ATTRIBUTE, new JsonCommand(
            "{request: 'getElementAttribute', elementId: ?elementId, attribute: ?attribute}"))
        .put(GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW, new JsonCommand(
            "{request: 'getElementLocationOnceScrolledIntoView', elementId: ?elementId}"))
        .put(GET_ELEMENT_LOCATION, new JsonCommand(
            "{request: 'getElementLocation', elementId: ?elementId}"))
        .put(GET_ELEMENT_SIZE, new JsonCommand(
            "{request: 'getElementSize', elementId: ?elementId}"))
        .put(GET_ELEMENT_TAG_NAME, new JsonCommand(
            "{request: 'getElementTagName', elementId: ?elementId}"))
        .put(GET_ELEMENT_TEXT, new JsonCommand(
            "{request: 'getElementText', elementId: ?elementId}"))
        .put(GET_ELEMENT_VALUE, new JsonCommand(
            "{request: 'getElementValue', elementId: ?elementId}"))
        .put(GET_ELEMENT_VALUE_OF_CSS_PROPERTY, new JsonCommand(
            "{request: 'getElementValueOfCssProperty', elementId: ?elementId, css: ?property}"))
        .put(IS_ELEMENT_DISPLAYED, new JsonCommand(
            "{request: 'isElementDisplayed', elementId: ?elementId}"))
        .put(IS_ELEMENT_ENABLED, new JsonCommand(
            "{request: 'isElementEnabled', elementId: ?elementId}"))
        .put(IS_ELEMENT_SELECTED, new JsonCommand(
            "{request: 'isElementSelected', elementId: ?elementId}"))
        .put(SET_ELEMENT_SELECTED, new JsonCommand(
            "{request: 'setElementSelected', elementId: ?elementId}"))
        .put(GET_ACTIVE_ELEMENT, new JsonCommand("{request: 'switchToActiveElement'}"))
        .put(SWITCH_TO_FRAME_BY_INDEX, new JsonCommand(
            "{request: 'switchToFrame', using: {index: ?index}}"))
        .put(SWITCH_TO_FRAME_BY_NAME, new JsonCommand(
            "{request: 'switchToFrame', using: {name: ?name}}"))
        .put(SWITCH_TO_DEFAULT_CONTENT, new JsonCommand("{request: 'switchToDefaultContent'}"))
        .put(GET_CURRENT_WINDOW_HANDLE, new JsonCommand("{request: 'getWindowHandle'}"))
        .put(GET_WINDOW_HANDLES, new JsonCommand("{request: 'getWindowHandles'}"))
        .put(SWITCH_TO_WINDOW, new JsonCommand("{request: 'switchToWindow', windowName: ?name}"))
        .put(EXECUTE_SCRIPT, new JsonCommand("EXECUTE")) //Dealt with specially
        .put(GET_CURRENT_URL, new JsonCommand("{request: 'getCurrentUrl'}"))
        .put(GET_PAGE_SOURCE, new JsonCommand("{request: 'getPageSource'}"))
        .put(GET_TITLE, new JsonCommand("{request: 'getTitle'}"))
        .build();
    
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
    listen = true;
    listeningThread = new ListeningThread(serverSocket);
    listeningThread.start();
  }
  
  /**
   * Returns whether an instance of Chrome is currently connected
   * @return whether an instance of Chrome is currently connected
   */
  boolean hasClient() {
    return hasClient;
  }
  
  /**
   * Executes the passed command
   * @param command command to execute
   * @return response to command
   * @throws IllegalStateException if no socket was present
   */
  public ChromeResponse execute(Command command) throws IOException {
    sendCommand(command);
    return handleResponse(command);
  }
  
  /**
   * Sends the passed command to the Chrome extension on the
   * longest-time accepted socket.  Removes the socket from the queue when done
   * @param command command to send
   * @throws IOException if couldn't write command to socket
   */
  private void sendCommand(Command command) throws IOException {
    Socket socket;
    //Peek, rather than poll, so that if it all goes horribly wrong,
    //we can just close all sockets in the queue,
    //not having to worry about the current ones
    while ((socket = listeningThread.sockets.peek()) == null) {
      if (hadClient && !hasClient) {
        throw new IllegalStateException("Cannot execute command without a client");
      }
      Thread.yield();
    }
    try {
      //Respond to request with the command
      JsonCommand commandToPopulate = 
        nameToJson.get(command.getName());
      if (commandToPopulate == null) {
        throw new UnsupportedOperationException("Didn't know how to execute: " +
            command.getName());
      }
      String commandStringToSend = commandToPopulate.populate(command.getParameters());
      socket.getOutputStream().write(fillTwoHundredWithJson(commandStringToSend));
      socket.getOutputStream().flush();
    } finally {
      socket.close();
      listeningThread.sockets.remove(socket);
    }
  }
  
  /**
   * Wraps the passed message up in an HTTP 200 response, with the Content-type
   * header set to application/json
   * @param message message to wrap up as the response
   * @return The passed message, wrapped up in an HTTP 200 response,
   * encoded in UTF-8
   */
  private byte[] fillTwoHundredWithJson(String message) {
    return fillTwoHundred(message, "application/json; charset=UTF-8");
  }

  /**
   * Fills in an HTTP 200 response with the passed message and content type.
   * @param message Response
   * @param contentType HTTP Content-type header
   * @return The HTTP 200 message encoded in UTF-8 as an array of bytes
   */
  private byte[] fillTwoHundred(String message, String contentType) {
    String httpMessage = "HTTP/1.1 200 OK" +
    "\r\nContent-Length: " + message.length() + 
    "\r\nContent-Type: " + contentType + 
    "\r\n\r\n" + message;
    try {
      return httpMessage.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      //Should never happen - Java ships with UTF-8
      throw new WebDriverException("Your environment doesn't support UTF-8");
    }
  }

  /**
   * Listens for the response to a command on the oldest socket in the queue
   * and parses it.
   * Expects the response to be an HTTP request, which ends in the line:
   * EOResponse
   * Responds by sending a 200 response containing QUIT
   * @param command command we are expecting a response to
   * @return response to the command.
   * @throws IOException if there are errors with the socket being used
   */
  private ChromeResponse handleResponse(Command command) throws IOException {
    Socket socket;
    //Peek, rather than poll, so that if it all goes horribly wrong,
    //we can just close all sockets in the queue,
    //not having to worry about the current ones
    while ((socket = listeningThread.sockets.peek()) == null) {
      Thread.yield();
    }
    StringBuilder resultBuilder = new StringBuilder();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    String line;
    boolean hasSeenDoubleCRLF = false; //Whether we are out of headers yet
    while ((line = reader.readLine()) != null && !line.equals("EOResponse")) {
      if (hasSeenDoubleCRLF) {
        if (resultBuilder.length() > 0) {
          //Out of headers, and not the first line, so append a newline
          resultBuilder.append("\n");
        }
        resultBuilder.append(line);
      }
      if (line.equals("")) {
        hasSeenDoubleCRLF = true;
      }
    }
    return parseResponse(resultBuilder.toString());
  }

  /**
   * Parses a raw json string into a response.
   * @param rawJsonString JSON string encapsulating the response.
   * @return the parsed response.
   */
  private ChromeResponse parseResponse(String rawJsonString) {
    if (rawJsonString.length() == 0) {
      return new ChromeResponse(0, null);
    }
    try {
      JSONObject jsonObject = new JSONObject(rawJsonString);
      if (!jsonObject.has("statusCode")) {
        throw new WebDriverException("Response had no status code.  Response was: " + rawJsonString);
      }
      if (jsonObject.getInt("statusCode") == 0) {
        //Success! Parse value
        if (!jsonObject.has("value") || jsonObject.isNull("value")) {
          return new ChromeResponse(0, null);
        }
        Object value = jsonObject.get("value");
        Object parsedValue = parseJsonToObject(value);
        if (parsedValue instanceof ChromeWebElement) {
          return new ChromeResponse(-1, ((ChromeWebElement)parsedValue).getElementId());
        } else {
          return new ChromeResponse(0, parsedValue);
        }
      } else {
        String message = "";
        if (jsonObject.has("value") &&
            jsonObject.get("value") instanceof JSONObject &&
            jsonObject.getJSONObject("value").has("message") &&
            jsonObject.getJSONObject("value").get("message") instanceof String) {
          message = jsonObject.getJSONObject("value").getString("message");
        }
        switch (jsonObject.getInt("statusCode")) {
        //Error codes are loosely based on native exception codes,
        //see common/src/cpp/webdriver-interactions/errorcodes.h
        case 2:
          //Cookie error
          throw new WebDriverException(message);
        case 3:
          throw new NoSuchWindowException(message);
        case 7:
          throw new NoSuchElementException(message);
        case 8:
          throw new NoSuchFrameException(message);
        case 9:
          //Unknown command
          throw new UnsupportedOperationException(message); 
        case 10:
          throw new StaleElementReferenceException(message);
        case 11:
          throw new ElementNotVisibleException(message);
        case 12: 
          //Invalid element state (e.g. disabled)
          throw new UnsupportedOperationException(message);
        case 17:
          //Bad javascript
          throw new WebDriverException(message);
        case 19:
          //Bad xpath
          throw new XPathLookupException(message);
        case 99:
          throw new WebDriverException("An error occured when sending a native event");
        case 500:
          if (message.equals("")) {
            message = "An error occured due to the internals of Chrome. " +
            "This does not mean your test failed. " +
            "Try running your test again in isolation.";
          }
          throw new FatalChromeException(message);
        }
        throw new WebDriverException("An error occured in the page");
      }
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }
  
  private Object parseJsonToObject(Object value) throws JSONException {
    if (value instanceof String) {
      return value;
    } else if (value instanceof Boolean) {
      return value;
    } else if (value instanceof Number) {
      //We return all numbers as longs
      return ((Number)value).longValue();
    } else if (value instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray)(value);
      List<Object> arr = new ArrayList<Object>(jsonArray.length());
      for (int i = 0; i < jsonArray.length(); i++) {
        arr.add(parseJsonToObject(jsonArray.get(i)));
      }
      return arr;
    } else if (value instanceof JSONObject) {
      //Should only happen when we return from a javascript execution.
      //Assumes the object is of the form {type: some_type, value: some_value}
      JSONObject object = (JSONObject)value;
      if (!object.has("type")) {
        throw new WebDriverException("Returned a JSONObjet which had no type");
      }
      if ("NULL".equals(object.getString("type"))) {
        return null;
      } else if ("VALUE".equals(object.getString("type"))) {
        Object innerValue = object.get("value");
        if (innerValue instanceof Integer) {
          innerValue = new Long((Integer)innerValue);
        }
        return innerValue;
      } else if ("ELEMENT".equals(object.getString("type"))) {
        return new ChromeWebElement(null, (String)object.get("value"));
      } else if ("POINT".equals(object.getString("type"))) {
        if (!object.has("x") || !object.has("y") ||
            !(object.get("x") instanceof Number) ||
            !(object.get("y") instanceof Number)) {
          throw new WebDriverException("Couldn't construct Point without " +
              "x and y coordinates");
        }
        return new Point(object.getInt("x"),
                         object.getInt("y"));
      } else if ("DIMENSION".equals(object.getString("type"))) {
        if (!object.has("width") || !object.has("height") ||
            !(object.get("width") instanceof Number) ||
            !(object.get("height") instanceof Number)) {
          throw new WebDriverException("Couldn't construct Dimension " +
              "without width and height");
        }
        return new Dimension(object.getInt("width"),
                             object.getInt("height"));
      } else if ("COOKIE".equals(object.getString("type"))) {
        if (!object.has("name") || !object.has("value")) {
          throw new WebDriverException("Couldn't construct Cookie " +
              "without name and value");
        }
        return new Cookie(object.getString("name"), object.getString("value"));
      }
    } else {
      throw new WebDriverException("Didn't know how to deal with " +
          "response value of type: " + value.getClass());
    }
    return null;
  }

  /**
   * Stops listening from for new sockets from Chrome
   */
  public void stopListening() {
    listen = false;
    listeningThread.stopListening();
    while (!serverSocket.isClosed()) {// || serverSocket.isBound()) {
      Thread.yield();
    }
    //TODO(danielwh): Remove this when using multiple ports (blocked on crbug.com 11547)
    try { Thread.sleep(500); } catch (InterruptedException e) {}
  }

  /**
   * Thread which, when spawned, accepts all sockets on its ServerSocket and
   * queues them up
   */
  private class ListeningThread extends Thread {
    private boolean isListening = false;
    private Queue<Socket> sockets = new ConcurrentLinkedQueue<Socket>();
    private ServerSocket serverSocket;
    
    public ListeningThread(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
    }
    
    public void run() {
      if (!isListening) {
        listen();
      }
    }
    public void listen() {
      isListening = true;
      try {
        while (listen) {
          sockets.add(serverSocket.accept());
          hasClient = true;
          hadClient = true;
        }
      } catch (SocketException e) {
        if (listen) {
          throw new WebDriverException(e);
        } else {
          //We are shutting down sockets manually
        }
      } catch (IOException e) {
        isListening = false;
        throw new WebDriverException(e);
      }
    }
    
    public void stopListening() {
      try {
        closeCurrentSockets();
      } catch (Exception e) {
        throw new WebDriverException(e);
      } finally {
        try {
          if (!serverSocket.isClosed()) {
            serverSocket.close();
          }
        } catch (Exception e) {
          throw new WebDriverException(e);
        }
      }
    }
    private void closeCurrentSockets() {
      for (Socket socket : listeningThread.sockets) {
        try {
          socket.close();
          listeningThread.sockets.remove(socket);
        } catch (IOException e) {
          //Nothing we can sanely do here
        }
      }
    }
  }
  
  static class JsonCommand {
    private final String json;
    
    /**
     * @param json String of form {var:?val, var2: "foo"}
     * where things starting with ? are placeholders for parameters
     */
    JsonCommand(String json) {
      if (json == null) {
        throw new NullPointerException("JSON cannot be null in JsonCommand");
      }
      this.json = json;
    }
    
    /**
     * Populates the JsonCommand with the passed parameters.
     * Replaces things starting with ? in the json string with
     * the passed parameters in order.
     * i.e. looks for ?anything and replaces the ?anything with the variable, in order
     * This is ugly.
     * @param parameters parameters to put in place of ?s
     * @return json string filled with parameters
     * @throws IllegalArgumentException if arguments.length != number of variables
     */
    public String populate(Object... parameters) {
      if (json.equals("EXECUTE")) {
        //Special case execution, because it needs arguments wrapped up as
        //type/value objects, rather than just in JSON.
        
        //Fill in the script we are executing, ignoring args to the script entirely
        JsonCommand jsonCommand = new JsonCommand("{request: 'execute', script: ?script}");
        String populated = jsonCommand.populate(parameters[0]);
        JSONObject jsonObject;
        try {
          jsonObject = new JSONObject(populated);
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
        
        //Fill in the args, if we were passed any
        JSONArray args = new JSONArray();
        if (parameters.length > 1 && parameters[1].getClass().isArray()) {
          Object[] argumentsFromParameters = (Object[])parameters[1];
          for (int i = 0; i < argumentsFromParameters.length; ++i) {
            args.put(wrapArgumentForScriptExecution(argumentsFromParameters[i]));
          }
        }
        try {
          jsonObject.put("args", args);
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
        return jsonObject.toString();
      }
      
      //If we are not filling in a script, we just fill in the args as needed
      List<String> parts = Arrays.asList(json.split(","));
      int i = 0;
      //The output won't be exactly of length json.length, but it's a convenient indication
      StringBuilder builder = new StringBuilder(json.length());
      Iterator<String> it = parts.iterator();
      while (it.hasNext()) {
        String part = it.next();
        String[] nameAndValue = part.split("\\?");
        builder.append(nameAndValue[0]);
        if (nameAndValue.length == 2) {
          if (i >= parameters.length) {
            throw new IllegalArgumentException(
                "More variables than parameters passed (" + parameters.length +
                ") when populating command");
          }
          String value = nameAndValue[1];
          while (value.indexOf(']') > -1 || value.indexOf('}') > -1) {
            if (value.indexOf(']') > -1) {
              value = value.substring(0, value.indexOf(']'));
            }
            if (value.indexOf('}') > -1) {
              value = value.substring(0, value.indexOf('}'));
            }
          }
          String parsedParameter;
          if (parameters[i] instanceof ChromeWebElement) {
            parsedParameter = ((ChromeWebElement)parameters[i]).getElementId().replace("element/", "");
          } else if (parameters[i] instanceof Cookie) {
            //This is not nice at all...
            Cookie cookie = (Cookie)parameters[i];
            Map<String, Object> cookieMap = new HashMap<String, Object>();
            cookieMap.put("name", cookie.getName());
            cookieMap.put("value", cookie.getValue());
            cookieMap.put("domain", cookie.getDomain());
            cookieMap.put("path", cookie.getPath());
            cookieMap.put("secure", cookie.isSecure());
            cookieMap.put("expiry", cookie.getExpiry());
            parsedParameter = new JSONObject(cookieMap).toString();
          } else if (parameters[i].getClass().isArray()) {
            try {
              parsedParameter = new JSONArray(parameters[i]).toString();
            } catch (JSONException e) {
              throw new RuntimeException(e);
            }
          } else {
            parsedParameter = parameters[i].toString();
          }
          parsedParameter = sanitize(parsedParameter);
          if (parameters[i] instanceof String) {
            parsedParameter = "\'" + parsedParameter + "\'";
          }
          
          i++;
          String restOfValue = nameAndValue[1].substring(value.length());
          builder.append(parsedParameter).append(restOfValue);
        }
        if (it.hasNext()) {
          builder.append(",");
        }
      }
      if (i != parameters.length) {
        throw new IllegalArgumentException("More parameters (" +
            parameters.length + ") than variables ( " + i + ") when populating command");
      }
      return builder.toString();
    }

    /**
     * Wraps up values as {type: some_type, value: some_value} objects
     * @param argument value to wrap up
     * @return wrapped up value
     * TODO(danielwh): See if JSONObject and JSONArray have a useful common superclass
     */
    static Object wrapArgumentForScriptExecution(Object argument) {
      JSONObject wrappedArgument = new JSONObject();
      try {
        if (argument instanceof String) {
          wrappedArgument.put("type", "STRING");
          wrappedArgument.put("value", argument);
        } else if (argument instanceof Boolean) {
          wrappedArgument.put("type", "BOOLEAN");
          wrappedArgument.put("value", argument);
        } else if (argument instanceof Number) {
          wrappedArgument.put("type", "NUMBER");
          wrappedArgument.put("value", argument);
        } else if (argument instanceof ChromeWebElement) {
          wrappedArgument.put("type", "ELEMENT");
          wrappedArgument.put("value", ((ChromeWebElement)argument).getElementId());
        } else if (argument instanceof Collection<?>) {
          JSONArray array = new JSONArray();
          for (Object o : (Collection<?>)argument) {
            array.put(wrapArgumentForScriptExecution(o));
          }
          return array;
        } else {
          throw new IllegalArgumentException("Could not wrap up " +
                "javascript parameter " + argument +
                "(class: " + argument.getClass() + ")");
        }
      } catch (JSONException e) {
        throw new WebDriverException(e);
      }
      return wrappedArgument;
    }
    
    /**
     * Escapes \, ' and \n for JSON as \\, \' and \\n respectively
     * @param string string to escape
     * @return escaped string
     */
    static String sanitize(String string) {
      return string.replace("\\", "\\\\").replace("\'", "\\\'").replace("\n", "\\n");
    }
  }
}
