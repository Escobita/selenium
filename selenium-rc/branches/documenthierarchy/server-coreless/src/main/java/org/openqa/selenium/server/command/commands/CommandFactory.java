package org.openqa.selenium.server.command.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.Command;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.LocalCommand;
import org.openqa.selenium.server.command.ProxyCommand;
import org.openqa.selenium.server.command.runner.CommandRunner;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Factory to create commands from a map of supported commands. (Local and Remote).
 * 
 * @author Matthew Purland
 */
public class CommandFactory {
	private static Logger logger = Logger.getLogger(CommandFactory.class);

	private static Map<String, SupportedLocalCommand> supportedLocalCommands = new ConcurrentHashMap<String, SupportedLocalCommand>();

	private static Map<String, SupportedProxyCommand> supportedProxyCommands = new ConcurrentHashMap<String, SupportedProxyCommand>();

	private SeleniumConfiguration seleniumConfiguration;

	public interface SupportedCommand {
		/**
		 * Get the class for the type of the local command.
		 * 
		 * @return Returns the class.
		 */
		public Class<? extends Command> getCommandClass();

		/**
		 * Get the name of the command.
		 * 
		 * @return Returns the name of the command.
		 */
		public String getCommandName();
	}

	public CommandFactory(SeleniumConfiguration seleniumConfiguration) {
		this.seleniumConfiguration = seleniumConfiguration;
	}

	public enum SupportedLocalCommand implements SupportedCommand {
		// Local commands

		// getNewBrowserSession
		GET_NEW_BROWSER_SESSION("getNewBrowserSession",
				GetNewBrowserSessionCommand.class),

		// testComplete
		TEST_COMPLETE("testComplete", TestCompleteCommand.class),

		// isPostSupported
		IS_POST_SUPPORTED("isPostSupported", IsPostSupportedCommand.class),
		
		// attachFile
		ATTACH_FILE("attachFile", AttachFileLocalCommand.class);

		private final String commandName;

		private final Class<? extends LocalCommand> commandClass;

		SupportedLocalCommand(String commandName,
				Class<? extends LocalCommand> commandClass) {
			this.commandName = commandName;
			this.commandClass = commandClass;
		}

		/**
		 * {@inheritDoc}
		 */
		public Class<? extends Command> getCommandClass() {
			return commandClass;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getCommandName() {
			return commandName;
		}
	}

	public enum SupportedProxyCommand implements SupportedCommand {
		// close
		CLOSE("close", CloseProxyCommand.class),

		// open
		OPEN("open", OpenProxyCommand.class),
		
		// waitForPageToLoad
		WAIT_FOR_PAGE_TO_LOAD("waitForPageToLoad", WaitForPageToLoadProxyCommand.class),
		
		// waitForFrameToLoad
		WAIT_FOR_FRAME_TO_LOAD("waitForFrameToLoad", WaitForFrameToLoadProxyCommand.class),
		
		// selectFrame
		SELECT_FRAME("selectFrame", SelectFrameProxyCommand.class),
		
		// selectWindow
		SELECT_WINDOW("selectWindow", SelectWindowProxyCommand.class),
		
		// waitForPopup
		WAIT_FOR_POPUP("waitForPopUp", WaitForPopupProxyCommand.class),
		
		// waitForPopup
		GET_ALL_WINDOW_NAMES("getAllWindowNames", GetAllWindowNamesProxyCommand.class),
		
		// getTitle
		GET_TITLE("getTitle", GetTitleProxyCommand.class);
		
		private final String commandName;

		private final Class<? extends Command> commandClass;

		SupportedProxyCommand(String commandName,
				Class<? extends Command> commandClass) {
			this.commandName = commandName;
			this.commandClass = commandClass;
		}

		/**
		 * {@inheritDoc}
		 */
		public Class<? extends Command> getCommandClass() {
			return commandClass;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getCommandName() {
			return commandName;
		}
	}

