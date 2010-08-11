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

package org.openqa.selenium.remote;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriverException;

import static org.apache.http.protocol.ExecutionContext.HTTP_TARGET_HOST;
import static org.openqa.selenium.remote.DriverCommand.*;

public class HttpCommandExecutor implements CommandExecutor {

  private final String remotePath;
  private URL remoteServer;

  private enum HttpVerb {

    GET() {
      public HttpUriRequest createMethod(String url) {
        HttpGet getMethod = new HttpGet(url);
//        getMethod.setFollowRedirects(true);
        return getMethod;
      }
    },
    POST() {
      public HttpUriRequest createMethod(String url) {
        return new HttpPost(url);
      }
    },
    DELETE() {
      public HttpUriRequest createMethod(String url) {
        return new HttpDelete(url);
      }
    };

    public abstract HttpUriRequest createMethod(String url);
  }

  private Map<String, CommandInfo> nameToUrl;
  private HttpClient client;

  public HttpCommandExecutor(URL addressOfRemoteServer) {
    if (addressOfRemoteServer == null) {
      String remoteServer = System.getProperty("webdriver.remote.server");
      if (remoteServer != null) {
        try {
          addressOfRemoteServer = new URL(remoteServer);
        } catch (MalformedURLException e) {
          throw new WebDriverException(e);
        }
      }
      if (addressOfRemoteServer == null)
        throw new IllegalArgumentException("You must specify a remote address to connect to");
    }

    this.remoteServer = addressOfRemoteServer;
    this.remotePath = addressOfRemoteServer.getPath();

    client = new DefaultHttpClient();
//    client.getConnectionManager().setHost(uri);

    nameToUrl = ImmutableMap.<String, CommandInfo>builder()
        .put(NEW_SESSION, post("/session"))
        .put(QUIT, delete("/session/:sessionId"))
        .put(GET_CURRENT_WINDOW_HANDLE, get("/session/:sessionId/window_handle"))
        .put(GET_WINDOW_HANDLES, get("/session/:sessionId/window_handles"))
        .put(GET, post("/session/:sessionId/url"))
        .put(GO_FORWARD, post("/session/:sessionId/forward"))
        .put(GO_BACK, post("/session/:sessionId/back"))
        .put(REFRESH, post("/session/:sessionId/refresh"))
        .put(EXECUTE_SCRIPT, post("/session/:sessionId/execute"))
        .put(GET_CURRENT_URL, get("/session/:sessionId/url"))
        .put(GET_TITLE, get("/session/:sessionId/title"))
        .put(GET_PAGE_SOURCE, get("/session/:sessionId/source"))
        .put(SCREENSHOT, get("/session/:sessionId/screenshot"))
        .put(SET_BROWSER_VISIBLE, post("/session/:sessionId/visible"))
        .put(IS_BROWSER_VISIBLE, get("/session/:sessionId/visible"))
        .put(FIND_ELEMENT, post("/session/:sessionId/element"))
        .put(FIND_ELEMENTS, post("/session/:sessionId/elements"))
        .put(GET_ACTIVE_ELEMENT, post("/session/:sessionId/element/active"))
        .put(FIND_CHILD_ELEMENT, post("/session/:sessionId/element/:id/element"))
        .put(FIND_CHILD_ELEMENTS, post("/session/:sessionId/element/:id/elements"))
        .put(CLICK_ELEMENT, post("/session/:sessionId/element/:id/click"))
        .put(CLEAR_ELEMENT, post("/session/:sessionId/element/:id/clear"))
        .put(SUBMIT_ELEMENT, post("/session/:sessionId/element/:id/submit"))
        .put(GET_ELEMENT_TEXT, get("/session/:sessionId/element/:id/text"))
        .put(SEND_KEYS_TO_ELEMENT, post("/session/:sessionId/element/:id/value"))
        .put(GET_ELEMENT_VALUE, get("/session/:sessionId/element/:id/value"))
        .put(GET_ELEMENT_TAG_NAME, get("/session/:sessionId/element/:id/name"))
        .put(IS_ELEMENT_SELECTED, get("/session/:sessionId/element/:id/selected"))
        .put(SET_ELEMENT_SELECTED, post("/session/:sessionId/element/:id/selected"))
        .put(TOGGLE_ELEMENT, post("/session/:sessionId/element/:id/toggle"))
        .put(IS_ELEMENT_ENABLED, get("/session/:sessionId/element/:id/enabled"))
        .put(IS_ELEMENT_DISPLAYED, get("/session/:sessionId/element/:id/displayed"))
        .put(HOVER_OVER_ELEMENT, post("/session/:sessionId/element/:id/hover"))
        .put(GET_ELEMENT_LOCATION, get("/session/:sessionId/element/:id/location"))
        .put(GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW,
            get("/session/:sessionId/element/:id/location_in_view"))
        .put(GET_ELEMENT_SIZE, get("/session/:sessionId/element/:id/size"))
        .put(GET_ELEMENT_ATTRIBUTE, get("/session/:sessionId/element/:id/attribute/:name"))
        .put(ELEMENT_EQUALS, get("/session/:sessionId/element/:id/equals/:other"))
        .put(GET_ALL_COOKIES, get("/session/:sessionId/cookie"))
        .put(ADD_COOKIE, post("/session/:sessionId/cookie"))
        .put(DELETE_ALL_COOKIES, delete("/session/:sessionId/cookie"))
        .put(DELETE_COOKIE, delete("/session/:sessionId/cookie/:name"))
        .put(SWITCH_TO_FRAME, post("/session/:sessionId/frame"))
        .put(SWITCH_TO_WINDOW, post("/session/:sessionId/window"))
        .put(CLOSE, delete("/session/:sessionId/window"))
        .put(DRAG_ELEMENT, post("/session/:sessionId/element/:id/drag"))
        .put(GET_SPEED, get("/session/:sessionId/speed"))
        .put(SET_SPEED, post("/session/:sessionId/speed"))
        .put(GET_ELEMENT_VALUE_OF_CSS_PROPERTY,
             get("/session/:sessionId/element/:id/css/:propertyName"))
        .put(IMPLICITLY_WAIT, post("/session/:sessionId/timeouts/implicit_wait"))
        .put(EXECUTE_SQL, post("/session/:sessionId/execute_sql"))
        .put(GET_LOCATION, get("/session/:sessionId/location"))
        .put(SET_LOCATION, post("/session/:sessionId/location"))
        .put(GET_APP_CACHE, get("/session/:sessionId/application_cache"))
        .put(GET_APP_CACHE_STATUS, get("/session/:sessionId/application_cache/status"))
        .put(IS_BROWSER_ONLINE, get("/session/:sessionId/browser_connection"))
        .put(SET_BROWSER_ONLINE, post("/session/:sessionId/browser_connection"))
        
        .put(GET_LOCAL_STORAGE_ITEM, get("/session/:sessionId/local_storage/:key"))
        .put(REMOVE_LOCAL_STORAGE_ITEM, delete("/session/:sessionId/local_storage/:key"))
        .put(GET_LOCAL_STORAGE_KEYS, get("/session/:sessionId/local_storage"))
        .put(SET_LOCAL_STORAGE_ITEM, post("/session/:sessionId/local_storage"))
        .put(CLEAR_LOCAL_STORAGE, delete("/session/:sessionId/local_storage"))
        .put(GET_LOCAL_STORAGE_SIZE, get("/session/:sessionId/local_storage/size"))
        
        .put(GET_SESSION_STORAGE_ITEM, get("/session/:sessionId/session_storage/:key"))
        .put(REMOVE_SESSION_STORAGE_ITEM, delete("/session/:sessionId/session_storage/:key"))
        .put(GET_SESSION_STORAGE_KEYS, get("/session/:sessionId/session_storage"))
        .put(SET_SESSION_STORAGE_ITEM, post("/session/:sessionId/session_storage"))
        .put(CLEAR_SESSION_STORAGE, delete("/session/:sessionId/session_storage"))
        .put(GET_SESSION_STORAGE_SIZE, get("/session/:sessionId/session_storage/size"))
        .build();
  }

