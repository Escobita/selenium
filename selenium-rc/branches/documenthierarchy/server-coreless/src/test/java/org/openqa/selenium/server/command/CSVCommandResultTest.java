package org.openqa.selenium.server.command;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test for CSV related functionality in command results.
 * 
 * @author Matthew Purland
 */
public class CSVCommandResultTest extends TestCase {
	public static final String[] INPUT_VALUES = { "test", "1", "2", "3" };
	public static final String INPUT_VALUES_CSV = "test\\, 1\\, 2\\, 3";
	public static final List<String> INPUT_LIST = new ArrayList<String>();
	
	static {
		for (String inputValue : INPUT_VALUES) {
			INPUT_LIST.add(inputValue);
		}
	}
	
	public void testGetListAsCSV() {
		String CSV = CSVCommandResult.getListAsCSV(INPUT_LIST);
		assertEquals("CSV from getListAsCSV is incorrect.", INPUT_VALUES_CSV, CSV);
	}
	
	public void testCSVCommandResult() {
		CommandResult commandResult = new CSVCommandResult(INPUT_LIST);
		
		final String EXPECTED_CSV = "OK," + INPUT_VALUES_CSV;
		
		assertEquals("CSV from CSVCommandResult is incorrect.", EXPECTED_CSV, commandResult.getCommandResult());
	}
	
	public void testCSVCommandResultFromGetListAsCSV() {
		CommandResult commandResult = new CSVCommandResult(INPUT_LIST);
		final String EXPECTED_CSV = "OK," + CSVCommandResult.getListAsCSV(INPUT_LIST);
		assertEquals("CSV from CSVCommandResult should be equal to OK, prefixed from getListAsCSV.", EXPECTED_CSV, commandResult.getCommandResult());	
	}
}
