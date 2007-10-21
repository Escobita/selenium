package org.openqa.selenium.server.command.runner;

import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.Command;
import org.openqa.selenium.server.command.CommandResult;

/**
 * Command runner to execute commands.
 * 
 * @author Matthew Purland
 * @param T The type of command to run
 */
public interface CommandRunner<T extends Command> {	
	/** 
	 * Run the command for the given session.
	 * 
	 * @param command The command to run
	 * @param client The client
	 * 
	 * @returns Returns the commands result; null if there was none returned.
	 */
	CommandResult run(T command, Session session);
}
