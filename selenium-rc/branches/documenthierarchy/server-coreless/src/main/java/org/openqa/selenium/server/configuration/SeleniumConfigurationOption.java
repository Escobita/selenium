package org.openqa.selenium.server.configuration;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.server.configuration.validators.NoSeleniumConfigurationOptionValidator;
import org.openqa.selenium.server.configuration.validators.PortSeleniumConfigurationOptionValidator;
import org.openqa.selenium.server.configuration.validators.SeleniumConfigurationOptionValidator;

/**
 * Selenium configuration option type for handling options and their arguments.
 * 
 * @author Matthew Purland
 */
public enum SeleniumConfigurationOption {
	// @todo Add usage for arguments and options here

	// Configuration options.
	// 1. Array of representations of that option.
	// 2. Default value
	// 3. Validator

	// Jetty6
	JETTY6(new String[] { "jetty6" }, Boolean.valueOf(false),
	// @todo Add validator to validate options are not supported yet for jetty6
			new NoSeleniumConfigurationOptionValidator()),
	// Port
	PORT(new String[] { "port" }, Integer.valueOf(4444),
			new PortSeleniumConfigurationOptionValidator()),
	// Timeout in seconds
	TIMEOUT(new String[] { "timeout" }, Integer.valueOf(30 * 60 * 1000),
			new NoSeleniumConfigurationOptionValidator()),
	// Interactive
	INTERACTIVE(new String[] { "interactive" }, Boolean.valueOf(false),
			new NoSeleniumConfigurationOptionValidator()),
	// Multi window
	MULTI_WINDOW(new String[] { "multiWindow" }, Boolean.valueOf(false),
			new NoSeleniumConfigurationOptionValidator()),
	// Forced browser mode (not boolean)
	FORCED_BROWSER_MODE(new String[] { "forcedBrowserMode",
			"defaultBrowserString" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// User extensions
	USER_EXTENSIONS(new String[] { "userExtensions" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// Browser session reuse
	BROWSER_SESSION_REUSE(new String[] { "browserSessionReuse" }, Boolean
			.valueOf(false), new NoSeleniumConfigurationOptionValidator()),
	// Always proxy
	ALWAYS_PROXY(new String[] { "alwaysProxy" }, Boolean.valueOf(false),
			new NoSeleniumConfigurationOptionValidator()),
	// Firefox profile template
	FIREFOX_PROFILE_TEMPLATE(new String[] { "firefoxProfileTemplate" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// Debug mode
	DEBUG_MODE(new String[] { "debugMode", "debug" }, Boolean.valueOf(false),
			new NoSeleniumConfigurationOptionValidator()),

	// Debug URL
	DEBUG_URL(new String[] { "debugURL" }, null,
			new NoSeleniumConfigurationOptionValidator()),

	// Html suite
	HTML_SUITE(new String[] { "htmlSuite" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// Proxy injection mode
	PROXY_INJECTION_MODE(new String[] { "proxyInjectionMode" }, Boolean
			.valueOf(false), new NoSeleniumConfigurationOptionValidator()),
	// Dont inject regex
	DONT_INJECT_REGEX(new String[] { "dontInjectRegex" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// User js injection
	USER_JS_INJECTION(new String[] { "userJsInjection" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// User content transformation
	USER_CONTENT_TRANSFORMATION(new String[] { "userContentTransformation" },
			null, new NoSeleniumConfigurationOptionValidator()),
	// Log out file
	LOG_OUT_FILE(new String[] { "logOut" }, null,
			new NoSeleniumConfigurationOptionValidator()),
	// Slow resources mode
	SLOW_RESOURCES_MODE(new String[] { "slowMode" }, Boolean.valueOf(false),
			new NoSeleniumConfigurationOptionValidator()),
	// Force proxy chain mode
	FORCE_PROXY_CHAIN_MODE(new String[] { "forceProxyChainMode" }, Boolean
			.valueOf(false), new NoSeleniumConfigurationOptionValidator()),
	// Browser side log toggle
	BROWSER_SIDE_LOG(new String[] { "browserSideLog" }, Boolean
			.valueOf(false), new NoSeleniumConfigurationOptionValidator());
	
	
	// The option name
	private final String[] optionNames;

	// The default value
	private final Object defaultValue;

	// The validator
	private final SeleniumConfigurationOptionValidator validator;

	private static final String DASH = "-";

	<T> SeleniumConfigurationOption(String[] optionNames, Object defaultValue,
			SeleniumConfigurationOptionValidator validator) {
		this.optionNames = optionNames;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	/**
	 * Get the argument for the primary option.
	 * 
	 * @return Returns the argument for the primary option.
	 */
	public String getOptionArgument() {
		return DASH + getPrimaryOptionName();
	}

	/**
	 * Get the option names.
	 * 
	 * @return Returns the name for the option.
	 */
	public String[] getOptionNames() {
		return optionNames;
	}

	/**
	 * Get the primary option name.
	 * 
	 * @return Returns the primary option name.
	 */
	public String getPrimaryOptionName() {
		return optionNames[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		List<String> optionNamesList = new ArrayList<String>();
		String[] optionNames = getOptionNames();
		for (int i = 0; i < optionNames.length; i++) {
			optionNamesList.add(optionNames[i]);
		}

		return "Selenium Configuration Option (optionNames=" + optionNamesList
				+ ", argument=" + getOptionArgument() + ")";
	}

	/**
	 * Get a selenium configuration option by the option name.
	 * 
	 * @param optionName
	 *            The option name
	 * @return Returns a selenium configuration option by the given option name.
	 */
	public static SeleniumConfigurationOption getSeleniumConfigurationOption(
			String optionName) {
		SeleniumConfigurationOption[] values = values();

		for (SeleniumConfigurationOption option : values) {
			String[] optionNames = option.getOptionNames();

			for (String name : optionNames) {
				if (name.equals(optionName)) {
					return option;
				}
			}
		}

		return null;
	}

	/**
	 * Get the validator for the configuration option.
	 * 
	 * @return Returns the validator.
	 */
	public SeleniumConfigurationOptionValidator getValidator() {
		return validator;
	}

	/**
	 * Get the default value for the configuration option.
	 * 
	 * @return Returns the default value.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

}
