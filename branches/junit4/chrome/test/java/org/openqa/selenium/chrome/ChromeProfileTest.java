package org.openqa.selenium.chrome;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ChromeProfileTest {
  @Test public void ProxyDirect() throws IOException {
    ChromeBinary binary = newBinaryWithProxy(new Proxy().setProxyType(ProxyType.DIRECT));
    String commandline = new ArrayList<String>(binary.getCommandline("foo")).toString();
    assertThat(commandline, containsString("--no-proxy-server"));
  }
  
  @Test public void ProxyAutoconfigUrl() throws IOException {
    ChromeBinary binary =
        newBinaryWithProxy(new Proxy().setProxyAutoconfigUrl("http://foo/bar.pac"));
    String commandline = new ArrayList<String>(binary.getCommandline("foo")).toString();
    assertThat(commandline, containsString("--proxy-pac-url=http://foo/bar.pac"));
  }
  
  @Test public void ProxyAutodetect() throws IOException {
    ChromeBinary binary = newBinaryWithProxy(new Proxy().setAutodetect(true));
    String commandline = new ArrayList<String>(binary.getCommandline("foo")).toString();
    assertThat(commandline, containsString("--proxy-auto-detect"));
  }
  
  @Test public void ManualProxy() throws IOException {
    ChromeBinary binary = newBinaryWithProxy(new Proxy().setHttpProxy("foo:123"));
    String commandline = new ArrayList<String>(binary.getCommandline("foo")).toString();
    assertThat(commandline, containsString("--proxy-server=foo:123"));
  }
  
  private ChromeBinary newBinaryWithProxy(Proxy proxy) {
    ChromeProfile profile = new ChromeProfile();
    profile.setProxy(proxy);
    return new ChromeBinary(profile, new ChromeExtension());
  }
}
