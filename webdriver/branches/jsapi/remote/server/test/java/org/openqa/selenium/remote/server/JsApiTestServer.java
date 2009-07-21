package org.openqa.selenium.remote.server;

import org.openqa.selenium.environment.webserver.Jetty6AppServer;

import java.io.File;

/**
 * Deploys a {@link Jetty6AppServer} configured to serve the JS API test files
 * on {@code http://localhost:3000/remote}.
 * 
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class JsApiTestServer {

  public static void main(String[] args) {
    Jetty6AppServer server = new Jetty6AppServer();
    File path = findWebSrc();
    if (null == path) {
      throw new IllegalStateException("Unable to locate remote/server/src/web");
    }
    server.addAdditionalWebApplication("/remote", path.getAbsolutePath());
    server.start();
  }

  protected static File findWebSrc() {
    String[] possiblePaths = {
        "remote/server/src/web",
        "../remote/server/src/web",
        "../../remote/server/src/web",
    };

    for (String potential : possiblePaths) {
      File current = new File(potential);
      if (current.exists()) {
        return current;
      }
    }
    return null;
  }
}
