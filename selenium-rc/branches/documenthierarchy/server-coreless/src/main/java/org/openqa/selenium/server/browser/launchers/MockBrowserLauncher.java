/*
 * Created on Oct 25, 2006
 *
 */
package org.openqa.selenium.server.browser.launchers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.runner.RemoteCommandRunner;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

public class MockBrowserLauncher extends AbstractBrowserLauncher implements Runnable {

	private static Logger logger = Logger.getLogger(MockBrowserLauncher.class);

    private Thread browser;
    private boolean interrupted = false;
    private String uniqueId;
    private int sequenceNumber = 0;
    
    public MockBrowserLauncher(SeleniumConfiguration seleniumConfiguration) {
        super(seleniumConfiguration);
        this.uniqueId = "mock";
    }       
    
    /**
     * {@inheritDoc}
     */
	@Override
	protected void launch(String url) {
        browser = new Thread(this);
        browser.setName("mockbrowser");
        browser.start();
	}

    public boolean close() {
        interrupted = true;
        browser.interrupt();
        return true;
    }

    public Process getProcess() {
        return null;
    }

    public void run() {
        try {
            String startURL = "http://localhost:" + getSeleniumConfiguration().getPort()+"/selenium-server/driver/?sessionId=" + getSession().getSessionId() + "&uniqueId=" + uniqueId;
            String commandLine = doBrowserRequest(startURL+"&seleniumStart=true&sequenceNumber="+sequenceNumber++, "START");
            while (!interrupted) {
            	logger.info("MOCK: " + commandLine);
                RemoteCommand sc = new RemoteCommand(commandLine, null);
                String result = doCommand(sc);
//                if (SeleniumServer.isBrowserSideLogEnabled() && !interrupted) {
//                    for (int i = 0; i < 3; i++) {
//                        doBrowserRequest(startURL + "&logging=true&sequenceNumber="+sequenceNumber++, "logLevel=debug:dummy log message " + i + "\n");
//                    }
//                }
                if (!interrupted) {
                    commandLine = doBrowserRequest(startURL+"&sequenceNumber="+sequenceNumber++, result);
                }
            }
            logger.info("MOCK: interrupted, exiting");
        } catch (Exception e) {
            RuntimeException re = new RuntimeException("Exception in mock browser", e);
            re.printStackTrace();
            throw re;
        }
    }

    private String doCommand(RemoteCommand sc) {
        String command = sc.getCommand();
        String result = "OK";
        if (command.equals("getAllButtons")) {
            result = "OK,";
        } else if (command.equals("getAllLinks")) {
            result = "OK,1";
        } else if (command.equals("getAllFields")) {
            result = "OK,1,2,3";
        } else if (command.equals("getWhetherThisFrameMatchFrameExpression")) {
            result = "OK,true";
        }
        else if (command.startsWith("get")) {
            result = "OK,x";
        } else if (command.startsWith("is")) {
            result = "OK,true";
        }
        return result;
    }
    
    private String stringContentsOfInputStream(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader r = new InputStreamReader(is, "UTF-8");
        int c;
        while ((c = r.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }
    
    private String doBrowserRequest(String url, String body) throws IOException {
        int responsecode = 200;
        URL result = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) result.openConnection();
        
        conn.setRequestProperty("Content-Type", "application/xml");
        // Send POST output.
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(body);
        wr.flush();
        wr.close();
        //conn.setInstanceFollowRedirects(false);
        //responsecode = conn.getResponseCode();
        if (responsecode == 301) {
            String pathToServlet = conn.getRequestProperty("Location");
            throw new RuntimeException("Bug! 301 redirect??? " + pathToServlet);
        } else if (responsecode != 200) {
            throw new RuntimeException(conn.getResponseMessage());
        } else {
            InputStream is = conn.getInputStream();
            return stringContentsOfInputStream(is);
        }
    }
}
