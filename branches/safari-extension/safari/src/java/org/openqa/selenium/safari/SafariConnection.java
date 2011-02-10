package org.openqa.selenium.safari;

import com.google.common.collect.Maps;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.SocketLock;
import org.openqa.selenium.remote.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: kurniady
 * Date: 1/12/11
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafariConnection implements CommandExecutor {

  private static final Logger LOG = Logger.getLogger(SafariConnection.class.getCanonicalName());

  private SafariWebSocket socket;
  private CountDownLatch connectionState;
  private Server server;

  // A lock for serializing the commands.
  private Object connectionLock;

  // A latch to be used to convert async WebSocket messaging to a synchronous WebDriver API.
  private CountDownLatch responseState;
  private String lastResponse;

  public SafariConnection() {
    connectionState = new CountDownLatch(1);
    connectionLock = new Object();
  }

  public int listen() {

    int port = 0;
    try {
      port = determineNextFreePort(SocketLock.DEFAULT_PORT);
    } catch (IOException e) {
      throw new WebDriverException("Failed to get a free port", e);
    }

    server = new Server(port);
    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(new ServletHolder(new SafariWebSocketServlet()), "/safaridriver");
    server.setHandler(handler);

    try {
      server.start();
    } catch (Exception e) {
      throw new WebDriverException("Failed to start Jetty", e);
    }
    return port;
  }

  public void quit() {
    try {
      server.stop();
    } catch (Exception e) {
      LOG.severe("Failed to stop Jetty");
      e.printStackTrace(System.err);
    }
  }

  public void waitUntilConnected() throws InterruptedException {
    connectionState.await();
  }

  public Response execute(Command command) throws IOException {
    Map<String, Object> params = Maps.newHashMap(command.getParameters());

    params.put("command", command.getName());
    if (command.getSessionId() != null) {
      params.put("sessionId", command.getSessionId().toString());
    }

    System.err.println(
        "Command: " + command.getName() +
        " SessionId: " + command.getSessionId() +
        " Parameters:" + command.getParameters());

    String converted = new BeanToJsonConverter().convert(params);
    System.err.println("Converted: " + converted);

    Response response;

    synchronized (connectionLock) {
      responseState = new CountDownLatch(1);
      socket.sendMessage(converted);

      try {
        if (responseState.await(123, TimeUnit.DAYS)) {
          response = toResponse(lastResponse);
        } else {
          throw new WebDriverException("Timeout in waiting for a response from Safari");
        }
      } catch (InterruptedException e) {
        throw new WebDriverException("Interrupted while waiting for a response from Safari", e);
      }
    }

    return response;
  }

  private Response toResponse(String lastResponse) {
    // Here is a bit different from the normal RemoteWebDriver
    // wire protocol: Safari returns a JSON for convenience.
    Response res = new JsonToBeanConverter().convert(Response.class, lastResponse);
    return res;
  }

  private WebSocket createSafariWebSocket() {
    if (socket == null) {
      socket = new SafariWebSocket();
      return socket;
    } else {
      throw new WebDriverException("Multiple sockets initiated, exactly one expected");
    }
  }

  // Stolen from Firefox Driver's NewProfileExtensionConnection
  protected static int determineNextFreePort(int port) throws IOException {
    // Attempt to connect to the given port on the host
    // If we can't connect, then we're good to use it
    int newport;

    for (newport = port; newport < port + 200; newport++) {

      Socket socket = new Socket();
      InetSocketAddress address = new InetSocketAddress("localhost", newport);

      try {
        socket.bind(address);
        return newport;
      } catch (BindException e) {
        // Port is already bound. Skip it and continue
      } finally {
        socket.close();
      }
    }

    throw new WebDriverException(
        String.format("Cannot find free port in the range %d to %d ", port, newport));
  }

  private class SafariWebSocketServlet extends WebSocketServlet {

    @Override
    protected WebSocket doWebSocketConnect(HttpServletRequest request, String arg) {
      return createSafariWebSocket();
    }
  }

  private class SafariWebSocket implements WebSocket {
    private Outbound outbound;

    public void onConnect(Outbound outbound) {
      this.outbound = outbound;
      connectionState.countDown();
    }

    public void onMessage(byte b, String s) {
      LOG.info("Received : [" + s + "]");
      lastResponse = s;
      responseState.countDown();
    }

    public void onFragment(boolean b, byte c, byte[] bytes, int i, int i1) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMessage(byte b, byte[] bytes, int i, int i1) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onDisconnect() {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    private void sendMessage(String data) throws IOException {
      LOG.info("Sending to Safari : [" + data + "]");
      outbound.sendMessage(data);
    }
  }
}