  public URL getAddressOfRemoteServer() {
    return remoteServer;
  }

  public Response execute(Command command) throws Exception {
    CommandInfo info = nameToUrl.get(command.getName());
    HttpUriRequest httpMethod = info.getMethod(remoteServer, command);

    httpMethod.addHeader("Accept", "application/json, image/png");

    String payload = new BeanToJsonConverter().convert(command.getParameters());

    if (httpMethod instanceof HttpPost) {
      ((HttpPost) httpMethod).setEntity(new StringEntity(payload, "UTF-8"));
    }

    HttpContext context = new BasicHttpContext();
    long start = System.currentTimeMillis();
    HttpResponse response = client.execute(httpMethod, context);

    long mid = System.currentTimeMillis();

    // TODO: SimonStewart: 2008-04-25: This is really shabby
    if (isRedirect(response)) {
      Header newLocation = response.getFirstHeader("location");
      httpMethod = new HttpGet(newLocation.getValue());
      httpMethod.addHeader("Accept", "application/json, image/png");
      response.getEntity().consumeContent();
      response = client.execute(httpMethod, context);
    }

    Response res = createResponse(response, context);

    long end = System.currentTimeMillis();

    System.out.println(String.format("start -> mid: %d, mid -> end: %d", mid - start, end - mid));

    return res;
  }

