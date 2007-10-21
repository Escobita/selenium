package org.openqa.selenium.server.browser.launchers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;


/**
 * launcher for IE under proxy injection mode
 * 
 * In proxy injection mode, the selenium server is a proxy for all traffic from the browser, 
 * not just traffic going to selenium-server URLs.  The incoming HTML is modified 
 * to include selenium's JavaScript, which then controls the test page from within (as 
 * opposed to controlling the test page from a different window, as selenium remote 
 * control normally does).
 * 
 * @author nelsons
 *
 */
public class ProxyInjectionInternetExplorerCustomProxyLauncher extends InternetExplorerCustomProxyLauncher {
    private static boolean changeMaxConnections = true;
    
    private static final int MAX_CONNECTIONS = 512;
    
	private static Logger logger = Logger
	.getLogger(InternetExplorerCustomProxyLauncher.class);
    
    public ProxyInjectionInternetExplorerCustomProxyLauncher(SeleniumConfiguration seleniumConfiguration) {
        super(seleniumConfiguration);
    }
    
    public ProxyInjectionInternetExplorerCustomProxyLauncher(SeleniumConfiguration seleniumConfiguration, String browserLaunchLocation) {
        super(seleniumConfiguration, browserLaunchLocation);
    }    
    
    @Override
    protected void changeRegistrySettings() throws IOException {
    	final int port = getSeleniumConfiguration().getPort();
    	
    	logger.info("Changing registry settings for Proxy Injection Internet Explorer...");
    	
        customPACappropriate = false;
        super.changeRegistrySettings();
        WindowsUtils.writeBooleanRegistryValue(REG_KEY_BASE + REG_KEY_PROXY_ENABLE, true);
        WindowsUtils.writeStringRegistryValue(REG_KEY_BASE + REG_KEY_PROXY_SERVER, "127.0.0.1:" + port);

        if (changeMaxConnections) {
            // need at least 1 xmlHttp connection per frame/window
            WindowsUtils.writeIntRegistryValue(REG_KEY_BASE + REG_KEY_MAX_CONNECTIONS_PER_1_0_SVR, MAX_CONNECTIONS);
            WindowsUtils.writeIntRegistryValue(REG_KEY_BASE + REG_KEY_MAX_CONNECTIONS_PER_1_1_SVR, MAX_CONNECTIONS);
        }
    }

    public static void setChangeMaxConnections(boolean changeMaxConnections) {
        ProxyInjectionInternetExplorerCustomProxyLauncher.changeMaxConnections = changeMaxConnections;
    }
}
