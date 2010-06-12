package org.openqa.selenium.server;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * #{@link org.openqa.selenium.server.RemoteControlConfiguration} unit test class.
 */
public class RemoteControlConfigurationUnitTest {
	
	private final RemoteControlConfiguration configuration = new RemoteControlConfiguration();

	@Before
	public void setUp() {
	}
	
    @Test public void portIs4444ByDefault() {
        assertEquals(4444, configuration.getPort());
    }

    @Test public void portCanBeSet() {
        configuration.setPort(1234);
        assertEquals(1234, configuration.getPort());
    }

    @Test public void multiWindowIsTrueByDefault() {
        assertTrue((!configuration.isSingleWindow()));
    }

    @Test public void multiWindowCanBeSet() {
        configuration.setSingleWindow(false);
        assertTrue((!configuration.isSingleWindow()));
    }

    @Test public void proxyInjectionModeArgIsFalseByDefault() {
        assertFalse(configuration.getProxyInjectionModeArg());
    }

    @Test public void proxyInjectionModeArgCanBeSet() {
        configuration.setProxyInjectionModeArg(true);
        assertTrue(configuration.getProxyInjectionModeArg());
    }

    @Test public void portDriversShouldContactIsSamePortByDefault() {
        configuration.setPort(1515);
        assertEquals(1515, configuration.getPortDriversShouldContact());
    }

    @Test public void portDriversShouldContactCanBeSet() {
        configuration.setPortDriversShouldContact(1234);
        assertEquals(1234, configuration.getPortDriversShouldContact());
    }

    @Test public void hTMLSuiteIsFalseByDefault() {
        assertFalse(configuration.isHTMLSuite());
    }

    @Test public void hTMLSuiteCanBeSet() {
        configuration.setHTMLSuite(true);
        assertTrue(configuration.isHTMLSuite());
    }

    @Test public void selfTestIsFalseByDefault() {
        assertFalse(configuration.isSelfTest());
    }

    @Test public void selfTestCanBeSet() {
        configuration.setSelfTest(true);
        assertTrue(configuration.isSelfTest());
    }

    @Test public void selfTestDirIsNullByDefault() {
        assertNull(configuration.getSelfTestDir());
    }

    @Test public void selfTestDirCanBeSet() {
        final File aDirectory = new File("\"A Directory Name\"");
        configuration.setSelfTestDir(aDirectory);
        assertEquals(aDirectory, configuration.getSelfTestDir());
    }

    @Test public void interactiveIsFalseByDefault() {
        assertFalse(configuration.isInteractive());
    }

    @Test public void interactiveCanBeSet() {
        configuration.setInteractive(true);
        assertTrue(configuration.isInteractive());
    }

    @Test public void userExtensionsIsNullByDefault() {
        assertNull(configuration.getUserExtensions());
    }

    @Test public void userExtensionsCanBeSet() {
        final File aDirectory = new File("\"A File Name\"");
        configuration.setUserExtensions(aDirectory);
        assertEquals(aDirectory, configuration.getUserExtensions());
    }

    @Test public void userJSInjectionIsFalseByDefault() {
        assertFalse(configuration.userJSInjection());
    }

    @Test public void userJSInjectionCanBeSet() {
        configuration.setUserJSInjection(true);
        assertTrue(configuration.userJSInjection());
    }

    @Test public void trustAllSSLCertificatesIsFalseByDefault() {
        assertFalse(configuration.trustAllSSLCertificates());
    }

    @Test public void trustAllSSLCertificatesCanBeSet() {
        configuration.setTrustAllSSLCertificates(true);
        assertTrue(configuration.trustAllSSLCertificates());
    }

    @Test public void debugURLIsEmptyByDefault() {
        assertEquals("", configuration.getDebugURL());
    }


    @Test public void debugURLCanBeSet() {
        configuration.setDebugURL("A URL");
        assertEquals("A URL", configuration.getDebugURL());
    }

    @Test public void dontInjectRegexIsNullByDefault() {
        assertNull(configuration.getDontInjectRegex());
    }

    @Test public void dontInjectRegexCanBeSet() {
        configuration.setDontInjectRegex("A Regex");
        assertEquals("A Regex", configuration.getDontInjectRegex());
    }

    @Test public void firefoxProfileTemplateIsNullByDefault() {
        assertNull(configuration.getFirefoxProfileTemplate());
    }

    @Test public void firefoxProfileTemplateCanBeSet() {
        final File aDirectory = new File("\"A Directory Path\"");
        configuration.setFirefoxProfileTemplate(aDirectory);
        assertEquals(aDirectory, configuration.getFirefoxProfileTemplate());
    }

    @Test public void reuseBrowserSessionsIsFalseByDefault() {
        assertFalse(configuration.reuseBrowserSessions());
    }

    @Test public void reuseBrowserSessionsCanBeSet() {
        configuration.setReuseBrowserSessions(true);
        assertTrue(configuration.reuseBrowserSessions());
    }

    @Test public void logoutFileNameIsNullByDefault() {
        assertNull(configuration.getLogOutFileName());
    }

