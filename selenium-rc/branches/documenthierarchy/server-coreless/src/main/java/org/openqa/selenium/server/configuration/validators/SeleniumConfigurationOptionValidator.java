package org.openqa.selenium.server.configuration.validators;

import org.openqa.selenium.server.configuration.InvalidConfigurationException;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Validator to validate for a given configuration option.
 * 
 * @author Matthew Purland
 */
public interface SeleniumConfigurationOptionValidator {
	/**
	 * Validate for the configuration option.
	 * 
	 * @throws InvalidConfigurationException when a validation error occurs
	 */
	void validate(SeleniumConfiguration configuration) throws InvalidConfigurationException;
}
