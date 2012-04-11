package org.openqa.selenium.server;

import static org.openqa.selenium.net.PortProber.pollPort;

import static org.junit.Assert.assertTrue;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import org.openqa.selenium.Build;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.testing.InProject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class SeleniumServerProxyTest {

  private static CommandLine command;
  private static int port;

  @BeforeClass
  public static void startServer() {
    new Build().of("selenium-server-standalone").go();
    File serverJar = InProject.locate(
        "build/java/server/src/org/openqa/grid/selenium/selenium-standalone.jar");
    port = PortProber.findFreePort();
    command = new CommandLine("java", "-jar",
        serverJar.getAbsolutePath(), "-port", "" + port);
    command.executeAsync();

    pollPort(port);
  }

  @AfterClass
  public static void killServer() {
    command.destroy();
  }

  @Test @Ignore
  public void testProxiesSeleniumStaticResourcesWithUpstreamProxy()
      throws Exception {

    URL target = new URL("http://www.google.com/selenium-server/core/Blank.html");
    URLConnection client =
        target.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", port)));

    assertTrue(getResponseAsString(client).contains("<body>"));

    target = new URL("https://www.google.com/selenium-server/core/Blank.html");
    client =
        target.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", port)));
    TrustEverythingSSLTrustManager.trustAllSSLCertificates((HttpsURLConnection) client);

    assertTrue(getResponseAsString(client).contains("<body>"));
  }

  private String getResponseAsString(URLConnection client) {
    try {
      byte[] bytes = ByteStreams.toByteArray(client.getInputStream());
      return new String(bytes);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
