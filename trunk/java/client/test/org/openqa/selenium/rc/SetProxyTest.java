/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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


package org.openqa.selenium.rc;

import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.remote.CapabilityType.PROXY;
import static org.openqa.selenium.testing.Ignore.Driver.ANDROID;
import static org.openqa.selenium.testing.Ignore.Driver.IE;
import static org.openqa.selenium.testing.Ignore.Driver.IPHONE;
import static org.openqa.selenium.testing.Ignore.Driver.SELENESE;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.openqa.selenium.Pages;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.environment.GlobalTestEnvironment;
import org.openqa.selenium.environment.InProcessTestEnvironment;
import org.openqa.selenium.environment.TestEnvironment;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.SeleniumTestRunner;
import org.openqa.selenium.testing.drivers.Browser;
import org.openqa.selenium.testing.drivers.BrowserToCapabilities;
import org.openqa.selenium.testing.drivers.WebDriverBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Ignore(value = {ANDROID, IE, IPHONE, SELENESE},
        reason = "Not tested on these browsers yet.")
@RunWith(SeleniumTestRunner.class)
public class SetProxyTest {

  private static Pages pages;
  private ProxyInstance instance;

  @BeforeClass
  public static void startProxy() {
    TestEnvironment environment = GlobalTestEnvironment.get(InProcessTestEnvironment.class);
    pages = new Pages(environment.getAppServer());
  }

  @Before
  public void newProxyInstance() {
    instance = new ProxyInstance();
  }

  @After
  public void deleteProxyInstance() {
    instance.destroy();
  }

  @Test
  public void shouldAllowProxyToBeSetViaTheCapabilities() {
    Proxy proxy = instance.asProxy();

    DesiredCapabilities caps = BrowserToCapabilities.of(Browser.detect());
    if (caps == null) {
      caps = new DesiredCapabilities();
    }
    caps.setCapability(PROXY, proxy);

    WebDriver driver = new WebDriverBuilder().setDesiredCapabilities(caps).get();

    driver.get(pages.simpleTestPage);
    driver.quit();

    assertTrue(instance.hasBeenCalled());
  }

  @Test
  public void shouldAllowProxyToBeConfiguredAsAPac() throws IOException {
    String pac = String.format(
        "function FindProxyForURL(url, host) {\n" +
        "  return 'PROXY %s';\n" +
        "}", instance.baseUrl);
    TemporaryFilesystem tempFs = TemporaryFilesystem.getDefaultTmpFS();
    File base = tempFs.createTempDir("proxy", "test");
    File pacFile = new File(base, "proxy.pac");
    // Use the default platform charset because otherwise IE gets upset. Apparently.
    Files.write(pac, pacFile, Charset.defaultCharset());

    String autoConfUrl = pacFile.toURI().toString();

    Proxy proxy = new Proxy();
    proxy.setProxyAutoconfigUrl(autoConfUrl);

    DesiredCapabilities caps = BrowserToCapabilities.of(Browser.detect());
    if (caps == null) {
      caps = DesiredCapabilities.firefox();
    }
    caps.setCapability(PROXY, proxy);

    WebDriver driver = new WebDriverBuilder().setDesiredCapabilities(caps).get();

    driver.get(pages.simpleTestPage);
    driver.quit();
    tempFs.deleteTemporaryFiles();

    assertTrue(instance.hasBeenCalled());
  }

  private static class ProxyInstance {
    private DefaultHttpProxyServer proxyServer;
    private final String baseUrl;
    private final List<String> uris = Lists.newLinkedList();

    public ProxyInstance() {
      int port = PortProber.findFreePort();

      String address = new NetworkUtils().getPrivateLocalAddress();
      baseUrl = String.format("%s:%d", address, port);

      proxyServer = new DefaultHttpProxyServer(port, new HttpRequestFilter() {
        public void filter(HttpRequest httpRequest) {
          String uri = httpRequest.getUri();
          String[] parts = uri.split("/");
          if (parts.length == 0) {
            return;
          }
          String finalPart = parts[parts.length - 1];
          uris.add(finalPart);
        }
      });

      proxyServer.start();
    }

    public boolean hasBeenCalled() {
      return uris.contains("simpleTest.html");
    }

    public void destroy() {
      proxyServer.stop();
    }

    public Proxy asProxy() {
      Proxy proxy = new Proxy();
      proxy.setHttpProxy(baseUrl);
      return proxy;
    }
  }
}
