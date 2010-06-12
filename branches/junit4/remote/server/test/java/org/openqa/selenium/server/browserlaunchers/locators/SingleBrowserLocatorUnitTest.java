package org.openqa.selenium.server.browserlaunchers.locators;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * {@link org.openqa.selenium.server.browserlaunchers.AbstractBrowserLauncher} unit test class.
 */
public class SingleBrowserLocatorUnitTest {
    @Test public void humanFriendlyLauncherFileNamesReturnsEmptyStringWhenThereIsNoStandardFileNames() {
        final SingleBrowserLocator locator;

        locator = new SingleBrowserLocator() {
            protected String[] standardlauncherFilenames() {
                return new String[0];
            }

            protected String browserName() {
                return null;
            }

            protected String seleniumBrowserName() {
                return null;
            }

            protected String browserPathOverridePropertyName() {
                return null;
            }

            protected String[] usualLauncherLocations() {
                return new String[0];
            }

        };
        assertEquals("", locator.humanFriendlyLauncherFileNames());
    }

    @Test public void humanFriendlyLauncherFileNamesReturnsQuotedFileNameWhenThereIsASingleFileName() {
        final SingleBrowserLocator locator;

        locator = new SingleBrowserLocator() {

            protected String[] standardlauncherFilenames() {
                return new String[] { "a-single-browser"};
            }

            protected String browserName() {
                return null;
            }

            protected String seleniumBrowserName() {
                return null;
            }

            protected String browserPathOverridePropertyName() {
                return null;
            }

            protected String[] usualLauncherLocations() {
                return new String[0];
            }

        };
        assertEquals("'a-single-browser'", locator.humanFriendlyLauncherFileNames());
    }

    @Test public void humanFriendlyLauncherFileNamesReturnsAllFileNamesOrSeperatedWhenThereIsMoreThanOneFileName() {
        final SingleBrowserLocator locator;

        locator = new SingleBrowserLocator() {

            protected String[] standardlauncherFilenames() {
                return new String[] { "a-browser", "another-one"};
            }

            protected String browserName() {
                return null;
            }

            protected String seleniumBrowserName() {
                return null;
            }

            protected String browserPathOverridePropertyName() {
                return null;
            }

            protected String[] usualLauncherLocations() {
                return new String[0];
            }

        };
        assertEquals("'a-browser' or 'another-one'", locator.humanFriendlyLauncherFileNames());
    }

}