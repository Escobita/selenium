/*
 * Copyright 2006 ThoughtWorks, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.openqa.selenium.server.browser.launchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.browser.BrowserType.Browser;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Returns BrowserLaunchers based on simple strings given by the user
 * 
 * @author Dan Fabulich
 */
public class BrowserLauncherFactory {

	private static final Pattern CUSTOM_PATTERN = Pattern
			.compile("^\\*custom( .*)?$");

	private static final Map<Browser, Class<? extends BrowserLauncher>> supportedBrowsers = new HashMap<Browser, Class<? extends BrowserLauncher>>();

	static {
		supportedBrowsers.put(Browser.PI_FIREFOX, FirefoxCustomProfileLauncher.class);
		supportedBrowsers.put(Browser.FIREFOX, FirefoxCustomProfileLauncher.class);
		supportedBrowsers.put(Browser.IEXPLORE, InternetExplorerCustomProxyLauncher.class);
		//supportedBrowsers.put(Browser.IEHTA, HTABrowserLauncher.class);
		supportedBrowsers.put(Browser.PI_IEXPLORE, ProxyInjectionInternetExplorerCustomProxyLauncher.class);
		supportedBrowsers.put(Browser.MOCK, MockBrowserLauncher.class);
		// @todo add support for rest of browsers
		// supportedBrowsers.put("iexplore", InternetExplorerCustomProxyLauncher.class);
		// supportedBrowsers.put("safari", SafariCustomProfileLauncher.class);
		// supportedBrowsers.put("iehta", HTABrowserLauncher.class);
		// supportedBrowsers.put("chrome", FirefoxChromeLauncher.class);
		// supportedBrowsers.put("opera", OperaCustomProfileLauncher.class);
		// supportedBrowsers.put("piiexplore",
		// ProxyInjectionInternetExplorerCustomProxyLauncher.class);
		// supportedBrowsers.put("pifirefox", ProxyInjectionFirefoxCustomProfileLauncher.class);
		// supportedBrowsers.put("konqueror", KonquerorLauncher.class);
		// supportedBrowsers.put("mock", MockBrowserLauncher.class);
	}

	private SeleniumConfiguration seleniumConfiguration;

	public BrowserLauncherFactory(SeleniumConfiguration seleniumConfiguration) {
		this.seleniumConfiguration = seleniumConfiguration;
	}
	
	/**
	 * Get the set of supported browsers supported by the browser launcher factory.
	 * 
	 * @return returns the set of supported browsers.
	 */
	public static Set<Browser> getSupportedBrowsers() {
		return supportedBrowsers.keySet();
	}

	/**
	 * Get the selenium configuration.
	 * 
	 * @return Get the selenium configuration.
	 */
	public SeleniumConfiguration getSeleniumConfiguration() {
		return seleniumConfiguration;
	}

	/**
	 * Returns the browser given by the specified browser string
	 * 
	 * @param browserType
	 *            A browser type with a browser and possibly a start command
	 * @return the BrowserLauncher ready to launch
	 */
	public BrowserLauncher getBrowserLauncher(BrowserType browserType) {
		if (browserType == null)
			throw new IllegalArgumentException("browserType may not be null");

		Class<? extends BrowserLauncher> browserLauncherClass = (Class<? extends BrowserLauncher>) supportedBrowsers.get(browserType.getBrowser());
		
		if (browserLauncherClass != null) {
			String browserStartCommand = browserType.getPathToBrowser();
			
			if (browserStartCommand.equalsIgnoreCase("")) {
				browserStartCommand = null;
			}
			
			return createBrowserLauncher(browserLauncherClass, browserStartCommand);
		}
		
		throw browserNotSupported(browserType.getBrowser());
//		for (Iterator iterator = supportedBrowsers.entrySet().iterator(); iterator
//				.hasNext();) {
//			Map.Entry entry = (Map.Entry) iterator.next();
//			String name = (String) entry.getKey();
//			Class<? extends BrowserLauncher> c = (Class<? extends BrowserLauncher>) entry
//					.getValue();
//			Pattern pat = Pattern.compile("^\\*" + name + "( .*)?$");
//			Matcher mat = pat.matcher(browser.toString());
//			if (mat.find()) {
//				String browserStartCommand;
//				if (browser.equals("*" + name)) {
//					browserStartCommand = null;
//				} else {
//					browserStartCommand = mat.group(1).substring(1);
//				}
//				return createBrowserLauncher(c, browserStartCommand, session);
//			}
//		}
//		Matcher CustomMatcher = CUSTOM_PATTERN.matcher(browser.toString());
//		if (CustomMatcher.find()) {
//			String browserStartCommand = CustomMatcher.group(1);
//			if (browserStartCommand == null) {
//				throw new RuntimeException(
//						"You must specify the path to an executable when using *custom!\n\n");
//			}
//			browserStartCommand = browserStartCommand.substring(1);
//			return new DestroyableRuntimeExecutingBrowserLauncher(
//					getSeleniumConfiguration(), session, browserStartCommand);
//		}
//		throw browserNotSupported(browser.toString());
	}

	public static void addBrowserLauncher(Browser browser,
			Class<? extends BrowserLauncher> clazz) {
		supportedBrowsers.put(browser, clazz);
	}

	private RuntimeException browserNotSupported(Browser browser) {
		StringBuffer errorMessage = new StringBuffer("Browser not supported: "
				+ browser);
//		 @todo Redo browser not supported for browser launcher factory
//		errorMessage.append('\n');

//		if (!browser.startsWith("*")) {
//			errorMessage.append("(Did you forget to add a *?)\n");
//		}
//		errorMessage.append('\n');
//		errorMessage.append("Supported browsers include:\n");
//		for (Iterator<String> iterator = supportedBrowsers.keySet().iterator(); iterator
//				.hasNext();) {
//			String name = iterator.next();
//			errorMessage.append("  *").append(name).append('\n');
//		}
//		errorMessage.append("  *custom\n");
		return new RuntimeException(errorMessage.toString());
	}

	/**
	 * Create a browser launcher from the browser luancher class with the browser start command and
	 * the session it belongs to.
	 * 
	 * @param browserLauncherClass
	 *            Class of the browser launcher
	 * @param browserStartCommand
	 *            The browser start command
	 * @param session
	 *            The session
	 * @return Returns the created browser launcher.
	 */
	private BrowserLauncher createBrowserLauncher(
			Class<? extends BrowserLauncher> browserLauncherClass,
			String browserStartCommand) {
		try {
			try {
				BrowserLauncher browserLauncher;
				Constructor<? extends BrowserLauncher> constructor;
				
				if (null == browserStartCommand) {
					constructor = browserLauncherClass.getConstructor(
							SeleniumConfiguration.class);
					browserLauncher = constructor.newInstance(getSeleniumConfiguration());
				} else {
					constructor = browserLauncherClass.getConstructor(
							SeleniumConfiguration.class, String.class);
					browserLauncher = constructor.newInstance(getSeleniumConfiguration(),
							browserStartCommand);
				}

				return browserLauncher;
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
