package org.openqa.selenium.server.configuration;

/**
 * A validatable configuration.  In order for a configuration to be taken
 * to the next step in program execution it must be valid.
 * 
 * @author Matthew Purland
 */
public interface ValidatableConfiguration {
	
	/**
	 * Validate the configuration.
	 * 
	 * @throws InvalidConfigurationException when there is a problem with the configuration
	 */
	void validate() throws InvalidConfigurationException;
}
