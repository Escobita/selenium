package org.openqa.selenium.server.browserlaunchers;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.openqa.jetty.log.LogFactory;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;


/**
 * {@link org.openqa.selenium.server.browserlaunchers.SafariCustomProfileLauncher} integration test class.
 */
public class SafariLauncherIntegrationTest {

    private static final Log LOGGER = LogFactory.getLog(SafariLauncherIntegrationTest.class);
    private static final int SECONDS = 1000;
    private static final int WAIT_TIME = 15 * SECONDS;

    @Test public void launcherWithDefaultConfiguration() throws Exception {
        final SafariCustomProfileLauncher launcher;

        launcher = new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), new RemoteControlConfiguration(), "CUST", null);
        launcher.launch("http://www.google.com");
        int seconds = 15;
        LOGGER.info("Killing browser in " + Integer.toString(seconds) + " seconds");
        AsyncExecute.sleepTight(WAIT_TIME);
        launcher.close();
        LOGGER.info("He's dead now, right?");
    }

    @Test public void launcherWithHonorSystemProxyEnabled() throws Exception {
        final SafariCustomProfileLauncher launcher;
        final RemoteControlConfiguration configuration;

        configuration = new RemoteControlConfiguration();
        configuration.setHonorSystemProxy(true);
        
        launcher = new SafariCustomProfileLauncher(new BrowserConfigurationOptions(), configuration, "CUST", null);
        launcher.launch("http://www.google.com");
        int seconds = 15;
        LOGGER.info("Killing browser in " + Integer.toString(seconds) + " seconds");
        AsyncExecute.sleepTight(WAIT_TIME);
        launcher.close();
        LOGGER.info("He's dead now, right?");
    }

}
