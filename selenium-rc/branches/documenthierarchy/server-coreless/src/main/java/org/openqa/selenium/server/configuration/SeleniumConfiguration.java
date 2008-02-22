package org.openqa.selenium.server.configuration;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.openqa.selenium.server.browser.BrowserType;

/**
 * Selenium configuration source for Selenium.
 * 
 * Will be useful when business logic using options and configuration can use the configuration
 * information without requiring state from other objects.
 * 
 * @see SeleniumConfigurationOption
 * 
 * @author Matthew Purland
 */
public interface SeleniumConfiguration extends ConfigurationSource {

	/**
	 * Get the underlying configuration.
	 * @return Returns the underlying configuration.
	 */
	Configuration getConfiguration();
	
	/**
	 * Set the option for the configuration with the given value.
	 * 
	 * @param option
	 *            The configuration option
	 * @param value
	 *            The value
	 */
	void setOption(SeleniumConfigurationOption option, Object value);

	/**
	 * Get the option from the configuration.
	 * 
	 * @param option
	 *            The configuration option
	 * @return Returns the value for the given configuration option.
	 */
	Object getOption(SeleniumConfigurationOption option);

	/**
	 * Get the option from the configuration as an int.
	 * 
	 * @param option
	 *            The option
	 * @return Returns the int for the option.
	 * 
	 * @throws ConversionException
	 * @throws NoSuchElementException
	 */
	int getOptionInt(SeleniumConfigurationOption option)
			throws ConversionException, NoSuchElementException;

	/**
	 * Get the option from the configuration as an int with a default value.
	 * 
	 * If the option is not found, it will return the default value.
	 * 
	 * @param option
	 *            The option
	 * @return Returns the int for the option.
	 * 
	 * @throws ConversionException
	 */
	int getOptionInt(SeleniumConfigurationOption option, int defaultValue)
			throws ConversionException;

	/**
	 * Get the option from the configuration as a String.
	 * 
	 * @param option
	 *            The option
	 * @return Returns the String for the option.
	 * 
	 * @throws ConversionException
	 * @throws NoSuchElementException
	 */
	String getOptionString(SeleniumConfigurationOption option)
			throws ConversionException, NoSuchElementException;

	/**
	 * Get the option from the configuration as a String with a default value.
	 * 
	 * If the option is not found, it will return the default value.
	 * 
	 * @param option
	 *            The option
	 * @return Returns the String for the option.
	 * 
	 * @throws ConversionException
	 */
	String getOptionString(SeleniumConfigurationOption option,
			String defaultValue) throws ConversionException;

	/**
	 * Get the port number that the selenium server is running on.
	 */
	int getPort();

	/**
	 * Set the port number that the selenium server is running on.
	 */
	void setPort(int port);

	/**
	 * Get timeout in number of seconds.
	 * 
	 * @see org.openqa.selenium.server.configuration.Configuration#setTimeout(int)
	 */
	int getTimeout();

	/**
	 * Set timeout in number of seconds.
	 * 
	 * @param timeoutInSeconds
	 *            Integer of timeout in seconds
	 * @see org.openqa.selenium.server.configuration.Configuration#setTimeout(int)
	 */
	void setTimeout(int timeoutInSeconds);

	/**
	 * Set interactive mode
	 */
	void setInteractiveMode(boolean interactiveMode);

	/**
	 * If interactive mode is enabled.
	 */
	boolean isInteractiveMode();

	/**
	 * Set multi window mode.
	 */
	void setMultiWindowMode(boolean multiWindowMode);

	/**
	 * Is multi window mode set.
	 */
	boolean isMultiWindowMode();

	/**
	 * Set forced browser mode.
	 * 
	 * @param The
	 *            forced browser to set. (e.g. *iexplore)
	 */
	void setForcedBrowserMode(BrowserType.Browser browser);

	/**
	 * Get forced browser mode string.
	 */
	String getForcedBrowserMode();	
	
	/**
	 * Set file for javascript user extensions.
	 */
	void setUserExtensions(String userExtensionsFile);

	/**
	 * Get file for javascript user extensions.
	 */
	String getUserExtensions();

	/**
	 * Set for browser session reuse.
	 */
	void setBrowserSessionReuse(boolean browserSessionReuse);

	/**
	 * Get whether to reuse browser sessions.
	 */
	boolean isBrowserSessionReuse();

	/**
	 * Set always proxy. By default, we proxy as little as we can.
	 */
	void setAlwaysProxy(boolean shouldAlwaysProxy);

	/**
	 * Get whether we always proxy or not.
	 * 
	 * @see org.openqa.selenium.server.configuration.Configuration#setAlwaysProxy(boolean)
	 */
	boolean isAlwaysProxy();