  private Response createResponse(HttpResponse httpResponse, HttpContext context) throws Exception {
    Response response;

    Header header = httpResponse.getFirstHeader("Content-Type");

    if (header != null && header.getValue().startsWith("application/json")) {
      HttpEntity httpEntity = httpResponse.getEntity();
      System.out.println("httpEntity = " + httpEntity);
      response = new JsonToBeanConverter().convert(Response.class, EntityUtils.toString(httpEntity));
    } else {
      response = new Response();

      if (header != null && header.getValue().startsWith("image/png")) {
        response.setValue(EntityUtils.toByteArray(httpResponse.getEntity()));
      } else if (httpResponse.getEntity() != null) {
        response.setValue(EntityUtils.toString(httpResponse.getEntity()));
      }

      HttpHost target = (HttpHost) context.getAttribute(HTTP_TARGET_HOST);
      String uri = target.toURI();
      int sessionIndex = uri.indexOf("/session/");
      if (sessionIndex != -1) {
        sessionIndex += "/session/".length();
        int nextSlash = uri.indexOf("/", sessionIndex);
        if (nextSlash != -1) {
          response.setSessionId(uri.substring(sessionIndex, nextSlash));
        }
      }
    }

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (!(statusCode > 199 && statusCode < 300)) {
      // 4xx represents an unknown command or a bad request.
      if (statusCode > 399 && statusCode < 500) {
        response.setStatus(ErrorCodes.UNKNOWN_COMMAND);
      } else if (statusCode > 499 && statusCode < 600) {
        // 5xx represents an internal server error. The response status should already be set, but
        // if not, set it to a general error code.
        if (response.getStatus() == ErrorCodes.SUCCESS) {
          response.setStatus(ErrorCodes.UNHANDLED_ERROR);
        }
      } else {
        response.setStatus(ErrorCodes.UNHANDLED_ERROR);
      }
    }


    if (response.getValue() instanceof String) {
      //We normalise to \n because Java will translate this to \r\n
      //if this is suitable on our platform, and if we have \r\n, java will
      //turn this into \r\r\n, which would be Bad!
      response.setValue(((String)response.getValue()).replace("\r\n", "\n"));
    }
    
    return response;
  }

  private boolean isRedirect(HttpResponse response) {
    int code = response.getStatusLine().getStatusCode();
    return (code == 301 || code == 302 || code == 303 || code == 307)
           && response.containsHeader("location");
  }

  private static CommandInfo get(String url) {
    return new CommandInfo(url, HttpVerb.GET);
  }

  private static CommandInfo post(String url) {
    return new CommandInfo(url, HttpVerb.POST);
  }

  private static CommandInfo delete(String url) {
    return new CommandInfo(url, HttpVerb.DELETE);
  }

  private static class CommandInfo {

    private final String url;
    private final HttpVerb verb;

    public CommandInfo(String url, HttpVerb verb) {
      this.url = url;
      this.verb = verb;
    }

    public HttpUriRequest getMethod(URL base, Command command) {
      StringBuilder urlBuilder = new StringBuilder();
      urlBuilder.append(base.getProtocol()).append("://");
      urlBuilder.append(base.getHost()).append(":").append(base.getPort());
      urlBuilder.append(base.getPath());
      for (String part : url.split("/")) {
        if (part.length() == 0) {
          continue;
        }

        urlBuilder.append("/");
        if (part.startsWith(":")) {
          String value = get(part.substring(1), command);
          if (value != null) {
            urlBuilder.append(get(part.substring(1), command));
          }
        } else {
          urlBuilder.append(part);
        }
      }

      System.out.println("urlBuilder = " + urlBuilder);
      return verb.createMethod(urlBuilder.toString());
    }

    private String get(String propertyName, Command command) {
      if ("sessionId".equals(propertyName)) {
        return command.getSessionId().toString();
      }

      // Attempt to extract the property name from the parameters
      Object value = command.getParameters().get(propertyName);
      if (value != null) {
        try {
          return URLEncoder.encode(String.valueOf(value), "UTF-8");
        } catch (UnsupportedEncodingException e) {
          // Can never happen. UTF-8 ships with java
          return String.valueOf(value);
        }
      }
      return null;
    }
  }
}
