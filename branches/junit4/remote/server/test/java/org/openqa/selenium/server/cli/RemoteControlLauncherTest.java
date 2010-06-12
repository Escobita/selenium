package org.openqa.selenium.server.cli;

import org.junit.Test;
import org.openqa.selenium.server.RemoteControlConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link org.openqa.selenium.server.cli.RemoteControlLauncher} unit test class.
 */
public class RemoteControlLauncherTest {
    @Test public void honorSystemProxyIsSetWhenProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;
        
        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{"-honor-system-proxy"});
        assertTrue(configuration.honorSystemProxy());
    }

    @Test public void honorSystemProxyIsFalseWhenNotProvidedAsAnOption() {
        final RemoteControlConfiguration configuration;

        configuration = RemoteControlLauncher.parseLauncherOptions(new String[]{});
        assertFalse(configuration.honorSystemProxy());
    }

}
