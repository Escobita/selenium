package org.openqa.selenium.server.cli;


import org.openqa.selenium.server.RemoteControlConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link org.openqa.selenium.server.cli.RemoteControlLauncher} unit test class.
 */
public class RemoteControlLauncherTest {
    public void testHonorSystemProxyIsSetWhenProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;
        
        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{"-honor-system-proxy"});
        assertTrue(configuration.honorSystemProxy());
    }

    public void testHonorSystemProxyIsFalseWhenNotProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;

        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{});
        assertFalse(configuration.honorSystemProxy());
    }

}
