package org.openqa.selenium.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.server.browser.launchers.LauncherUtils;
import org.openqa.selenium.server.client.Session;

/**
 * Default implementation for a remote command.
 * 
 * @author Matthew Purland
 */
public class RemoteCommand<T extends CommandResult> extends AbstractCommand<T> {
	private static Logger logger = Logger.getLogger(RemoteCommand.class);

	private static int commandIdCounter = 0;
	
	private boolean hasReceivedResult = false;

	private String attachedJavascript;
	
	private String commandId;
	
	private WaitingType waitingType = WaitingType.WAIT_FOR_COMMAND_RESULT;
	
	private final Pattern JSON_ESCAPABLES = Pattern.compile("([\\\\\"'\b\f\n\r\t])");
	
	public enum WaitingType {
		WAIT_FOR_COMMAND_RESULT,
		DONT_WAIT
	}
	
	/**
	 * Construct a new remote command from the given command and list of command parameters as
	 * values.
	 * 
	 * @param command
	 *            The command
	 * @param commandParameterMap
	 *            The map of command parameters
	 */
	public RemoteCommand(String command,	Map<String, String> commandParameterMap) {
		super(command, commandParameterMap);
		
		this.commandId = createNewCommandId();
	}
	
	protected String createNewCommandId() {
		// Increment counter
		commandIdCounter++;
		return String.valueOf(commandIdCounter);
	}	

	/**
	 * Check to determine if the remote command has finished executing and has a result.
	 * 
	 * @return Returns true if the remote command has a result.
	 */
	public boolean hasReceivedResult() {
		return hasReceivedResult;
	}
	
    public String getJSONString() throws JSONException {
		JSONObject commandObject = new JSONObject();
		commandObject.put("command", getCommand());
		// @todo Should be able to have multiple commands and parameters in JSON
		// @todo Only supports 2 parameters right now! 
		Object target = "";
		Object value = "";
		
		if (!getCommandParameterMap().isEmpty()) {
			String[] parameterMap = getCommandParameterMap().values().toArray(new String[0]);
			
			// Reorder parameters correctly for 1-param and 2-param
			if (parameterMap.length > 0) {
				if (parameterMap.length == 1) {
					target = parameterMap[0];
				}
				if (parameterMap.length == 2) {
					target = parameterMap[1];
					value = parameterMap[0];
				}
			}
		}
		commandObject.put("target", target);
		commandObject.put("value", value);
		commandObject.put("commandId", getCommandId());
		
		if (getAttachedJavascript() != null && !"".equals(getAttachedJavascript())) {
			commandObject.put("rest", getAttachedJavascript());
		}
		String jsonString = commandObject.toString();
		return "json=" + jsonString;
    }

    /**
     * Factory method to create a RemoteCommand from a wiki-style input string.
     * 
     * @param jsonInputString Input string containing a JSON input-style for a command object.
     **/
    public static RemoteCommand<RemoteCommandResult> parse(String jsonInputString) throws JSONException {
        if (jsonInputString == null) throw new NullPointerException("jsonInputString must not be null");
        jsonInputString = jsonInputString.trim();
        // TODO use a real JSON library (but it should be Apache licensed and less than 1.4 megs including deps!)
        final String jsonPrefix = "json=";
        //final String jsonCommandPrefix = jsonPrefix + "={command:\"";
        if (!jsonInputString.startsWith(jsonPrefix)) throw new IllegalArgumentException("invalid command string, missing '" + jsonPrefix + "'=" + jsonInputString);
        
        JSONObject jsonCommandObject = new JSONObject(jsonInputString.substring(jsonPrefix.length()));
        Map<String, String> jsonCommandParameterMap = new HashMap<String, String>();
        jsonCommandParameterMap.put("target", jsonCommandObject.getString("target"));
        jsonCommandParameterMap.put("value", jsonCommandObject.getString("value"));
        
        return new RemoteCommand<RemoteCommandResult>(jsonCommandObject.getString("command"), jsonCommandParameterMap);
//        int index = prefix.length();
//        int hackToPassByReference[] = new int[1];
//        hackToPassByReference[0] = index;
//        String command = parseJSONString(inputLine, hackToPassByReference);
//        index = hackToPassByReference[0] + 1;
//        final String targetDelim = ",target:\"";
//        if (!(inputLine.length() > index + targetDelim.length())) throw new IllegalArgumentException("invalid command string, missing '" + targetDelim + "'=" + inputLine);
//        if (!inputLine.substring(index, index + targetDelim.length()).equals(targetDelim)) throw new IllegalArgumentException("invalid command string, missing '" + targetDelim + "'=" + inputLine);
//        index += targetDelim.length();
//        hackToPassByReference[0] = index;
//        String target = parseJSONString(inputLine, hackToPassByReference);
//        index = hackToPassByReference[0] + 1;
//        final String valueDelim = ",value:\"";
//        if (!(inputLine.length() > index + valueDelim.length())) throw new IllegalArgumentException("invalid command string, missing '" + valueDelim + "'=" + inputLine);
//        if (!inputLine.substring(index, index + valueDelim.length()).equals(valueDelim)) throw new IllegalArgumentException("invalid command string, missing '" + valueDelim + "'=" + inputLine);
//        index += valueDelim.length();
//        hackToPassByReference[0] = index;
//        String value = parseJSONString(inputLine, hackToPassByReference);
//        index = hackToPassByReference[0] + 1;
//        final String restDelim = ",rest:\"";
//        if (!(inputLine.length() > index + restDelim.length())) {
//            return new DefaultRemoteCommand(command, target, value);
//        }
//        if (!inputLine.substring(index, index + restDelim.length()).equals(restDelim)) throw new IllegalArgumentException("invalid command string, missing '" + restDelim + "'=" + inputLine);
//        index += restDelim.length();
//        hackToPassByReference[0] = index;
//        String rest = parseJSONString(inputLine, hackToPassByReference);
//        return new DefaultRemoteCommand(command, target, value, rest);
    }

