/*
 * Created on Feb 26, 2006
 *
 */
package org.openqa.selenium.server.browser.launchers.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.launchers.AsyncExecute;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.browser.launchers.LauncherUtils;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Runs HTML Selenium test suites.
 *  
 * @author Dan Fabulich
 * @author Matthew Purland
 */
public class HTMLLauncher implements HTMLResultsListener {

	private static Logger logger = Logger
	.getLogger(HTMLLauncher.class);	
	
    private SeleniumConfiguration seleniumConfiguration;
    private HTMLTestResults results;
    
    public HTMLLauncher(SeleniumConfiguration seleniumConfiguration) {
        this.seleniumConfiguration = seleniumConfiguration;
    }
    
    /** 
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteURL - the relative URL to the HTML suite
     * @param outputFile - The file to which we'll output the HTML results
     * @param timeoutInMs - the amount of time (in milliseconds) to wait for the browser to finish
     * @param multiWindow multi window mode
     * @return Returns true if the test passed; false otherwise.
     * @throws IOException if we can't write the output file
     */
    public boolean runHTMLSuite(String browser, String browserURL, String suiteURL, File outputFile, int timeoutInSeconds) throws IOException {
        outputFile.createNewFile();
        if (!outputFile.canWrite()) {
        	throw new IOException("Can't write to outputFile: " + outputFile.getAbsolutePath());
        }
    	long timeoutInMs = 1000l * timeoutInSeconds;
        if (timeoutInMs < 0) {
            logger.warn("Looks like the timeout overflowed, so resetting it to the maximum.");
            timeoutInMs = Long.MAX_VALUE;
        }
        //server.handleHTMLRunnerResults(this);
//        BrowserLauncherFactory blf = new BrowserLauncherFactory(seleniumConfiguration);
//        String sessionId = Long.toString(System.currentTimeMillis() % 1000000);
        //BrowserLauncher launcher = blf.getBrowserLauncher(browser, sessionId, null);
        //server.registerBrowserLauncher(sessionId, launcher);
        //launcher.launchHTMLSuite(suiteURL, browserURL, multiWindowMode);
        long now = System.currentTimeMillis();
        long end = now + timeoutInMs;
        while (results == null && System.currentTimeMillis() < end) {
            AsyncExecute.sleepTight(500);
        }
        //launcher.close();
        //if (results == null) {
        //    throw new SeleniumCommandTimedOutException();
        //}
        if (outputFile != null) {
            FileWriter fw = new FileWriter(outputFile);
            results.write(fw);
            fw.close();
        }
        
        return results.isPassing();
    }
    
    /** Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFile - a file containing the HTML suite to run
     * @param outputFile - The file to which we'll output the HTML results
     * @param timeoutInMs - the amount of time (in milliseconds) to wait for the browser to finish
     * @param multiWindow - whether to run the browser in multiWindow or else framed mode
     * @return PASSED or FAIL
     * @throws IOException if we can't write the output file
     */
    public String runHTMLSuite(String browser, String browserURL, File suiteFile, File outputFile, int timeoutInSeconds) throws IOException {
        if (browser == null) throw new IllegalArgumentException("browser may not be null");
        if (!suiteFile.exists()) {
    		throw new IOException("Can't find HTML Suite file:" + suiteFile.getAbsolutePath());
    	}
    	if (!suiteFile.canRead()) {
    		throw new IOException("Can't read HTML Suite file: " + suiteFile.getAbsolutePath());
    	}
    	//server.addNewStaticContent(suiteFile.getParentFile());
        
        // DGF this is a hack, but I can't find a better place to put it
        String suiteURL;
        if (browser.startsWith("*chrome") || browser.startsWith("*iehta")) {
            //suiteURL = "http://localhost:" + server.getPortDriversShouldContact() + "/selenium-server/tests/" + suiteFile.getName();
        } else {
            suiteURL = LauncherUtils.stripStartURL(browserURL) + "/selenium-server/tests/" + suiteFile.getName();
        }
    	return null;//runHTMLSuite(browser, browserURL, suiteURL, outputFile, timeoutInSeconds, multiWindow);
    }
    
    
    /** Accepts HTMLTestResults for later asynchronous handling */
    public void processResults(HTMLTestResults resultsParm) {
        this.results = resultsParm;
    }
}
