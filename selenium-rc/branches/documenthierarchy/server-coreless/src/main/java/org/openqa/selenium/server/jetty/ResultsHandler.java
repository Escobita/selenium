package org.openqa.selenium.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.launchers.html.HTMLResultsListener;
import org.openqa.selenium.server.browser.launchers.html.HTMLTestResults;
import org.openqa.selenium.server.jetty.Handler.Method;

public class ResultsHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(ResultsHandler.class);

	private List<HTMLResultsListener> listeners;

	private boolean started = false;

	public ResultsHandler() {
		listeners = new ArrayList<HTMLResultsListener>();
	}

	public void addListener(HTMLResultsListener listener) {
		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handle(String contextPath, String queryString, Map parameterMap,
			Method method, WebRequest webRequest, WebResponse webResponse, OutputStream outputStream)  throws IOException {
		boolean requestWasHandled = false;

		contextPath = getResourceContextPath(webRequest.getRequestURL(), contextPath);

		if (!"/core/postResults".equals(contextPath))
			return requestWasHandled;

		requestWasHandled = true;
		logger.info("Handling results request " + webRequest.getRequestURL() + "?"
				+ queryString);

		String result = getParameter("result", parameterMap);

		if (result == null) {
			outputStream.write("No result was specified!".getBytes());
		}

		String seleniumVersion = getParameter("selenium.version", parameterMap);
		String seleniumRevision = getParameter("selenium.revision",
				parameterMap);
		String totalTime = getParameter("totalTime", parameterMap);
		String numTestPasses = getParameter("numTestPasses", parameterMap);
		String numTestFailures = getParameter("numTestFailures", parameterMap);
		String numCommandPasses = getParameter("numCommandPasses", parameterMap);
		String numCommandFailures = getParameter("numCommandFailures",
				parameterMap);
		String numCommandErrors = getParameter("numCommandErrors", parameterMap);
		String suite = getParameter("suite", parameterMap);

		if (numTestPasses != null && numTestFailures != null && suite != null) {

			int numTotalTests = Integer.parseInt(numTestPasses)
					+ Integer.parseInt(numTestFailures);

			List<String> testTables = createTestTables(parameterMap,
					numTotalTests);

			HTMLTestResults results = new HTMLTestResults(seleniumVersion,
					seleniumRevision, result, totalTime, numTestPasses,
					numTestFailures, numCommandPasses, numCommandFailures,
					numCommandErrors, suite, testTables);

			for (Iterator i = listeners.iterator(); i.hasNext();) {
				HTMLResultsListener listener = (HTMLResultsListener) i.next();
				listener.processResults(results);
				i.remove();
			}

			processResults(results, outputStream);

		} else {
			outputStream.write("Arguments must be specified.".getBytes());
		}

		if (requestWasHandled) {
			outputStream.flush();
		}

		return requestWasHandled;
	}

	/**
	 * Print the test results out to the HTML response
	 */
	private void processResults(HTMLTestResults results,
			OutputStream outputStream) throws IOException {
		Writer writer = new OutputStreamWriter(outputStream, "ISO-8859-1");
		results.write(writer);
		writer.flush();
	}

	private List<String> createTestTables(Map parameterMap, int numTotalTests) {
		List<String> testTables = new LinkedList<String>();
		for (int i = 1; i <= numTotalTests; i++) {
			String testTable = getParameter("testTable." + i, parameterMap);
			// System.out.println("table " + i);
			// System.out.println(testTable);
			testTables.add(testTable);
		}
		return testTables;
	}

}
