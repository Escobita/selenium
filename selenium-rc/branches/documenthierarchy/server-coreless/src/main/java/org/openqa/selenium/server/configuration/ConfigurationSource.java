package org.openqa.selenium.server.configuration;

import java.util.Iterator;

import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeListener;

/**
 * Configurable source for a listenable configuration for options.
 * 
 * @author Matthew Purland
 */
public interface ConfigurationSource {
	/**
	 * Determine whether an option was specified.
	 * 
	 * @param option
	 *            Selenium configuration option to check.
	 * @return Returns true if the option was given.
	 */
	boolean isOptionSpecified(SeleniumConfigurationOption option);
	
	/**
	 * Determine whether an option was specified.
	 * 
	 * @param optionName
	 *            The option name to check.
	 * @return Returns true if the option was given.
	 */
	boolean isOptionSpecified(String optionName);
	
	boolean isOptionValueSpecified(SeleniumConfigurationOption option);
	
	boolean isOptionValueSpecified(String optionName);
	
	/**
	 * Get the property iterator.
	 */
	Iterator getPropertyIterator();
	
	/**
	 * Add a configuration listener to the configuration source for specified option.
	 * 
	 * @param option The option to add a listener for
	 * @param listener The configuration listener to notify
	 */
	void addConfigurationListener(SeleniumConfigurationOption option, PropertyConfigurationChangeListener listener);

	/**
	 * Remove a configuration listener from the configuration source for the specified option.
	 * 
	 * @param optionName The option to remove a listener from
	 * @param listener The configuration listener to remove
	 */
	void removeConfigurationListener(SeleniumConfigurationOption option, PropertyConfigurationChangeListener listener);
	
	/**
	 * Determine if the configuration is read only. Once set read only it cannot be modified.
	 * 
	 * @return Returns true if the configuration is read only.
	 */
	boolean isReadOnly();
}
