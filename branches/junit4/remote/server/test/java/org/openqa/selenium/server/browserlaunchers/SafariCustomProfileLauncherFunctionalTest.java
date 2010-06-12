package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 * {@link SafariCustomProfileLauncher} integration test class.
 */
public class SafariCustomProfileLauncherFunctionalTest extends LauncherFunctionalTestCase {

    @Test public void launcherWithDefaultConfiguration() throws Exception {
        launchBrowser(new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), new RemoteControlConfiguration(), "CUST", null));
    }

    @Test public void launcherWithHonorSystemProxyEnabled() throws Exception {
        final RemoteControlConfiguration configuration;

        configuration = new RemoteControlConfiguration();
        configuration.setHonorSystemProxy(true);
        launchBrowser(new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), configuration, "CUST", null));
    }

    @Test public void launchTwoBrowsersInARowWithDefaultConfiguration() throws Exception {
        final RemoteControlConfiguration configuration = new RemoteControlConfiguration();

        launchBrowser(new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), configuration, "CUST", null));
        launchBrowser(new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), configuration, "CUST", null));
    }

}