package org.openqa.selenium.firefox;

import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.JsonToBeanConverter;
import org.openqa.selenium.remote.Response;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class LightweightServer {

  private long timeOut;
  private int port;
  private BlockingQueue<Command> commands = new SynchronousQueue<Command>();
  private BlockingQueue<Response> responses = new SynchronousQueue<Response>();
  private JsonToBeanConverter jsonConverter = new JsonToBeanConverter();
  private BeanToJsonConverter beanConverter = new BeanToJsonConverter();

  public LightweightServer() {
    this(2, TimeUnit.MINUTES);
  }

  public LightweightServer(long timeOut, TimeUnit unit) {
    this.timeOut = unit.toMillis(timeOut);
  }

  public void start() {
    try {
      port = PortProber.findFreePort();
      WebServer server = WebServers.createWebServer(port);
      configure(server);
      server.start();

      // D'oh! Cast! D'oh!
      PortProber.pollPort(port, (int) timeOut, TimeUnit.MILLISECONDS);
      System.out.println("Started on port: " + port);
    } catch (IOException e) {
      throw new WebDriverException("Unable to start httpd", e);
    }
  }

  public int getPort() {
    return port;
  }

  private void configure(WebServer server) {
    server.add("/xhr", new HttpHandler() {
      public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
          throws Exception {
        Response res = jsonConverter.convert(Response.class, request.body());

        // This is fugly
        JSONObject jsonObject = new JSONObject(request.body());
        if (jsonObject.has("name") && "ping".equals(jsonObject.getString("name"))) {
          System.out.println("Ping seen and ignored.");
        } else {
          responses.put(res);
        }

        Command command = commands.take();
        String converted = beanConverter.convert(command);

        response.header("Content-Type", "application/json");
        response.header("Content-Length", converted.length());
        response.content(converted);
        response.end();
      }
    });
  }

  public void addCommand(Command command) {
    try {
      commands.put(command);
    } catch (InterruptedException e) {
      throw new WebDriverException(e);
    }
  }

  public Response getResponse() {
    try {
      return responses.take();
    } catch (InterruptedException e) {
      throw new WebDriverException(e);
    }
  }
}
