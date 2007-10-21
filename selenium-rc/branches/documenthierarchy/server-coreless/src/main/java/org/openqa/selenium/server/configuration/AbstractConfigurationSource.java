package org.openqa.selenium.server.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeListener;

/**
 * Abstract implementation for {@link ConfigurationSource}.
 * 
 * @author Matthew Purland
 */
public class AbstractConfigurationSource extends CompositeConfiguration
		implements ConfigurationSource {

	// If the configuration is read only
	private boolean readOnly;

	// Master listener for all configuration events
	private SeleniumConfigurationListener seleniumConfigurationListener;

	public AbstractConfigurationSource() {
		super();
		Map<String, String> mapConfiguration = new HashMap<String, String>();
		Configuration configuration = new MapConfiguration(mapConfiguration);
		
		SeleniumConfigurationListener seleniumConfigurationListener = new SeleniumConfigurationListener(configuration);
		
		super.addConfiguration(configuration);
		this.seleniumConfigurationListener = seleniumConfigurationListener;
		this.addConfigurationListener(seleniumConfigurationListener);
	}
	
	public AbstractConfigurationSource(Configuration configuration,
			SeleniumConfigurationListener seleniumConfigurationListener) {
		super(configuration);

		this.seleniumConfigurationListener = seleniumConfigurationListener;
		this.addConfigurationListener(seleniumConfigurationListener);
	}

	public AbstractConfigurationSource(Configuration configuration,
			SeleniumConfigurationListener seleniumConfigurationListener,
			boolean readOnly) {
		this(configuration, seleniumConfigurationListener);
		this.readOnly = readOnly;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConfigurationListener(SeleniumConfigurationOption option,
			PropertyConfigurationChangeListener listener)
			throws IllegalArgumentException {
		String optionName = option.getPrimaryOptionName();
		
		if (!isOptionSpecified(optionName)) {
			throw new IllegalArgumentException(
					"Cannot add a configuration listener for inexistent option "
							+ optionName);
		}

		seleniumConfigurationListener.addPropertyListener(optionName, listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeConfigurationListener(SeleniumConfigurationOption option,
			PropertyConfigurationChangeListener listener)
			throws IllegalArgumentException {
		String optionName = option.getPrimaryOptionName();
		
		if (!isOptionSpecified(optionName)) {
			throw new IllegalArgumentException(
					"Cannot remove a configuration listener for inexistent option "
							+ optionName);
		}

		seleniumConfigurationListener.removePropertyListener(optionName,
				listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptionSpecified(SeleniumConfigurationOption option) {
		String[] optionNames = option.getOptionNames();
		boolean optionWasSpecified = false;

		for (String optionName : optionNames) {
			if (containsKey(optionName)) {
				optionWasSpecified = true;
			}
		}

		return optionWasSpecified;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptionSpecified(String optionName) {
		return containsKey(optionName);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return readOnly;
	}



	/**
	 * {@inheritDoc}
	 */
	public Iterator getPropertyIterator() {
		return getKeys();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptionValueSpecified(SeleniumConfigurationOption option) {
		boolean optionValueWasSpecified = false;
		boolean optionWasSpecified = isOptionSpecified(option);
		if (optionWasSpecified) {
			String[] optionNames = option.getOptionNames();
			for (String optionName : optionNames) {
				if (isOptionSpecified(optionName)) {
					if (isOptionValueSpecified(optionName)) {
						optionValueWasSpecified = true;
						break;
					}
				}
			}
		}

		return optionWasSpecified && optionValueWasSpecified;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptionValueSpecified(String optionName) {
		boolean optionValueWasSpecified = false;
		boolean optionWasSpecified = isOptionSpecified(optionName);
		
		Object object = getProperty(optionName);

		if (object != null) {
			optionValueWasSpecified = true;
		}

		return optionWasSpecified && optionValueWasSpecified;
	}
}
