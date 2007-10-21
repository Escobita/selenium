package org.openqa.selenium.server.configuration.validators;

import org.openqa.selenium.server.configuration.InvalidConfigurationException;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Validator that does nothing.  Place holder for a null object.
 * 
 * @author Matthew Purland
 */
public class NoSeleniumConfigurationOptionValidator implements
		SeleniumConfigurationOptionValidator {

	/**
	 * {@inheritDoc}
	 */
	public void validate(SeleniumConfiguration configuration)
			throws InvalidConfigurationException {
		// Does nothing...
	}

}
