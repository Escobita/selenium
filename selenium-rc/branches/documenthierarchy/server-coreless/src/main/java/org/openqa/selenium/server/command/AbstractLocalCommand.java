package org.openqa.selenium.server.command;

import java.util.Map;

/**
 * Abstract implementation for a {@link LocalCommand}.
 *   
 * @author Matthew Purland
 *
 * @param <T>
 */
public abstract class AbstractLocalCommand<T extends CommandResult> extends
		AbstractCommand<T> implements LocalCommand<T> {

	/**
	 * Construct a new local command from the given command and list of command parameters as
	 * values.
	 * 
	 * @param command
	 *            The command
	 * @param commandParameterMap
	 *            The map of command parameters
	 */
	public AbstractLocalCommand(String command, Map<String, String> commandParameterMap) {
		super(command, commandParameterMap);
	}

	/**
	 * Construct a new local command from the given command.
	 * 
	 * @param command
	 *            The command
	 */
	public AbstractLocalCommand(String command) {
		super(command);
	}
}
