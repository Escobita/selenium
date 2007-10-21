package org.openqa.selenium.server.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.MapConfiguration;

/**
 * Configuration based around configuration that will be passed to an
 * application through the main(String[]) method.
 * 
 * @author Matthew Purland
 */
public class ArgumentsConfiguration extends MapConfiguration {

	public ArgumentsConfiguration(String[] arguments) {
		super(getMapArguments(arguments));
	}

	/**
	 * Import arguments into internal map.
	 * 
	 * @param args The arguments as a string array
	 */
	protected static Map<String, Object> getMapArguments(String[] args) {
		// Was List<String> instead of Object
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<String> argValues = new ArrayList<String>();

		if (args != null && args.length > 0) {
			// If empty then last in args was a value, otherwise argument
			String argument = "";

			for (int i = 0; i < args.length; i++) {
				String arg = args[i];

				// Argument declaration such as -debugMode
				// Is an argument
				if (arg.startsWith("-")) {
					if (!argument.equals("") && !argValues.isEmpty()) {
						tempMap.put(argument, argValues);
						// Effectively clear the current list with a new one
						argValues = new ArrayList<String>();
					}

					argument = arg.substring(1, arg.length());
				}
				// Is an argument value
				else {
					// @todo Add a test case for an argument with whitespace such as "-port  "
					// Only add a non white space value
					if (arg.trim().length() > 0) {
						// Add argument value to list
						argValues.add(arg);
					}
				}
				
				// Ensure our argument is something and isn't empty space
				if (argument != null && argument.length() > 0) {
					// We must insert a null for the list otherwise we cannot use "getProperty" correctly
					// @todo Add a test case for this as opposed to returning an empty list
					if (argValues.isEmpty()) {
						tempMap.put(argument, Boolean.TRUE);
					}
					else {
						tempMap.put(argument, argValues);
					}
				}
			}
		}


		return tempMap;
	}
}
