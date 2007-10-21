package org.openqa.selenium.server.command;

import java.util.Iterator;
import java.util.List;

/**
 * An "OK" result from a command that it was successful.
 * 
 * @author Matthew Purland
 */
public class CSVCommandResult extends OKCommandResult {

	public CSVCommandResult(List<String> result) {
		super(getListAsCSV(result));
	}

	/**
	 * Generates a CSV string from the given string array.
	 * 
	 * @param stringArray
	 *            Array of strings to generate a CSV.
	 */
	static public String getListAsCSV(List<String> list) {
		StringBuffer sb = new StringBuffer();

		if (list != null) {
			for (Iterator<String> i = list.iterator(); i.hasNext();) {
				String str = i.next();

				// If the string contains a slash make it appear as \\ in the
				// protocol
				// 1 slash in Java/regex is \\\\
				str = str.replaceAll("\\\\", "\\\\\\\\");
				str = str.replaceAll(",", "\\\\,");
				sb.append(str);

				if (i.hasNext()) {
					sb.append('\\');
					sb.append(',');
					sb.append(" ");
				}

			}
		}

		return sb.toString();
	}
}
