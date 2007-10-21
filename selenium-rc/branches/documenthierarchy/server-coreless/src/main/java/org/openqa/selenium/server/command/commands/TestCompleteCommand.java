package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractLocalCommand;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedLocalCommand;

/**
 * Local command to indicate the test is complete and the session should be closed.
 * 
 * This should result in all browsers for the given session being shutdown.
 * 
 * @author Matthew Purland
 */
public class TestCompleteCommand extends AbstractLocalCommand<OKCommandResult> {
	private static Logger logger = Logger.getLogger(TestCompleteCommand.class);

	public TestCompleteCommand(Map<String, String> commandParameterMap) {
		super(SupportedLocalCommand.TEST_COMPLETE.getCommandName(), commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OKCommandResult runCommand(Session session) {
		logger.info("Test has completed. Closing session " + session);
		
		session.close();
		
		return new OKCommandResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// Nothing to validate...
	}

}
