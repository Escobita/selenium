package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 * {@link Firefox2Launcher} integration test class.
 */
public class Firefox2LauncherFunctionalTest extends LauncherFunctionalTestCase {

    @Test public void launcherWithDefaultConfiguration() throws Exception {
        launchBrowser(new Firefox2Launcher(new BrowserConfigurationOptions(), new RemoteControlConfiguration(), "CUSTFFCHROME", null));
    }

    @Test public void launchTwoBrowsersInARowWithDefaultConfiguration() throws Exception {
        final RemoteControlConfiguration configuration = new RemoteControlConfiguration();

        launchBrowser(new Firefox2Launcher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", null));
        launchBrowser(new Firefox2Launcher(new BrowserConfigurationOptions(), configuration, "CUSTFFCHROME", null));
    }


}