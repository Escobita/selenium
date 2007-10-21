package org.openqa.selenium.server.locator;

/**
 * Locator interface for representing element locators.  Based on a target.
 * 
 * @author Matthew Purland
 */
public interface Locator<T, S> {
	/**
	 * Locate a type by the given argument.
	 * 
	 * @param target The target
	 * @param argument The argument
	 * 
	 * @return Returns the located type; null if not found.
	 */
	S locate(T target, String argument);
}
