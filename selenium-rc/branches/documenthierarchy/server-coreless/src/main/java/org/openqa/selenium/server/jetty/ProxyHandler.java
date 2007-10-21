package org.openqa.selenium.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.jetty.Handler.Method;

/**
 * Not implemented right now.  Using Jetty5ProxyHandler for independent 
 * implementation for Jetty5, will have to reimplement handler for each
 * web server independently for it to support proxying.
 * 
 * @author Matthew Purland
 */
public class ProxyHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(ProxyHandler.class);

//	protected class URI {
//		private String scheme;
//		private String host;
//		private int port;
//		
//		public URI(String scheme, String host, int port) {
//			this.scheme =scheme;
//			this.host = host;
//			this.port = port;
//		}
//
//		public String getHost() {
//			return host;
//		}
//
//		public int getPort() {
//			return port;
//		}
//
//		public String getScheme() {
//			return scheme;
//		}
//	}
	
//	/**
//	 * Check if a URL is a Selenium URL.
//	 * 
//	 * @param url
//	 *            The url to check
//	 * @return Returns true if the url is a selenium url; false otherwise.
//	 */
//	private boolean isSeleniumUrl(String url) {
//		int slashSlash = url.indexOf("//");
//		if (slashSlash == -1) {
//			return false;
//		}
//
//		int nextSlash = url.indexOf("/", slashSlash + 2);
//		if (nextSlash == -1) {
//			return false;
//		}
//
//		final String SELENIUM_SERVER_PATH = AbstractSeleniumWebServer.SELENIUM_SERVER_PATH
//				+ "/";
//
//		int seleniumServer = url.indexOf(SELENIUM_SERVER_PATH);
//		if (seleniumServer == -1) {
//			return false;
//		}
//
//		// we do this complex checking because sometimes some sites/pages (such as ominture ads)
//		// embed the referrer URL,
//		// which will include selenium stuff, in to the query parameter, which would fake out a
//		// simple String.contains()
//		// call. This method is more robust and will catch this stuff.
//		return seleniumServer == nextSlash;
//	}
//	
//    /**
//     * Is URL Proxied. Method to allow derived handlers to select which URIs are proxied and to
//     * where.
//     *
//     * @param uri The requested URI, which should include a scheme, host and port.
//     * @return The URL to proxy to, or null if the passed URI should not be proxied. The default
//     *         implementation returns the passed uri if isForbidden() returns true.
//     */
//    protected URL isProxied(String url, String scheme, String host, String port) throws MalformedURLException {
//        // Is this a proxy request?
//        if (isForbidden(scheme, host, port))
//            return null;
//
//        // OK return URI as untransformed URL.
//        return new URL(url);
//    }
//    
//    /**
//     * Is URL Forbidden.
//     *
//     * @return True if the URL is not forbidden. Calls isForbidden(scheme,host,port,true);
//     */
//    protected boolean isForbidden(String scheme, String host, String port) {
////        String scheme = uri.getScheme();
////        String host = uri.getHost();
////        int port = uri.getPort();
//        return isForbidden(scheme, host, port, true);
//    }
//    
//    /**
//     * Is scheme,host & port Forbidden.
//     *
//     * @param scheme           A scheme that mast be in the proxySchemes StringMap.
//     * @param host             A host that must pass the white and black lists
//     * @param port             A port that must in the allowedConnectPorts Set
//     * @param openNonPrivPorts If true ports greater than 1024 are allowed.
//     * @return True if the request to the scheme,host and port is not forbidden.
//     */
//    protected boolean isForbidden(String scheme, String host, int port, boolean openNonPrivPorts) {
//        // Check port
//        if (port > 0 && !_allowedConnectPorts.contains(new Integer(port))) {
//            if (!openNonPrivPorts || port <= 1024)
//                return true;
//        }
//
//        // Must be a scheme that can be proxied.
//        if (scheme == null || !_ProxySchemes.containsKey(scheme))
//            return true;
//
//        // Must be in any defined white list
//        if (_proxyHostsWhiteList != null && !_proxyHostsWhiteList.contains(host))
//            return true;
//
//        // Must not be in any defined black list
//        return _proxyHostsBlackList != null && _proxyHostsBlackList.contains(host);
//
//    }

	/**
	 * {@inheritDoc}
	 */
	public boolean handle(String contextPath, String queryString, Map parameterMap,
			Method method, WebRequest webRequest, WebResponse webResponse, OutputStream outputStream)  throws IOException {
		// Request will only be handled if it is proxied
		boolean requestWasHandled = false;

////		URI uri = new URI(requestScheme, requestHost, requestPort);
//		
//		if (method.equals(Method.CONNECT)) {
//
//		}
//
//		// Do we proxy this?
//		URL url = isProxied(requestScheme, requestHost, requestPort);		
		
		return requestWasHandled;
	}

}
