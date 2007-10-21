package org.openqa.selenium.server.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import junit.framework.TestCase;

/**
 * Test case to test the arguments configuration.
 * 
 * @author Matthew Purland
 */
public class ArgumentsConfigurationTest extends TestCase {

	private static final String TEST_ARGUMENT = "-testArgument";

	private static final String TEST_ARGUMENT_WITHOUT_DASH = "testArgument";

	/**
	 * Test that no arguments work and return an empty configuration.
	 */
	public void testEmptyArguments() {
		String[] args = new String[0];

		int keyCount = 0;

		Configuration argumentsConfiguration = new ArgumentsConfiguration(args);
		Iterator argumentsConfigurationKeyIterator = argumentsConfiguration
				.getKeys();

		// Assert that the key count for the arguments configuration is 0
		while (argumentsConfigurationKeyIterator.hasNext()) {
			argumentsConfigurationKeyIterator.next();
			keyCount++;
		}

		assertEquals("The size of an empty arguments configuration is not 0.",
				0, keyCount);
	}

	/**
	 * Test a single positive argument that should allow the arguments configuration to work
	 * correctly.
	 */
	public void testSinglePositiveArgument() {
		String expectedValue = "test";
		String argument = TEST_ARGUMENT;
		String argumentWithoutDash = TEST_ARGUMENT_WITHOUT_DASH;

		String[] args = new String[] { argument, expectedValue };

		Configuration argumentsConfiguration = new ArgumentsConfiguration(args);

		List<String> expectedValueList = new ArrayList<String>();
		expectedValueList.add(expectedValue);

		assertEquals(
				"Single argument passed to arguments configuration was not equal to the expected value.",
				expectedValueList, argumentsConfiguration
						.getProperty(argumentWithoutDash));
	}

	/**
	 * Test single positive argument without a value. This should test a toggle argument/flag.
	 */
	public void testSinglePositiveArgumentWithoutValue() {
		String argument = TEST_ARGUMENT;
		String argumentWithoutDash = TEST_ARGUMENT_WITHOUT_DASH;

		String[] args = new String[] { argument };

		Configuration argumentsConfiguration = new ArgumentsConfiguration(args);

		Object argumentValue = argumentsConfiguration
				.getProperty(argumentWithoutDash);

		boolean argumentWasSpecified = argumentsConfiguration.containsKey(argumentWithoutDash);
		
		assertTrue("Argument is not a key to arguments configuration.", argumentWasSpecified);
		
		assertEquals(
				"Single argument without value passed to arguments configuration was not in arguments configuration correctly.",
				Boolean.TRUE, argumentValue);
	}

	/**
	 * Test multiple positive arguments.
	 */
	public void testMultiplePositiveArguments() {
		Map<String, String> argumentToValueMap = new HashMap<String, String>();
		final int numberOfArguments = 10;

		for (int i = 0; i < numberOfArguments; i++) {
			argumentToValueMap.put("-testArgument" + i, "test" + i);
		}

		Set<String> argumentToValueMapKeySet = argumentToValueMap.keySet();
		Iterator<String> argumentToValueMapKeySetIterator = argumentToValueMapKeySet
				.iterator();
		List<String> argsList = new ArrayList<String>();

		// Create arguments list for arguments configuration
		while (argumentToValueMapKeySetIterator.hasNext()) {
			String argument = argumentToValueMapKeySetIterator.next();
			String argumentValue = argumentToValueMap.get(argument);
			argsList.add(argument);
			argsList.add(argumentValue);
		}
		Configuration argumentsConfiguration = new ArgumentsConfiguration(
				argsList.toArray(new String[0]));

		// Assert configuration for each argument without -
		while (argumentToValueMapKeySetIterator.hasNext()) {
			String argument = argumentToValueMapKeySetIterator.next();
			String argumentValue = argumentToValueMap.get(argument);

			String argumentWithoutDash = argument.substring(0, argument
					.length());
			List<String> expectedValueList = new ArrayList<String>();
			expectedValueList.add(argumentValue);

			// Assert each argument
			assertEquals("Multiple argument pass for argument (argument="
					+ argumentWithoutDash + ") was not in configuration.",
					expectedValueList, argumentsConfiguration
							.getProperty(argumentWithoutDash));
		}
	}

	/**
	 * Test multiple positive arguments with a negative argument.
	 */
	public void testMultiplePositiveArgumentsWithNegativeArgument() {
		// Test that "something" is not ignored
		String[] args = new String[] { "-testArgument1", "test1", "something",
				"-testArgument2", "test2" };

		Configuration argumentsConfiguration = new ArgumentsConfiguration(args);

		List<String> firstExpectedValueList = new ArrayList<String>();
		firstExpectedValueList.add("test1");
		firstExpectedValueList.add("something");

		// Assert first argument
		assertEquals("First argument is not in configuration correctly.",
				firstExpectedValueList, argumentsConfiguration
						.getProperty("testArgument1"));

		List<String> secondExpectedValueList = new ArrayList<String>();
		secondExpectedValueList.add("test2");

		// Assert second argument
		assertEquals("Second argument is not in configuration correctly.",
				secondExpectedValueList, argumentsConfiguration
						.getProperty("testArgument2"));
	}
}
