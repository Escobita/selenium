package org.openqa.selenium.firefox;

import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.Build;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.internal.FileExtension;
import org.openqa.selenium.internal.InProject;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class BackwardsFirefoxDriver extends RemoteWebDriver {

  public BackwardsFirefoxDriver() {
    super(new BackwardsCommandExecutor(), DesiredCapabilities.firefox());
  }

  @Override
  protected void startClient() {
    BackwardsCommandExecutor executor = (BackwardsCommandExecutor) getCommandExecutor();

    new Build().of("//javascript/firefox-driver:webdriver").go();
    File extension = InProject.locate("build/javascript/firefox-driver/webdriver.xpi");

    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("webdriver.client.address", executor.getUrl());
    profile.addExtension("webdriver", new FileExtension(extension));

    File profileDir = profile.layoutOnDisk();
    FirefoxBinary process = new FirefoxBinary();
    try {
      process.clean(profile, profileDir);
      process.startProfile(profile, profileDir);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }

  @Override
  protected void startSession(Capabilities desiredCapabilities) {
    Response firstResponse = execute(DriverCommand.NEW_SESSION,
        ImmutableMap.of("desiredCapabilities", desiredCapabilities));
    sessionId = new SessionId((String) firstResponse.getValue());

    Response response = execute("getSessionCapabilities");
    Map caps = (Map) response.getValue();
    capabilities = new DesiredCapabilities(caps);
  }
}