    private static String parseJSONString(String inputLine, int[] hackToPassByReference) {
        int index = hackToPassByReference[0];
        StringBuffer sb = new StringBuffer();
        boolean finished = false;
        for (; index < inputLine.length(); index++) {
            char c = inputLine.charAt(index);
            if ('"' == c) {
                finished = true;
                break;
            }
            if ('\\' == c) {
                c = inputLine.charAt(++index);
                switch (c) {
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        String fourHexDigits = inputLine.substring(index+1, index+5);
                        c = (char) Integer.parseInt(fourHexDigits, 16);
                        sb.append(c);
                        index+=4;
                        break;
                    default:
                        // probably \ or "
                        sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        if (!finished) {
            throw new IllegalArgumentException("Invalid JSON string, quote never terminated: " + inputLine);
        }
        hackToPassByReference[0] = index;
        return sb.toString();
    }    
    
	/**
	 * Check to determine if the remote command has finished executing and has a result.
	 * 
	 * @return Returns true if the remote command has a result.
	 */
	public String getBrowserCommandResponse() {
		StringBuffer buffer = new StringBuffer();

		String browserCommandResponse = "";
		
//		Set<String> commandParameterMapKeySet = getCommandParameterMap()
//				.keySet();
//
//		buffer.append("cmd=" + LauncherUtils.urlEncode(getCommand()));
//
//		// int i = 1;
//
//		for (String commandParameterKey : commandParameterMapKeySet) {
////			int i = SeleniumCommandTranslator.getIndexFromArgumentName(
////					getCommand(), commandParameterKey);
//			
//			int i = 0;
//			try { 
//				i = Integer.parseInt(commandParameterKey);
//				buffer.append("&"
//						+ i
//						+ "="
//						+ LauncherUtils.urlEncode(getCommandParameterMap().get(
//								commandParameterKey)));
//				// i++;
//			}
//			catch (NumberFormatException ex) {
//				// Do nothing...
//			}
//		}
//		
//		buffer.append("&commandId=" + getCommandId());
//
//		browserCommandResponse = buffer.toString();
//		
//		if (getAttachedJavascript() != null && !"".equals(getAttachedJavascript())) {
//			browserCommandResponse = browserCommandResponse + "\n" + getAttachedJavascript();
//		}
//		
		
		// Output to browser in JSON
		try {
			browserCommandResponse = getJSONString();
		}
		catch (JSONException ex) {
			logger.error("JSONException occurred.  Completing test...", ex);
			browserCommandResponse = "{\"command\":\"testComplete\"}";
		}
		
		return browserCommandResponse;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T run(Session session) {
		T commandResult = super.run(session);

		hasReceivedResult = true;

		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		// @todo Can we get command result in the toString?
		// @todo Command result was removed because of blocking getCommandResult
		return "Remote Command (command=" + getCommand() + " parameters="
				+ getCommandParameterMap() + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected T runCommand(Session session) {
		// No implementation for a remote command...
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// @todo Should we have remote command validators be called in here?
		// Nothing to validate yet...
	}

	/**
	 * Get the attached javascript.
	 */
	protected String getAttachedJavascript() {
		return attachedJavascript;
	}

	/**
	 * Set the attached javascript.
	 * 
	 * @param attachedJavascript
	 *            The javascript to set
	 */
	public void setAttachedJavascript(String attachedJavascript) {
		this.attachedJavascript = attachedJavascript;
	}

	public String getCommandId() {
		return commandId;
	}
	
	/**
	 * Set the type of waiting for the command.
	 */
	public void setWaitingType(WaitingType waitingType) {
		this.waitingType = waitingType;
	}

	/**
	 * Get the waiting type for the command.
	 */
	public WaitingType getWaitingType() {
		return waitingType;
	}
	
	/**
	 * Get the field.
	 */
	public String getField() {
		return getCommandParameterMap().get("1");
	}
}
