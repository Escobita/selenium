package org.openqa.selenium.server.command.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractLocalCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedLocalCommand;

public class AttachFileLocalCommand extends AbstractLocalCommand {
	
	public AttachFileLocalCommand(Map<String, String> commandParameterMap) {
		super(SupportedLocalCommand.ATTACH_FILE.getCommandName(), commandParameterMap);
	}
	
	@Override
	protected CommandResult runCommand(Session session) {
		// FrameGroupCommandQueueSet queue =
		// FrameGroupCommandQueueSet.getQueueSet(sessionId);
		Map<String, String> parametersMap = getCommandParameterMap();

		File downloadedFile = session.downloadFile(parametersMap.get("2"));
		List<File> tempFilesForSession = session.getTempFiles();
		tempFilesForSession.add(downloadedFile);

		Map<String, String> commandParameterMap = new HashMap<String, String>();
		commandParameterMap.put("locator", parametersMap.get("1"));
		commandParameterMap.put("value", downloadedFile.getAbsolutePath());
		RemoteCommand setContextRemoteCommand = new RemoteCommand("type",
				commandParameterMap);

		return session.getWindowManager().run(setContextRemoteCommand);
	}

	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// TODO Auto-generated method stub

	}

}
