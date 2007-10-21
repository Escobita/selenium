package org.openqa.selenium.server.configuration.validators;

import org.apache.commons.configuration.ConversionException;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.configuration.InvalidConfigurationException;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;

/**
 * Validator to validate the port configuration option.
 * 
 * @todo i18n and message bundles for messages
 * 
 * @author Matthew Purland
 */
public class PortSeleniumConfigurationOptionValidator implements
		SeleniumConfigurationOptionValidator {
	private static Logger logger = Logger.getLogger(PortSeleniumConfigurationOptionValidator.class);
	
	/**
	 * {@inheritDoc}
	 */
	public void validate(SeleniumConfiguration configuration)
			throws InvalidConfigurationException {
		if (configuration
				.isOptionSpecified(SeleniumConfigurationOption.PORT)) {
			// A value for port was given
			if (configuration
					.isOptionValueSpecified(SeleniumConfigurationOption.PORT)) {
				try {
					int port = configuration.getPort();

					if (port < 0) {
						throw new InvalidConfigurationException(
								"You must specify a port greater than 0.");
					}
				} 
				// The port must be a number
				catch (ConversionException ex) {
					throw new InvalidConfigurationException(
							"You must specify a number for a port.");
				}
			}
			// A value for port was not given
			else {
				throw new InvalidConfigurationException(
						"You must specify a number for a port.");
			}
		}
		else {
			configuration.setOption(SeleniumConfigurationOption.PORT, SeleniumConfigurationOption.PORT.getDefaultValue());
			logger.info("Port was not specified.  Using default value " + SeleniumConfigurationOption.PORT.getDefaultValue());
		}
	}

}
