package org.openqa.selenium.server.client;

/**
 * 
 * @author Matthew Purland
 *
 */
public class WindowManagerTest {
	private WindowManager windowManager;
	
	private static final String WINDOW_NAME = "someWindowName";
	
	public void setUp() {
		windowManager = new WindowManager();
	}
	
	public void testAddDocumentStructure() {
		//windowManager.addDocumentStructure(uniqueId, windowName, frameAddress, frameName, modalDialog)
	}
}
