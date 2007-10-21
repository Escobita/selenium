package org.openqa.selenium.server.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.configuration.validators.SeleniumConfigurationOptionValidator;

/**
 * A validatable configuration for Selenium. This will ensure the given arguments to the
 * {@link SeleniumServer} are correct before being passed to other parts in the execution.
 * 
 * @author Matthew Purland
 */
public class SeleniumValidatableConfiguration implements
		ValidatableConfiguration {

	private SeleniumConfiguration seleniumConfiguration;

	private static final String ARGUMENT_DASH = "-";

	/**
	 * construct a new selenium validatable configuration.
	 * 
	 * @param seleniumConfiguration
	 *            The selenium configuration to validate.
	 */
	public SeleniumValidatableConfiguration(
			SeleniumConfiguration seleniumConfiguration) {
		this.seleniumConfiguration = seleniumConfiguration;
	}

//	/**
//	 * Get argument representation for the given option name.
//	 * 
//	 * @param optionName
//	 *            The option name
//	 * @return Returns the argument for the option name.
//	 */
//	private static String getArgument(String optionName) {
//		return ARGUMENT_DASH + optionName;
//	}

	// /**
	// * Get a boolean property from the configuration.
	// *
	// * @param property
	// * The property name
	// * @return Returns true if the boolean property was specified; false otherwise.
	// */
	// public boolean getBooleanProperty(String property) {
	// boolean returnResult = false;
	//
	// String singlePropertyString = getSingleProperty(property);
	// if (singlePropertyString != null && singlePropertyString.equals("")) {
	// returnResult = true;
	// }
	//
	// return returnResult;
	// }
	//
	// /**
	// * Get a single property from the configuration. Will return a single argument if it was a
	// * boolean argument as "".
	// *
	// * @param property
	// * The property name
	// * @return Returns the single string property if it was specified; false otherwise.
	// */
	// public String getSingleProperty(String property) {
	// List list = (List) configuration.getProperty(property);
	//
	// String value = "";
	//
	// if (list == null) {
	// value = null;
	// } else if (list.size() > 0) {
	// value = (String) list.get(0);
	// }
	//
	// return value;
	// }

	/**
	 * {@inheritDoc}
	 */
	public void validate() throws InvalidConfigurationException {
		// @todo Need to redo implementation to favor SeleniumConfiguration layer
		// boolean defaultBrowserString = getBooleanProperty(DEFAULT_BROWSER_STRING);
		// boolean interactive = getBooleanProperty(INTERACTIVE);
		// String htmlSuite = getSingleProperty(HTML_SUITE);
		// String port = getSingleProperty("port");

		SeleniumConfigurationOption[] seleniumConfigurationOptionValues = SeleniumConfigurationOption
				.values();

		List<SeleniumConfigurationOption> seleniumConfigurationOptionList = new ArrayList<SeleniumConfigurationOption>();

		// Add values to list for using easy contains method in next loop
		Collections.addAll(seleniumConfigurationOptionList, seleniumConfigurationOptionValues);

		Iterator configurationPropertyIterator = seleniumConfiguration
				.getPropertyIterator();

		// Go through configuration keys and ensure that the only arguments we
		// accept
		// are the ones in our map
		while (configurationPropertyIterator.hasNext()) {
			Object configurationProperty = configurationPropertyIterator.next();
			String configurationPropertyString = (String) configurationProperty;
			
			SeleniumConfigurationOption option = SeleniumConfigurationOption
					.getSeleniumConfigurationOption(configurationProperty
							.toString());
			
			if ((option == null
					|| !seleniumConfigurationOptionList.contains(option)) /*&& !configurationPropertyString.startsWith("java.")*/) {
				throw new InvalidConfigurationException(
						"Unrecognized configuration property \"" + configurationProperty
								+ "\"");
			}
		}

		// Now validate for each configuration option
		// This allows to keep configuration out of a central place...less mess. More easily
		// testable.
		for (SeleniumConfigurationOption option : seleniumConfigurationOptionValues) {
			SeleniumConfigurationOptionValidator validator = option
					.getValidator();

			validator.validate(seleniumConfiguration);
		}

		// if (seleniumConfiguration.isOptionSpecified(option)) {
		// throw new InvalidConfigurationException(
		// "-defaultBrowserString has been renamed to -forcedBrowserMode");
		// }
		//
		// if (interactive && htmlSuite != null) {
		// throw new InvalidConfigurationException(
		// "You can't use -interactive and -htmlSuite on the same line!");
		// }

	}
}
