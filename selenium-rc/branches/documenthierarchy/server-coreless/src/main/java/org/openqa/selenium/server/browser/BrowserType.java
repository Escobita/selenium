package org.openqa.selenium.server.browser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A browser type for many different browser targets such as Firefox, Internet Explorer, etc.
 * 
 * @author Matthew Purland
 */
public class BrowserType {

	public enum Browser {
		FIREFOX("*firefox"), // Firefox
		IEXPLORE("*iexplore"), // Internet Explorer
		CUSTOM("*custom"), // Custom browser
		CHROME("*chrome"), // Launches Firefox using a chrome URL
		IEHTA("*iehta"), // Launches IE as an HTML Application (HTA)
		PI_FIREFOX("*pifirefox"), // Launches Firefox in PI mode
		PI_IEXPLORE("*piiexplore"),// Launches IE in PI mode
		MOCK("*mock");// Mock browser
		private String browserType;

		Browser(String browserType) {
			this.browserType = browserType;
		}

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			return browserType;
		}
	}

	private static Map<String, Browser> browserStringMap = new HashMap<String, Browser>();

	static {
		browserStringMap.put(Browser.FIREFOX.toString(), Browser.FIREFOX);
		browserStringMap.put(Browser.IEXPLORE.toString(), Browser.IEXPLORE);
		browserStringMap.put(Browser.CUSTOM.toString(), Browser.CUSTOM);
		browserStringMap.put(Browser.CHROME.toString(), Browser.CHROME);
		browserStringMap.put(Browser.IEHTA.toString(), Browser.IEHTA);
		browserStringMap.put(Browser.PI_FIREFOX.toString(), Browser.PI_FIREFOX);
		browserStringMap.put(Browser.PI_IEXPLORE.toString(),
				Browser.PI_IEXPLORE);
		browserStringMap.put(Browser.MOCK.toString(),
				Browser.MOCK);
	}

	private Browser browser;

	private String pathToBrowser;

	public BrowserType(Browser browser) {
		this(browser, "");
	}

	public BrowserType(Browser browser, String pathToBrowser) {
		// Must be a valid browser
		if (browser == null) {
			throw new IllegalArgumentException("browser may not be null");
		}
		this.browser = browser;
		this.pathToBrowser = pathToBrowser;
	}

	/**
	 * Get a browser type from a browserString. A browser string will of the form (e.g. *firefox
	 * /path/to/browser).
	 * 
	 * @param browserString
	 *            The browser string
	 * @return Returns the matching browser string; null if there is no browser type matching the
	 *         browser string.
	 */
	public static BrowserType getBrowserType(String browserString) {
		BrowserType browserType = null;

		if (browserString != null) {
			StringTokenizer tokenizer = new StringTokenizer(browserString, " ");
			int tokens = tokenizer.countTokens();

			Browser browser = null;

			if (tokens > 0) {
				String browserTypeString = tokenizer.nextToken();

				browser = browserStringMap.get(browserTypeString);

				if (tokens > 1) {
					String pathToBrowser = tokenizer.nextToken();
					browserType = new BrowserType(browser, pathToBrowser);

				} else {
					browserType = new BrowserType(browser);
				}
			}
		}

		return browserType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String browserToString = "";

		if (pathToBrowser.length() > 0) {
			browserToString = browser.toString() + " " + pathToBrowser;
		} else {
			browserToString = browser.toString();
		}

		return browserToString;
	}

	/**
	 * Get the path to the browser from the browserString.
	 * 
	 * @return Returns the path to the browser.
	 */
	public String getPathToBrowser() {
		return pathToBrowser;
	}

	/**
	 * Get the browser the browser type represents.
	 * 
	 * @return Returns the browser.
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * Indicates whether the given browserType is equal to the existing {@link BrowserType}
	 * instance. An equal instance will match browser and pathToBrowser.
	 * 
	 * @param browserType
	 *            The browser type to compare
	 * @return Returns true if the given browserType is equal; false otherwise.
	 */
	public boolean equals(BrowserType browserType) {
		boolean isEqual = true;

		// pathToBrowser must be equal
		if (!getPathToBrowser().equals(browserType.getPathToBrowser())) {
			isEqual = false;
		}

		// browser must be equal
		// @todo check toString equals approach
		if (!getBrowser().equals(browserType.getBrowser())) {
			isEqual = false;
		}

		return isEqual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;

		if (obj instanceof BrowserType) {
			BrowserType browserType = (BrowserType) obj;
			isEqual = equals(browserType);
		} else {
			isEqual = super.equals(obj);
		}

		return isEqual;
	}

}
