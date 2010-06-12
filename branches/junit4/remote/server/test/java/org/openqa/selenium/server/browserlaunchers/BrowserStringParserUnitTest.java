package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * {@link BrowserInstallationCache} unit test class.
 */
public class BrowserStringParserUnitTest {

    @Test public void browserStartCommandMatchWhenBrowserStringIsTheBrowserName() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "firefox");
        assertTrue(result.match());
        assertNull(result.customLauncher());
    }
    @Test public void browserStartCommandMatchWhenBrowserStringIsStarTheBrowserName() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "*firefox");
        assertTrue(result.match());
    }

    @Test public void browserStartCommandDoNotMatchWhenBrowsersAreWayDifferent() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "*safari");
        assertFalse(result.match());
        assertNull(result.customLauncher());
    }

    @Test public void browserStartCommandMatchWhenCustomLauncherIsProvided() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "*firefox /a/custom/launcher");
        assertTrue(result.match());
        assertEquals("/a/custom/launcher", result.customLauncher());
    }

    @Test public void browserStartCommandDoNotMatchWhenBrowsersisASubstring() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "*firefoxproxy");
        assertFalse(result.match());
        assertNull(result.customLauncher());
    }

    @Test public void browserStartCommandIsNullWhenThereIsNothingButSpaceAfterTheBrowserName() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("firefox", "firefox    ");
        assertTrue(result.match());
        assertNull(result.customLauncher());
    }

    @Test public void browserStartCommandMatchIgnoredTrailingSpacesWhenCustomLauncherIsProvided() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("iexplore", "*iexplore /a/custom/launcher   ");
        assertTrue(result.match());
        assertEquals("/a/custom/launcher", result.customLauncher());
    }

    @Test public void browserStartCommandMatchPreservedSpacesWhithinCustomLauncher() {
        final BrowserStringParser.Result result;

        result = new BrowserStringParser().parseBrowserStartCommand("hta", "*hta '/a/custom/launcher with space'   ");
        assertTrue(result.match());
        assertEquals("'/a/custom/launcher with space'", result.customLauncher());
    }

}