    @Test public void logoutFileNameCanBeSet() {
    	configuration.setLogOutFileName("A File Name");
        assertEquals("A File Name", configuration.getLogOutFileName());
    }

    @Test public void forcedBrowserModeIsNullByDefault() {
        assertNull(configuration.getForcedBrowserMode());
    }

    @Test public void forcedBrowserModeCanBeSet() {
        configuration.setForcedBrowserMode("A Mode");
        assertEquals("A Mode", configuration.getForcedBrowserMode());
    }

    @Test public void honorSystemProxyIsFalseByDefault() {
        assertFalse(configuration.honorSystemProxy());

    }

    @Test public void honorSystemProxyCanBeSet() {
        configuration.setHonorSystemProxy(true);
        assertTrue(configuration.honorSystemProxy());

    }

    @Test public void shouldOverrideSystemProxyIsTrueByDefault() {
        assertTrue(configuration.shouldOverrideSystemProxy());

    }

    @Test public void shouldOverrideSystemProxyIsFalseIfHonorSystemProxyIsSet() {
        configuration.setHonorSystemProxy(true);
        assertFalse(configuration.shouldOverrideSystemProxy());
    }

    @Test public void timeoutInSecondsIs30MinutesByDefault() {
        assertEquals(30 * 60, configuration.getTimeoutInSeconds());
    }

    @Test public void timeoutInSecondsCanBeSet() {
        configuration.setTimeoutInSeconds(123);
        assertEquals(123, configuration.getTimeoutInSeconds());
    }

    @Test public void retryTimeoutInSecondsIs10SecondsByDefault() {
        assertEquals(10, configuration.getRetryTimeoutInSeconds());
    }

    @Test public void retryTimeoutInSecondsCanBeSet() {
        configuration.setRetryTimeoutInSeconds(123);
        assertEquals(123, configuration.getRetryTimeoutInSeconds());
    }

    @Test public void dontTouchLoggingIsFalseByDefault() {
        assertFalse(configuration.dontTouchLogging());
    }

    @Test public void dontTouchLoggingCanBeSet() {
        configuration.setDontTouchLogging(true);
        assertTrue(configuration.dontTouchLogging());
    }

    @Test public void shortTermMemoryLoggerCapacityIs50Bydefault() {
        assertEquals(30, configuration.shortTermMemoryLoggerCapacity());
    }

    @Test public void remoteControlConfigurationWillBeCopiedIntoBrowserOptions() throws Exception {
    	final BrowserConfigurationOptions browserOptions = new BrowserConfigurationOptions();
    	
    	String fileName = "file";
    	int timeOut = 5;
    	boolean honorSystemProxy = true;
    	String dontInjectRegex = "newdontInjectRegex";
    	boolean trustAllSSLCertificates = true;
    	File newuserExtensions = new File("newuserExtensions");
    	boolean useUserJSInjection = true;
    	boolean useProxyInjectionMode = true;
    	boolean useSingleWindow = true;
    	boolean ensureCleanSession = true;
    	boolean avoidProxy = true;
    	boolean browserSideLogEnabled = true;
    	
    	configuration.setFirefoxProfileTemplate(new File(fileName));
    	configuration.setTimeoutInSeconds(timeOut);
    	configuration.setHonorSystemProxy(honorSystemProxy);
    	configuration.setDontInjectRegex(dontInjectRegex);
    	configuration.setTrustAllSSLCertificates(trustAllSSLCertificates);
    	configuration.setUserExtensions(newuserExtensions);
    	configuration.setUserJSInjection(useUserJSInjection);
    	configuration.setProxyInjectionModeArg(useProxyInjectionMode);
    	configuration.setSingleWindow(useSingleWindow);
    	configuration.setEnsureCleanSession(ensureCleanSession);
    	configuration.setAvoidProxy(avoidProxy);
    	configuration.setBrowserSideLogEnabled(browserSideLogEnabled);
    	
    	configuration.copySettingsIntoBrowserOptions(browserOptions);
    	
    	assertEquals(fileName, browserOptions.get("firefoxProfileTemplate"));
    	assertEquals(Integer.toString(timeOut), browserOptions.get("timeoutInSeconds"));
    	assertEquals(Boolean.toString(honorSystemProxy), browserOptions.get("honorSystemProxy"));
    	assertEquals(dontInjectRegex, browserOptions.get("dontInjectRegex"));
    	assertEquals(Boolean.toString(trustAllSSLCertificates), browserOptions.get("trustAllSSLCertificates"));
    	assertEquals(newuserExtensions.getName(), browserOptions.get("userExtensions"));
    	assertEquals(Boolean.toString(useUserJSInjection), browserOptions.get("userJSInjection"));
    	assertEquals(Boolean.toString(useProxyInjectionMode), browserOptions.get("proxyInjectionMode"));
    	assertEquals(Boolean.toString(useSingleWindow), browserOptions.get("singleWindow"));
    	assertEquals(Boolean.toString(ensureCleanSession), browserOptions.get("ensureCleanSession"));
    	assertEquals(Boolean.toString(avoidProxy), browserOptions.get("avoidProxy"));
    	assertEquals(Boolean.toString(browserSideLogEnabled), browserOptions.get("browserSideLog"));
    }
    
}