	/**
	 * Set custom location for firefox profile template.
	 */
	void setFirefoxProfileTemplate(String firefoxProfileTemplate);

	/**
	 * Get custom location for firefox profile template.
	 */
	String getFirefoxProfileTemplate();

	/**
	 * Is debug mode set.
	 */
	boolean isDebugMode();

	/**
	 * Set debug mode.
	 */
	void setDebugMode(boolean debugMode);

	/**
	 * Set Selense HTML suite to run and exit immediately.
	 * 
	 * @todo Add in configuration for htmlSuite
	 */
	// void setHtmlSuite(HtmlSuite suite);
	/**
	 * Get html suite for this configuration.
	 */
	// HtmlSuite getHtmlSuite();
	/**
	 * Set proxy injection mode.
	 * 
	 * @param isProxyInjectionMode
	 *            Provide true to set proxy injection mode
	 */
	void setProxyInjectionMode(boolean proxyInjectionMode);

	/**
	 * Is proxy injection mode set.
	 */
	boolean isProxyInjectionMode();

	/**
	 * Set regex pattern to not inject. Should only be used with proxy injection mode.
	 * 
	 * @param dontInjectRegex
	 *            Regex pattern to match pages you do not want to be injected with proxy injection
	 *            mode.
	 */
	void setDontInjectRegex(String dontInjectRegex);

	/**
	 * Get regex pattern for pages not to inject.
	 */
	String getDontInjectRegex();

	/**
	 * Specify a JavaScript file which will then be injected into all pages.
	 * 
	 * @param userJsInjectionFile
	 *            File to which you injected.
	 */
	void setUserJsInjection(String userJsInjectionFile);

	/**
	 * Get user javascript injection file.
	 */
	String getUserJsInjection();

	/**
	 * Specify regex pattern for content to be transformed and replaced.
	 * 
	 * @param regex
	 *            Regex pattern to match content needing to be replaced.
	 * @param replacement
	 *            Replacement for what matches regex pattern.
	 */
	void setUserContentTransformation(String regex, String replacement);

	/**
	 * Get user content transformation. First element of returned array is regex. Second element of
	 * returned array is replacement text.
	 * 
	 */
	String[] getUserContentTransformation();

	/**
	 * Set log to filename.
	 */
	void setLogOut(String filenameLogOut);

	/**
	 * Get filename for logOut
	 */
	String getLogOut();
	
	/**
	 * Set slow resources mode.
	 */
	void setSlowResourcesMode(boolean slowResourcesMode);

	/**
	 * Is serving slow resources?
	 */
	boolean isSlowResourcesMode();
	
	/**
	 * Set force proxy chain mode.
	 */
	void setForceProxyChainMode(boolean forceProxyChainMode);

	/**
	 * Is force chain proxy mode?
	 */
	boolean isForceProxyChainMode();
	
	/**
	 * Set the debug URL.
	 */
	void setDebugURL(String debugURL);
	
	/**
	 * Gets the debug URL.
	 * 
	 * @return Returns the debug URL.
	 */
	String getDebugURL();
	
	/**
	 * Set browser side log toggle.
	 */
	void setBrowserSideLogEnabled(boolean browserSideLog);

	/**
	 * Is browser side log enabled?
	 */
	boolean isBrowserSideLogEnabled();
	
	/**
	 * Set to ensure clean session.
	 */
	void setEnsureCleanSession(boolean ensureCleanSession);
	
	/**
	 * Is ensure clean session enabled?
	 */
	boolean isEnsureCleanSession();
	
	/**
	 * Set to avoid proxy.
	 */
	void setAvoidProxy(boolean avoidProxy);
	
	/**
	 * Is avoiding proxy?
	 */
	boolean isAvoidProxy();
	
	/**
	 * Set killable process enabled.
	 */
	void setKillableProcessEnabled(boolean killableProcessEnabled);
	
	/**
	 * Is killable process enabled?
	 */
	boolean isKillableProcessEnabled();
	
	/**
	 * Set the hostname for the server.
	 */
	void setHostname(String hostname);
	
	/**
	 * Get the hostname for the server.
	 */
	String getHostname();

	// @todo Check configuration to ensure all options are accessible through selenium.option

	// @todo Add in configuration for selenium.alwaysProxy
	// @todo Add in configuration for selenium.forcedBrowserMode
	// @todo Add in not supported config for sleenium.defaultBrowserString
	// @todo Add in configuration for selenium.log.fileName for properties/env
	// @todo Add in configuration for selenium.javascript.dir for proeprties/env
	// @todo Add in configuration for selenium.slowMode/selenium.slowResources for
	// properties/environment
	// @todo Add in SeleniumConfiguration for debugURL
	// @todo Add in SeleniumConfiguration for jettyThreads
	// @todo Add in configuration for selenium.log for properties/environment
}
