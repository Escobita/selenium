package org.openqa.selenium.server;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class SeleniumServerTestSuite extends TestSuite {
	static class SeleniumServerTestSetup extends TestSetup {
		public SeleniumServerTestSetup(Test test) {
			super(test);
		}

		public void setUp() {
	    	SeleniumServer.startServer(new String[0]);
		}
		
		public void tearDown() {
			SeleniumServer.stopServer();
		}	
	}

	public static Test suite() {
		TestSuite superSuite = new TestSuite(SeleniumServerTestSuite.class.getName());
	    TestSuite suite = new TestSuite(SeleniumServerTestSuite.class.getName());
	    
	    //suite.addTestSuite(SeleniumClientToServerTest.class);
	    
	    // Add setUp and tearDown for test suite
	    //superSuite.addTest(new SeleniumServerTestSetup(suite));
	    return superSuite;
	}
}
