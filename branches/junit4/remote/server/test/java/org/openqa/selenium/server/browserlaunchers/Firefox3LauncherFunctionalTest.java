package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 * {@link org.openqa.selenium.server.browserlaunchers.Firefox3Launcher} integration test class.
 */
public class Firefox3LauncherFunctionalTest extends LauncherFunctionalTestCase {

    @Test public void launcherWithDefaultConfiguration() throws Exception {
        launchBrowser(new Firefox3Launcher(new BrowserConfigurationOptions(), new RemoteControlConfiguration(), "CUSTFFCHROME", null));
    }

    @Test public void launchTwoBrowsersInARowWithDefaultConfiguration() throws Exception {
        final RemoteControlConfiguration configuration = new RemoteControlConfiguration();

        launchBrowser(new Firefox3Launcher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", null));
        launchBrowser(new Firefox3Launcher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", null));
    }

}