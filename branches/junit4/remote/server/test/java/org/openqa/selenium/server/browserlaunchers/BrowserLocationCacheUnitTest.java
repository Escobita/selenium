package org.openqa.selenium.server.browserlaunchers;

import org.junit.Test;
import org.openqa.selenium.server.browserlaunchers.locators.BrowserLocator;

import static org.junit.Assert.assertEquals;

/**
 * {@link BrowserInstallationCache} unit test class.
 */
public class BrowserLocationCacheUnitTest {

    public void tesCacheKeyIsTheBrowserStringWhenNoCustomPathIsProvided() {
        assertEquals("*aBrowser", new BrowserInstallationCache().cacheKey("*aBrowser", null));
    }

    @Test public void cacaheIsTheBrowserStringConcatenatedWithCustomPathWhenCustomPathIsProvided() {
        assertEquals("*aBrowseraCustomPath", new BrowserInstallationCache().cacheKey("*aBrowser", "aCustomPath"));
    }

    @Test public void locateBrowserInstallationUseLocatorWhenCacheIsEmpty() {
        final BrowserInstallation expectedInstallation;
        final BrowserLocator locator;

        expectedInstallation = new BrowserInstallation(null, null);
        locator = new BrowserLocator() {

            public BrowserInstallation findBrowserLocationOrFail() {
                return expectedInstallation;
            }

            public BrowserInstallation retrieveValidInstallationPath(String customLauncherPath) {
                throw new UnsupportedOperationException();
            }

        };

        assertEquals(expectedInstallation,
                     new BrowserInstallationCache().locateBrowserInstallation("aBrowser", null, locator));
    }

    @Test public void locateBrowserInstallationUseCacheOnSecondAccess() {
        final BrowserInstallation expectedInstallation;
        final BrowserInstallationCache cache;
        final BrowserLocator locator;

        expectedInstallation = new BrowserInstallation(null, null);
        locator = new BrowserLocator() {

            public BrowserInstallation findBrowserLocationOrFail() {
                return expectedInstallation;
            }

            public BrowserInstallation retrieveValidInstallationPath(String customLauncherPath) {
                throw new UnsupportedOperationException();
            }
        };

        cache = new BrowserInstallationCache();
        cache.locateBrowserInstallation("aBrowser", null, locator);
        assertEquals(expectedInstallation, cache.locateBrowserInstallation("aBrowser", null, null));
    }

    @Test public void locateBrowserInstallationUseLocatorWhenCacheIsEmptyAndACustomPathIsProvided() {
        final BrowserInstallation expectedInstallation;
        final BrowserLocator locator;

        expectedInstallation = new BrowserInstallation(null, null);
        locator = new BrowserLocator() {

            public BrowserInstallation findBrowserLocationOrFail() {
                throw new UnsupportedOperationException();
            }

            public BrowserInstallation retrieveValidInstallationPath(String customLauncherPath) {
                if ("aCustomLauncher".equals(customLauncherPath)) {
                    return expectedInstallation;
                }
                throw new UnsupportedOperationException(customLauncherPath);
            }

        };

        assertEquals(expectedInstallation,
                     new BrowserInstallationCache().locateBrowserInstallation("aBrowser", "aCustomLauncher", locator));
    }

    @Test public void locateBrowserInstallationUseCacheOnSecondAccessWhenCustomLauncherIsProvided() {
        final BrowserInstallation expectedInstallation;
        final BrowserInstallationCache cache;
        final BrowserLocator locator;

        expectedInstallation = new BrowserInstallation(null, null);
        locator = new BrowserLocator() {

            public BrowserInstallation findBrowserLocationOrFail() {
                throw new UnsupportedOperationException();
            }

            public BrowserInstallation retrieveValidInstallationPath(String customLauncherPath) {
                if ("aCustomLauncher".equals(customLauncherPath)) {
                    return expectedInstallation;
                }
                throw new UnsupportedOperationException(customLauncherPath);
            }
        };

        cache = new BrowserInstallationCache();
        cache.locateBrowserInstallation("aBrowser", "aCustomLauncher", locator);
        assertEquals(expectedInstallation, cache.locateBrowserInstallation("aBrowser", "aCustomLauncher", null));
    }

}
