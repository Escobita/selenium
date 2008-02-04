package org.openqa.selenium.server.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.BrowserType.Browser;

/**
 * Default implementation for {@link SeleniumConfiguration}. Accesses internal map to set and get
 * internal attributes.
 * 
 * @todo Need to fix configuration for specified configuration and not specified configurations.
 * 
 * @author Matthew Purland
 */
public class DefaultSeleniumConfiguration extends AbstractConfigurationSource
		implements SeleniumConfiguration {

	private static Logger logger = Logger.getLogger(DefaultSeleniumConfiguration.class);
	
	public DefaultSeleniumConfiguration() {
		super();
		
		setDefaultValues(this);
	}

	public DefaultSeleniumConfiguration(Configuration configuration,
			SeleniumConfigurationListener seleniumConfigurationListener) {
		super(getModifiedConfiguration(configuration),
				seleniumConfigurationListener);

		setDefaultValues(getConfiguration());
	}

	/**
	 * Get a modified configuration to put different options under the same primary option name.
	 */
	protected static Configuration getModifiedConfiguration(
			Configuration configuration) {
		Map<String, String> mapConfiguration = new HashMap<String, String>();
		Configuration newConfiguration = new MapConfiguration(mapConfiguration);

		SeleniumConfigurationOption[] seleniumConfigurationOptionValues = SeleniumConfigurationOption
				.values();
		
		Iterator configurationKeyIterator = configuration.getKeys();

		while (configurationKeyIterator.hasNext()) {
			Object configurationKey = configurationKeyIterator.next();
			
			if (configurationKey instanceof String) {
				String configurationOptionName = (String) configurationKey;
				boolean configurationOptionFound = false;
				
				// Go through each option
				for (SeleniumConfigurationOption option : seleniumConfigurationOptionValues) {
					if (!configurationOptionFound) {
						for (String optionName : option.getOptionNames()) {
							if (optionName.equals(configurationOptionName)) {
								configurationOptionFound = true;
								newConfiguration.addProperty(option.getPrimaryOptionName(), configuration.getProperty(optionName));
								break;
							}
						}
					}
				}
				
				// If we didn't find it...add it anyway
				if (!configurationOptionFound) {
					newConfiguration.addProperty(configurationOptionName, configuration.getProperty(configurationOptionName));	
				}
			}
		}
		
//		// Go through each option
//		for (SeleniumConfigurationOption option : seleniumConfigurationOptionValues) {
//			for (String optionName : option.getOptionNames()) {
//				// If property exists in old configuration and its an option
//				// Add under primary option name
//				if (configuration.containsKey(optionName)) {
//					newConfiguration.addProperty(option.getPrimaryOptionName(),
//							configuration.getProperty(optionName));
//				}
//			}
//		}

		return newConfiguration;
	}

	// @todo Set default values when SeleniumConfiguration is constructed. What about isOption...?
	public void setDefaultValues(Configuration configuration) {
		for (SeleniumConfigurationOption option : SeleniumConfigurationOption
				.values()) {
			String optionName = option.getPrimaryOptionName();

			// Don't set default values for already defined options
			if (!configuration.containsKey(optionName)) {
				Object defaultValue = option.getDefaultValue();

				// Only set non null default values
				if (defaultValue != null) {
					this.setOption(option, option.getDefaultValue());
				}
			}
			else {
				logger.info("Option " + option + " was specified with value \"" + configuration.getProperty(optionName) + "\"");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getOption(SeleniumConfigurationOption option) {
		return getProperty(option.getPrimaryOptionName());
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOptionInt(SeleniumConfigurationOption option)
			throws ConversionException, NoSuchElementException {
		return getInt(option.getPrimaryOptionName());
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOptionInt(SeleniumConfigurationOption option, int defaultValue)
			throws ConversionException {
		return getInt(option.getPrimaryOptionName(), defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOptionString(SeleniumConfigurationOption option)
			throws ConversionException, NoSuchElementException {
		return getString(option.getPrimaryOptionName());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOptionString(SeleniumConfigurationOption option,
			String defaultValue) throws ConversionException {
		return getString(option.getPrimaryOptionName(), defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOption(SeleniumConfigurationOption option, Object value) {
		setProperty(option.getPrimaryOptionName(), value);
	}

	public Configuration getConfiguration() {
		return getInMemoryConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPort(int port) {
		setProperty(SeleniumConfigurationOption.PORT.getPrimaryOptionName(),
				Integer.valueOf(port));
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPort() {
		int port = getInt(SeleniumConfigurationOption.PORT
				.getPrimaryOptionName());
		return port;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDontInjectRegex(String dontInjectRegex) {
		setProperty(SeleniumConfigurationOption.DONT_INJECT_REGEX
				.getPrimaryOptionName(), dontInjectRegex);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDontInjectRegex() {
		String dontInjectRegex = getString(SeleniumConfigurationOption.DONT_INJECT_REGEX
				.getPrimaryOptionName());
		return dontInjectRegex;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(int timeoutInSeconds) {
		setProperty(SeleniumConfigurationOption.TIMEOUT.getPrimaryOptionName(),
				Integer.valueOf(timeoutInSeconds));
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		int timeoutInSeconds = getInt(SeleniumConfigurationOption.TIMEOUT
				.getPrimaryOptionName());
		return timeoutInSeconds;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDebugMode(boolean debugMode) {
		setProperty(SeleniumConfigurationOption.DEBUG_MODE
				.getPrimaryOptionName(), Boolean.valueOf(debugMode));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebugMode() {
		boolean debugMode = getBoolean(SeleniumConfigurationOption.DEBUG_MODE
				.getPrimaryOptionName());
		return debugMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFirefoxProfileTemplate(String firefoxProfileTemplate) {
		setProperty(SeleniumConfigurationOption.FIREFOX_PROFILE_TEMPLATE
				.getPrimaryOptionName(), firefoxProfileTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFirefoxProfileTemplate() {
		String firefoxProfileTemplate = getString(SeleniumConfigurationOption.FIREFOX_PROFILE_TEMPLATE
				.getPrimaryOptionName());
		return firefoxProfileTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setForcedBrowserMode(Browser browser) {
		setProperty(SeleniumConfigurationOption.FORCED_BROWSER_MODE
				.getPrimaryOptionName(), browser);
	}

	/**
	 * {@inheritDoc}
	 */
	public Browser getForcedBrowserMode() {
		Browser browser = (Browser) getProperty(SeleniumConfigurationOption.FORCED_BROWSER_MODE
				.getPrimaryOptionName());
		return browser;
	}

	public void setLogOut(String filenameLogOut) {
		setProperty(SeleniumConfigurationOption.LOG_OUT_FILE
				.getPrimaryOptionName(), filenameLogOut);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLogOut() {
		String logOut = getString(SeleniumConfigurationOption.LOG_OUT_FILE
				.getPrimaryOptionName());
		return logOut;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUserContentTransformation(String regex, String replacement) {
		setProperty(SeleniumConfigurationOption.USER_CONTENT_TRANSFORMATION
				.getPrimaryOptionName(), new String[] { regex, replacement });
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getUserContentTransformation() {
		String[] regexAndReplacement = (String[]) getProperty(SeleniumConfigurationOption.USER_CONTENT_TRANSFORMATION
				.getPrimaryOptionName());
		return regexAndReplacement;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUserExtensions(String userExtensionsFile) {
		setProperty(SeleniumConfigurationOption.USER_EXTENSIONS
				.getPrimaryOptionName(), userExtensionsFile);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserExtensions() {
		String userExtensions = getString(SeleniumConfigurationOption.USER_EXTENSIONS
				.getPrimaryOptionName());
		return userExtensions;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUserJsInjection(String userJsInjectionFile) {
		setProperty(SeleniumConfigurationOption.USER_JS_INJECTION
				.getPrimaryOptionName(), userJsInjectionFile);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserJsInjection() {
		String userJsInjection = getString(SeleniumConfigurationOption.USER_JS_INJECTION
				.getPrimaryOptionName());

		return userJsInjection;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAlwaysProxy(boolean shouldAlwaysProxy) {
		setProperty(SeleniumConfigurationOption.ALWAYS_PROXY
				.getPrimaryOptionName(), Boolean.valueOf(shouldAlwaysProxy));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAlwaysProxy() {
		boolean isAlwaysProxy = getBoolean(SeleniumConfigurationOption.ALWAYS_PROXY
				.getPrimaryOptionName());

		return isAlwaysProxy;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBrowserSessionReuse(boolean browserSessionReuse) {
		setProperty(SeleniumConfigurationOption.BROWSER_SESSION_REUSE
				.getPrimaryOptionName(), Boolean.valueOf(browserSessionReuse));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBrowserSessionReuse() {
		boolean isBrowserSessionReuse = getBoolean(SeleniumConfigurationOption.BROWSER_SESSION_REUSE
				.getPrimaryOptionName());

		return isBrowserSessionReuse;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInteractiveMode(boolean interactiveMode) {
		setProperty(SeleniumConfigurationOption.INTERACTIVE
				.getPrimaryOptionName(), Boolean.valueOf(interactiveMode));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInteractiveMode() {
		boolean isInteractiveMode = getBoolean(SeleniumConfigurationOption.INTERACTIVE
				.getPrimaryOptionName());

		return isInteractiveMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMultiWindowMode(boolean multiWindowMode) {
		setProperty(SeleniumConfigurationOption.MULTI_WINDOW
				.getPrimaryOptionName(), Boolean.valueOf(multiWindowMode));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMultiWindowMode() {
		boolean isMultiWindowMode = getBoolean(SeleniumConfigurationOption.MULTI_WINDOW
				.getPrimaryOptionName());

		return isMultiWindowMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProxyInjectionMode(boolean proxyInjectionMode) {
		setProperty(SeleniumConfigurationOption.PROXY_INJECTION_MODE
				.getPrimaryOptionName(), Boolean.valueOf(proxyInjectionMode));

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProxyInjectionMode() {
		boolean isProxyInjectionMode = getBoolean(SeleniumConfigurationOption.PROXY_INJECTION_MODE
				.getPrimaryOptionName());

		return isProxyInjectionMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSlowResourcesMode() {
		boolean isSlowResourcesMode = getBoolean(SeleniumConfigurationOption.SLOW_RESOURCES_MODE
				.getPrimaryOptionName());

		return isSlowResourcesMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSlowResourcesMode(boolean slowResourcesMode) {
		setProperty(SeleniumConfigurationOption.SLOW_RESOURCES_MODE
				.getPrimaryOptionName(), Boolean.valueOf(slowResourcesMode));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isForceProxyChainMode() {
		boolean isForceProxyChainMode = getBoolean(SeleniumConfigurationOption.FORCE_PROXY_CHAIN_MODE
				.getPrimaryOptionName());

		return isForceProxyChainMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setForceProxyChainMode(boolean forceProxyChainMode) {
		setProperty(SeleniumConfigurationOption.FORCE_PROXY_CHAIN_MODE
				.getPrimaryOptionName(), Boolean.valueOf(forceProxyChainMode));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDebugURL() {
		String debugURL = getString(SeleniumConfigurationOption.DEBUG_URL
				.getPrimaryOptionName());

		return debugURL;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDebugURL(String debugURL) {
		setProperty(SeleniumConfigurationOption.DEBUG_URL
				.getPrimaryOptionName(), debugURL);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBrowserSideLogEnabled() {
		boolean isBrowserSideLogEnabled = getBoolean(SeleniumConfigurationOption.BROWSER_SIDE_LOG
				.getPrimaryOptionName());

		return isBrowserSideLogEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBrowserSideLogEnabled(boolean browserSideLog) {
		setProperty(SeleniumConfigurationOption.BROWSER_SIDE_LOG
				.getPrimaryOptionName(), Boolean.valueOf(browserSideLog));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEnsureCleanSession() {
		boolean isEnsureCleanSession = getBoolean(SeleniumConfigurationOption.ENSURE_CLEAN_SESSION
				.getPrimaryOptionName());

		return isEnsureCleanSession;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEnsureCleanSession(boolean ensureCleanSession) {
		setProperty(SeleniumConfigurationOption.ENSURE_CLEAN_SESSION
				.getPrimaryOptionName(), Boolean.valueOf(ensureCleanSession));
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAvoidProxy() {
		boolean isAvoidProxy = getBoolean(SeleniumConfigurationOption.AVOID_PROXY
				.getPrimaryOptionName());

		return isAvoidProxy;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAvoidProxy(boolean avoidProxy) {
		setProperty(SeleniumConfigurationOption.AVOID_PROXY
				.getPrimaryOptionName(), Boolean.valueOf(avoidProxy));
	}

}