	static {
		// Local commands
		supportedLocalCommands.put(
				SupportedLocalCommand.GET_NEW_BROWSER_SESSION.getCommandName(),
				SupportedLocalCommand.GET_NEW_BROWSER_SESSION);
		supportedLocalCommands.put(SupportedLocalCommand.TEST_COMPLETE
				.getCommandName(), SupportedLocalCommand.TEST_COMPLETE);
		supportedLocalCommands.put(SupportedLocalCommand.IS_POST_SUPPORTED
				.getCommandName(), SupportedLocalCommand.IS_POST_SUPPORTED);
		supportedLocalCommands.put(SupportedLocalCommand.ATTACH_FILE
				.getCommandName(), SupportedLocalCommand.ATTACH_FILE);
		
		// Proxy commands
//		supportedProxyCommands.put(
//				SupportedProxyCommand.CLOSE.getCommandName(),
//				SupportedProxyCommand.CLOSE);
		supportedProxyCommands.put(
				SupportedProxyCommand.OPEN.getCommandName(),
				SupportedProxyCommand.OPEN);
		supportedProxyCommands.put(
				SupportedProxyCommand.WAIT_FOR_PAGE_TO_LOAD.getCommandName(),
				SupportedProxyCommand.WAIT_FOR_PAGE_TO_LOAD);
		supportedProxyCommands.put(
				SupportedProxyCommand.WAIT_FOR_FRAME_TO_LOAD.getCommandName(),
				SupportedProxyCommand.WAIT_FOR_FRAME_TO_LOAD);
		supportedProxyCommands.put(
				SupportedProxyCommand.SELECT_FRAME.getCommandName(),
				SupportedProxyCommand.SELECT_FRAME);
		supportedProxyCommands.put(
				SupportedProxyCommand.SELECT_WINDOW.getCommandName(),
				SupportedProxyCommand.SELECT_WINDOW);
		supportedProxyCommands.put(
				SupportedProxyCommand.WAIT_FOR_POPUP.getCommandName(),
				SupportedProxyCommand.WAIT_FOR_POPUP);
		supportedProxyCommands.put(
				SupportedProxyCommand.GET_ALL_WINDOW_NAMES.getCommandName(),
				SupportedProxyCommand.GET_ALL_WINDOW_NAMES);
//		supportedProxyCommands.put(
//				SupportedProxyCommand.GET_TITLE.getCommandName(),
//				SupportedProxyCommand.GET_TITLE);
	}

	/**
	 * Get the command from the supported commands map.
	 * 
	 * @param supportedCommandsMap
	 *            The supported commands map
	 * @param commandName
	 *            The command name
	 * @param session
	 *            The session the command is being invoked under
	 * @param parametersMap
	 *            Map of parameter names to parameter values
	 * 
	 * @return Returns the command; null if the command is not supported.
	 */
	protected Command getCommand(
			Map<String, ? extends SupportedCommand> supportedCommandsMap,
			String commandName, Session session,
			Map<String, String> parametersMap) {
		SupportedCommand supportedCommand = supportedCommandsMap
				.get(commandName);
		Command commandInstance = null;

		if (supportedCommand != null) {
			Class<? extends Command> commandClass = supportedCommand
					.getCommandClass();

			try {

				Constructor<? extends Command> commandConstructor = commandClass
						.getConstructor(new Class[] { Map.class });

				commandInstance = commandConstructor.newInstance(parametersMap);
			} catch (InstantiationException e) {
				logger.error(e);
			} catch (IllegalAccessException e) {
				logger.error(e);
			} catch (SecurityException e) {
				logger.error(e);
			} catch (NoSuchMethodException e) {
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			} catch (InvocationTargetException e) {
				logger.error(e);
			}
		}

		return commandInstance;

	}

	public void validateCommandParametersMap(String commandName,
			Session session, Map<String, String> parametersMap) {

	}

	/**
	 * Get the local command.
	 * 
	 * @param commandName
	 *            Name of the command
	 * @param session
	 *            The session the command is being invoked under
	 * @param parametersMap
	 *            Map of parameter names to parameter values
	 * @return Returns the command instance for the given command name; null if the command is not
	 *         supported.
	 */
	public LocalCommand<CommandResult> getLocalCommand(String commandName, Session session,
			Map<String, String> parametersMap) {
		Command command = getCommand(supportedLocalCommands, commandName,
				session, parametersMap);

		LocalCommand localCommand = null;

		if (command != null) {
			localCommand = (LocalCommand) command;
		}

		return localCommand;
	}

	/**
	 * Get the remote command.
	 * 
	 * @param commandName
	 *            Name of the command
	 * @param session
	 *            The session the command is being invoked under
	 * @param parametersMap
	 *            Map of parameter names to parameter values
	 * @return Returns the command instance for the given command name; null if the command is not
	 *         supported.
	 */
	public ProxyCommand<CommandResult> getProxyCommand(String commandName, Session session,
			Map<String, String> parametersMap) {

		ProxyCommand proxyCommand = null;
		Command command = getCommand(supportedProxyCommands, commandName, session,
				parametersMap);
		
		if (command != null) {
			proxyCommand = (ProxyCommand) command;
		}

		return proxyCommand;
	}

}
