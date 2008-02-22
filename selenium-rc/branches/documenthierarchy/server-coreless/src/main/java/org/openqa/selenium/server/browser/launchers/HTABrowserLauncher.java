package org.openqa.selenium.server.browser.launchers;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;

public class HTABrowserLauncher extends AbstractBrowserLauncher {
	private static Logger logger = Logger.getLogger(HTABrowserLauncher.class);
    private File dir;
    private String htaCommandPath;
    private Process htaProcess;
    private Process iexploreProcess;
    
    public HTABrowserLauncher(SeleniumConfiguration seleniumConfiguration, Session session) {
        super(seleniumConfiguration);
    	
    	htaCommandPath = findHTALaunchLocation();
    }
    
    public HTABrowserLauncher(SeleniumConfiguration seleniumConfiguration, Session session, String browserLaunchLocation) {
        super(seleniumConfiguration);
    	
    	htaCommandPath = browserLaunchLocation;
    }
    
    private static String findHTALaunchLocation() {
        String defaultPath = System.getProperty("mshtaDefaultPath");
        if (defaultPath == null) {
            defaultPath = WindowsUtils.findSystemRoot() + "\\system32\\mshta.exe";
        }
        File defaultLocation = new File(defaultPath);
        if (defaultLocation.exists()) {
            return defaultLocation.getAbsolutePath();
        }
        File mshtaEXE = AsyncExecute.whichExec("mshta.exe");
        if (mshtaEXE != null) return mshtaEXE.getAbsolutePath();
        throw new RuntimeException("MSHTA.exe couldn't be found in the path!\n" +
                "Please add the directory containing mshta.exe to your PATH environment\n" +
                "variable, or explicitly specify a path to mshta.exe like this:\n" +
                "*mshta c:\\blah\\mshta.exe");
    }

    private void launch(String url, String htaName) {
        String query = LauncherUtils.getQueryString(url);
        query += "&baseUrl=http://localhost:" + getSeleniumConfiguration().getPort() + "/selenium-server/";
        createHTAFiles();
        String hta = (new File(dir, "core/" + htaName)).getAbsolutePath();
        logger.info("Launching Embedded Internet Explorer...");
        AsyncExecute exe = new AsyncExecute();
        exe.setCommandline(new String[] {InternetExplorerCustomProxyLauncher.findBrowserLaunchLocation(), "-Embedding"});
        try {
            iexploreProcess = exe.asyncSpawn();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Launching Internet Explorer HTA...");
        AsyncExecute htaExe = new AsyncExecute();
        htaExe.setCommandline(new String[] {htaCommandPath, hta, query});
        try {
            htaProcess = htaExe.asyncSpawn();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void createHTAFiles() {
        dir = LauncherUtils.createCustomProfileDir(getSession());
        File coreDir = new File(dir, "core");
        try {
            coreDir.mkdirs();
            ResourceExtractor.extractResourcePath(HTABrowserLauncher.class, "/core", coreDir);
            FileUtils f = FileUtils.getFileUtils();
            File selRunnerSrc = new File(coreDir, "RemoteRunner.html");
            File selRunnerDest = new File(coreDir, "RemoteRunner.hta");
            f.copyFile(selRunnerSrc, selRunnerDest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean close() {
    	if (iexploreProcess != null) {
    		int exitValue = AsyncExecute.killProcess(iexploreProcess);
            if (exitValue == 0) {
                logger.warn("Embedded iexplore seems to have ended on its own (did we kill the real browser???)");
            }
    	}
    	if (htaProcess == null) return false;
    	int exitStatus = AsyncExecute.killProcess(htaProcess);
        LauncherUtils.recursivelyDeleteDir(dir);
        
        return exitStatus == 0;
    }

    public Process getProcess() {
        return htaProcess;
    }

    public void launchHTMLSuite(String suiteUrl, String browserURL, boolean multiWindow) {
        launch(LauncherUtils.getDefaultHTMLSuiteUrl(browserURL, suiteUrl, multiWindow, getSeleniumConfiguration().getPort(), (String) SeleniumConfigurationOption.LOG_LEVEL.getDefaultValue()), "TestRunner.hta");
    }

    public void launchRemoteSession(String browserURL, boolean multiWindow, boolean debugMode) {
        launch(LauncherUtils.getDefaultRemoteSessionUrl(browserURL, getSession(), multiWindow, debugMode, getSeleniumConfiguration().isProxyInjectionMode(), getSeleniumConfiguration().getPort(), getSeleniumConfiguration().getHostname()), "RemoteRunner.hta");
    }

	@Override
	protected void launch(String url) {
		// Does nothing
	}

}
