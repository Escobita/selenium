package org.openqa.selenium.server.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.server.client.Session;

/**
 * Abstract implementation for a {@link Command}.
 * 
 * @author Matthew Purland
 */
public abstract class AbstractCommand<T extends CommandResult> implements
		Command<T> {
	private String command = "";

	private Map<String, String> commandParameterMap;

	private boolean hasExecuted = false;

	private T commandResult = null;
	
	/**
	 * Constructs a command object with a command.
	 * 
	 * @param command
	 *            The string representation of the command
	 * @throws CommandValidationException
	 *             when command parameters are incorrect
	 */
	public AbstractCommand(String command) throws CommandValidationException {
		this(command, new HashMap<String, String>());
	}

	/**
	 * Constructs a command object with a command and map of parameters.
	 * 
	 * @param command
	 *            The string representation of the command
	 * @param commandParameterMap
	 *            The map of command parameter names to parameter values
	 * 
	 * @throws CommandValidationException
	 *             when command parameters are incorrect
	 */
	public AbstractCommand(String command, Map<String, String> commandParameterMap)
			throws CommandValidationException {
		this.command = command;
		this.commandParameterMap = commandParameterMap;
		//validateAndSetParameters();
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract void validateAndSetParameters() throws CommandValidationException;

	/**
	 * {@inheritDoc}
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getCommandParameterMap() {
		return commandParameterMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getCommandResult() {
		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasExecuted() {
		return hasExecuted;
	}

	/**
	 * Set that the command has executed.
	 * 
	 * @param hasExecuted
	 *            true if the command has executed; false otherwise.
	 */
	protected void setExecuted(boolean hasExecuted) {
		this.hasExecuted = hasExecuted();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Command (command=" + getCommand() + " parameters="
				+ getCommandParameterMap() + ") (commandResult="
				+ getCommandResult() + ")";
	}

	/**
	 * Run the command that is wrapped by run.
	 * 
	 * @return Returns the command result.
	 */
	protected abstract T runCommand(Session session);

	/**
	 * {@inheritDoc}
	 */
	public T run(Session session) {
		T commandResult = runCommand(session);

		this.commandResult = commandResult;

		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + command.hashCode();
		result = 37 * result + commandParameterMap.hashCode();

		return result;
	}

	/**
	 * Get a list with a single value obj.
	 * 
	 * @param obj
	 *            The object
	 * @return Returns a list with a single value obj.
	 */
	protected static <S> List<S> getValueList(S obj) {
		List<S> list = new ArrayList<S>();
		list.add(obj);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCommandResult(T commandResult) {
		this.commandResult = commandResult;
	}
}
