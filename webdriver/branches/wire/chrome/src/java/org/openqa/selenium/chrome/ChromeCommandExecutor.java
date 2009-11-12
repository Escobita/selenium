//TODO(danielwh): Actually use JSON for the commands.  In fact, even valid JSON strings would be nice.

package org.openqa.selenium.chrome;

import com.google.common.collect.ImmutableMap;
import com.google.common.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.JsonToBeanConverter;
import static org.openqa.selenium.remote.DriverCommand.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChromeCommandExecutor implements CommandExecutor {
  private static final String[] ELEMENT_ID_ARG = new String[] {"id"};
  private static final String[] NO_ARGS = new String[] {};

  private final int port;
  private final ChromeBinary chromeBinary;

  //Whether the listening thread should listen
  private volatile boolean listen = false;
  ListeningThread listeningThread;

  private static final Map<DriverCommand, String[]> COMMANDS =
      ImmutableMap.<DriverCommand, String[]> builder()
          .put(CLOSE, NO_ARGS)
          .put(QUIT, NO_ARGS)
          .put(GET, new String[] {"url"})
          .put(GO_BACK, NO_ARGS)
          .put(GO_FORWARD, NO_ARGS)
          .put(REFRESH, NO_ARGS)
          .put(ADD_COOKIE, new String[] {"cookie"})
          .put(GET_ALL_COOKIES,  NO_ARGS)
          .put(GET_COOKIE, new String[] {"name"})
          .put(DELETE_ALL_COOKIES, NO_ARGS)
          .put(DELETE_COOKIE, new String[] {"name"})
          .put(FIND_ELEMENT, new String[] {"using", "value"})
          .put(FIND_ELEMENTS, new String[] {"using", "value"})
          .put(FIND_CHILD_ELEMENT, new String[] {"id", "using", "value"})
          .put(FIND_CHILD_ELEMENTS, new String[] {"id", "using", "value"})
          .put(CLEAR_ELEMENT, ELEMENT_ID_ARG)
          .put(CLICK_ELEMENT, ELEMENT_ID_ARG)
          .put(HOVER_OVER_ELEMENT, ELEMENT_ID_ARG)
          .put(SEND_KEYS_TO_ELEMENT, new String[] {"id", "value"})
          .put(SUBMIT_ELEMENT, ELEMENT_ID_ARG)
          .put(TOGGLE_ELEMENT, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_ATTRIBUTE, new String[] {"id", "name"})
          .put(_GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_LOCATION, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_SIZE, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_TAG_NAME, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_TEXT, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_VALUE, ELEMENT_ID_ARG)
          .put(GET_ELEMENT_VALUE_OF_CSS_PROPERTY, new String[] {"id", "propertyName"})
          .put(IS_ELEMENT_DISPLAYED, ELEMENT_ID_ARG)
          .put(IS_ELEMENT_ENABLED, ELEMENT_ID_ARG)
          .put(IS_ELEMENT_SELECTED, ELEMENT_ID_ARG)
          .put(SET_ELEMENT_SELECTED, ELEMENT_ID_ARG)
          .put(GET_ACTIVE_ELEMENT, NO_ARGS)
          .put(SWITCH_TO_FRAME_BY_INDEX, new String[] {"index"})
          .put(SWITCH_TO_FRAME_BY_NAME, new String[] {"name"})
          .put(SWITCH_TO_DEFAULT_CONTENT, NO_ARGS)
          .put(GET_CURRENT_WINDOW_HANDLE, NO_ARGS)
          .put(GET_WINDOW_HANDLES, NO_ARGS)
          .put(SWITCH_TO_WINDOW, new String[] {"windowName"})
          .put(GET_CURRENT_URL, NO_ARGS)
          .put(GET_PAGE_SOURCE, NO_ARGS)
          .put(GET_TITLE, NO_ARGS)
          .put(EXECUTE_SCRIPT, new String[] {"script", "args"})
          .build();

  /**
   * Creates a new ChromeCommandExecutor which will listen on a TCP port.
   * To start the executor, call {@link #startListening()}.
   *
   * @param port port on which to listen for the initial connection,
   *     and dispatch commands
   * @throws WebDriverException if could not bind to port
   * @see #startListening()
   * TODO(danielwh): Bind to a random port (blocked on crbug.com 11547)
   */
  public ChromeCommandExecutor(int port, ChromeBinary binary) {
    this.port = port;
    this.chromeBinary = binary;
  }

  @VisibleForTesting ChromeBinary getChromeBinary() {
    return chromeBinary;
  }
  
  /**
   * Returns whether an instance of Chrome is currently connected
   * @return whether an instance of Chrome is currently connected
   */
  boolean hasClient() {
    return listeningThread != null && listeningThread.hasClient;
  }
  
  /**
   * Executes the passed command
   * @param command command to execute
   * @return response to command
   * @throws IllegalStateException if no socket was present
   * @throws DeadChromeException If Chrome unexpectedly quits, making it
   *     impossible to complete the command.
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
    Socket socket = getOldestSocket();
    try {
      //Respond to request with the command
      String commandStringToSend;
      commandStringToSend = fillArgs(command);
      socket.getOutputStream().write(fillTwoHundredWithJson(commandStringToSend));
      socket.getOutputStream().flush();
    } finally {
      socket.close();
      listeningThread.sockets.remove(socket);
    }
  }
  
  static String fillArgs(Command command) {
    String[] parameterNames = COMMANDS.get(command.getName());
    JSONObject json = new JSONObject();
    if (parameterNames.length != command.getParameters().keySet().size()) {
      throw new WebDriverException(new IllegalArgumentException(
          "Did not supply the expected number of parameters"));
    }
    try {
      json.put("request", command.getName());
      for (String parameterName : parameterNames) {
        //Icky icky special case
        // TODO(jleyba): This is a temporary solution and will be going away _very_
        // soon.
        boolean isArgs = (EXECUTE_SCRIPT.equals(command.getName()) &&
                          "args".equals(parameterName));
        if (!command.getParameters().containsKey(parameterName)) {
          throw new WebDriverException(new IllegalArgumentException(
              "Missing required parameter \"" + parameterName + "\""));
        }
        json.put(parameterName, convertToJsonObject(
            command.getParameters().get(parameterName), isArgs));
      }
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    return json.toString();
  }
  
  static Object convertToJsonObject(Object object, boolean wrapArgs) throws JSONException {
    if (object.getClass().isArray()) {
      object = Arrays.asList((Object[]) object);
    }
    if (object instanceof List) {
      JSONArray array = new JSONArray();
      for (Object o : (List) object) {
        if (wrapArgs) {
          array.put(wrapArgumentForScriptExecution(o));
        } else {
          array.put(o);
        }
      }
      return array;
    } else if (object instanceof Cookie) {
      Cookie cookie = (Cookie)object;
      Map<String, Object> cookieMap = new HashMap<String, Object>();
      cookieMap.put("name", cookie.getName());
      cookieMap.put("value", cookie.getValue());
      cookieMap.put("domain", cookie.getDomain());
      cookieMap.put("path", cookie.getPath());
      cookieMap.put("secure", cookie.isSecure());
      cookieMap.put("expiry", cookie.getExpiry());
      return new JSONObject(cookieMap);
    } else if (object instanceof ChromeWebElement) {
      return ((ChromeWebElement)object).getId();
    } else {
      return object;
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
    try {
      String httpMessage = "HTTP/1.1 200 OK" +
      "\r\nContent-Length: " + message.getBytes("UTF-8").length + 
      "\r\nContent-Type: " + contentType + 
      "\r\n\r\n" + message;
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
    Socket socket = getOldestSocket();
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

  private Socket getOldestSocket() {
    Socket socket;
    //Peek, rather than poll, so that if it all goes horribly wrong,
    //we can just close all sockets in the queue,
    //not having to worry about the current ones
    while ((socket = listeningThread.sockets.peek()) == null) {
      Thread.yield();

      // If Chrome is no longer alive, this loop will block indefinitely, so go
      // ahead and throw an error to let our clients know something very bad
      // happened.
      if (!chromeBinary.isAlive()) {
        // There is no longer a client to listen for, so go ahead and shutdown.
        stop();
        throw new DeadChromeException("Chrome has unexpectedly died.");
      }
    }
    return socket;
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

    ChromeResponse response;
    try {
      JSONObject jsonObject = new JSONObject(rawJsonString);
      if (!jsonObject.has("statusCode")) {
        throw new WebDriverException("Response had no status code. Response was: " + rawJsonString);
      }
      response = new ChromeResponse(jsonObject.getInt("statusCode"),
          jsonObject.has("value") && !jsonObject.isNull("value") ? jsonObject.get("value") : null);
    } catch (JSONException e) {
      throw new WebDriverException(e); 
    }

    if (response.getStatusCode() == 0) {
      //Success! Parse value
      if (response.getValue() == null) {
        return response;
      }
      Object value = response.getValue();
      try {
        Object parsedValue = parseJsonToObject(value);
        if (parsedValue instanceof ChromeWebElement) {
          return new ChromeResponse(-1, ((ChromeWebElement)parsedValue).getId());
        } else {
          return new ChromeResponse(0, parsedValue);
        }
      } catch (Exception e) {
        throw new WebDriverException(e);
      }
    } else {
      String message = "";
      Object value = response.getValue();
      if (value instanceof JSONObject) {
        JSONObject jsonObject = (JSONObject) value;
        try {
          if (jsonObject.has("message") && jsonObject.get("message") instanceof String) {
            message = jsonObject.getString("message");
          }
        } catch (JSONException e) {
          throw new WebDriverException(e);
        }
      }

      switch (response.getStatusCode()) {
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
  }
  
  private Object parseJsonToObject(Object value) throws Exception {
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
          innerValue = ((Number) innerValue).longValue();
        }
        return innerValue;
      } else if ("ELEMENT".equals(object.getString("type"))) {
        return new ChromeWebElement(null, (String)object.get("value"));
      } else {
        return jsonToMap(object);
      }
    } else {
      throw new WebDriverException("Didn't know how to deal with " +
          "response value of type: " + value.getClass());
    }
  }

  @SuppressWarnings({"unchecked"})
  private Map<String, Object> jsonToMap(JSONObject json) throws Exception {
    return (Map<String, Object>) new JsonToBeanConverter().convert(Map.class, json.toString());
  }

  /**
   * Launches Chrome and starts listening for connections from it.
   */
  public void start() {
    while (!hasClient()) {
      stop();
      startListening();
      try {
        chromeBinary.start();
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
   * Kills Chrome and closes the server socket.
   */
  public void stop() {
    chromeBinary.kill();
    stopListening();
  }

  /**
   * Starts listening for new socket connections from Chrome.
   * Doesn't return until the TCP port is connected to.
   */
  public void startListening() {
    if (listeningThread == null) {
      listen = true;
      listeningThread = new ListeningThread(port);
      listeningThread.start();
    }
  }

  /**
   * Stops listening from for new sockets from Chrome
   */
  public void stopListening() {
    if (listeningThread == null) {
      return;
    }
    listen = false;
    listeningThread.stopListening();
    //TODO(danielwh): Remove this when using multiple ports (blocked on crbug.com 11547)
    try { Thread.sleep(500); } catch (InterruptedException e) {}
    listeningThread = null;
  }

  /**
   * Thread which, when spawned, accepts all sockets on its ServerSocket and
   * queues them up
   */
  private class ListeningThread extends Thread {
    private boolean isListening = false;
    private Queue<Socket> sockets = new ConcurrentLinkedQueue<Socket>();
    private final ServerSocket serverSocket;
    private volatile boolean hasClient = false;
    
    public ListeningThread(int port) {
      try {
        serverSocket = new ServerSocket(port);
      } catch (IOException e) {
        throw new WebDriverException(e);
      }
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
            while (!serverSocket.isClosed()) {
              Thread.yield();
            }
          }
        } catch (Exception e) {
          throw new WebDriverException(e);
        }
      }
    }

    private void closeCurrentSockets() {
      for (Socket socket : sockets) {
        try {
          socket.close();
          sockets.remove(socket);
        } catch (IOException e) {
          //Nothing we can sanely do here
        }
      }
    }
  }
  
  /**
   * Wraps up values as {type: some_type, value: some_value} objects
   * @param argument value to wrap up
   * @return wrapped up value; will be either a JSONObject or a JSONArray.
   * TODO(jleyba): Remove this
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
        wrappedArgument.put("value", ((ChromeWebElement)argument).getId());
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
}
