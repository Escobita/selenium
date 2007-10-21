package org.openqa.selenium.server.configuration;

/**
 * Place holder object for a configuration property that has no value.
 * 
 * @author Matthew Purland
 */
public class ConfigurationNoValue {

	// Not instantiable
	private ConfigurationNoValue() {
	}
	
	private static volatile ConfigurationNoValue instance;
	
	/**
	 * Get a single instance of the configuration no value.
	 */
	public static synchronized ConfigurationNoValue getInstance() {
		if (instance == null) {
			instance = new ConfigurationNoValue();
		}
		
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		
		if (obj instanceof ConfigurationNoValue) {
			// All instances are equal to each other
			isEqual = true;
		}
		else {
			isEqual = super.equals(obj);
		}
		
		return isEqual;
	}
	
}
