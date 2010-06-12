package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 * {@link FirefoxChromeLauncher} integration test class.
 */
public class FirefoxChromeLauncherIntegrationTest extends LauncherFunctionalTestCase {

    @Test public void launcherWithDefaultConfiguration() throws Exception {
        launchBrowser(new FirefoxChromeLauncher(new BrowserConfigurationOptions(), new RemoteControlConfiguration(), "CUSTFFCHROME", (String)null));
    }

    @Test public void launchTwoBrowsersInARowWithDefaultConfiguration() throws Exception {
        final RemoteControlConfiguration configuration = new RemoteControlConfiguration();

        launchBrowser(new FirefoxChromeLauncher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", (String)null));
        launchBrowser(new FirefoxChromeLauncher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", (String)null));
    }

}