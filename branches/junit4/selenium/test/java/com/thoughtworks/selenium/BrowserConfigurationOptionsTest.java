package com.thoughtworks.selenium;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BrowserConfigurationOptionsTest {
	@Test public void canUseWithValidArg() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
		assertTrue(bco.canUse("foobar"));
	}
	
	@Test public void canUseWithNullArg() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
		assertFalse(bco.canUse(null));
	}
	
	@Test public void canUseWithEmptyArg() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
		assertFalse(bco.canUse(""));
	}
	
	@Test public void setProfileWithNullDoesNotSet() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setProfile(null);
		assertFalse(bco.isSet(BrowserConfigurationOptions.PROFILE_NAME));
	}
	
	@Test public void setProfileWithNonNullDoesSet() {
		String profile = "foo";
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setProfile(profile);
		assertTrue(bco.isSet(BrowserConfigurationOptions.PROFILE_NAME));
		assertEquals(profile, bco.getProfile());
	}
	
	@Test public void setSingleWindow() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setSingleWindow();
		assertTrue(bco.isSingleWindow());
		assertTrue(bco.isSet(BrowserConfigurationOptions.SINGLE_WINDOW));
	}
	
	@Test public void setSingleWindowWhenMultiWindowWasAlreadySet() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setMultiWindow().setSingleWindow();
		assertTrue(bco.isSingleWindow());
		assertFalse(bco.isMultiWindow());
		assertTrue(bco.isSet(BrowserConfigurationOptions.SINGLE_WINDOW));
	}
	
	@Test public void setMultiWindow() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setMultiWindow();
		assertTrue(bco.isMultiWindow());
		assertTrue(bco.isSet(BrowserConfigurationOptions.MULTI_WINDOW));
	}
	
	@Test public void setMultiWindowWhenSingleWindowWasAlreadySet() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setSingleWindow().setMultiWindow();
		assertTrue(bco.isMultiWindow());
		assertFalse(bco.isSingleWindow());
		assertTrue(bco.isSet(BrowserConfigurationOptions.MULTI_WINDOW));
	}
	
	@Test public void setBrowserExecutablePathWithNullPath() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setBrowserExecutablePath(null);
		assertFalse(bco.isSet(BrowserConfigurationOptions.BROWSER_EXECUTABLE_PATH));
	}
	
	@Test public void setBrowserExcecutablePathWithValidPath() {
		String path = "c:\\chrome\\is\\cool.exe with_arg";
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setBrowserExecutablePath(path);
		assertTrue(bco.isSet(BrowserConfigurationOptions.BROWSER_EXECUTABLE_PATH));
		assertEquals(path, bco.getBrowserExecutablePath());
	}
	
	@Test public void setTimeoutInSeconds() {
		int timeout = 17;
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setTimeoutInSeconds(timeout);
		assertTrue(bco.isSet(BrowserConfigurationOptions.TIMEOUT_IN_SECONDS));
		assertEquals(timeout, bco.getTimeoutInSeconds());
	}
	
	@Test public void getTimeoutWhenNoneSet() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
		assertEquals(BrowserConfigurationOptions.DEFAULT_TIMEOUT_IN_SECONDS, bco.getTimeoutInSeconds());
	}

	@Test public void browserModeWithNullMode() {
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setBrowserMode(null);
		assertFalse(bco.isSet(BrowserConfigurationOptions.BROWSER_MODE));
	}
	
	@Test public void browserModeWithNonNullMode() {
		String mode = "hta";
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions().setBrowserMode(mode);
		assertTrue(bco.isSet(BrowserConfigurationOptions.BROWSER_MODE));
		assertEquals(mode, bco.getBrowserMode());
	}
	
	@Test public void serverOptionsCanLoadClientOptions() {
		String profile = "foo";
		String execPath = "c:\\simon stewart\\likes\\cheese";
		BrowserConfigurationOptions bco = new BrowserConfigurationOptions()
			.setSingleWindow()
			.setProfile(profile)
			.setBrowserExecutablePath(execPath);
		org.openqa.selenium.server.BrowserConfigurationOptions serverOptions =
			new org.openqa.selenium.server.BrowserConfigurationOptions(bco.toString());
		assertEquals(profile, serverOptions.getProfile());
		assertEquals(execPath, serverOptions.getExecutablePath());
		assertTrue(serverOptions.isSingleWindow());
	}
	
}
