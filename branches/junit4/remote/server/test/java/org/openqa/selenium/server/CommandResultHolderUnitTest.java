package org.openqa.selenium.server;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.jetty.log.LogFactory;
import org.openqa.selenium.server.log.LoggingManager;
import org.openqa.selenium.server.log.StdOutHandler;
import org.openqa.selenium.server.log.TerseFormatter;
import org.openqa.selenium.testworker.TrackableRunnable;
import org.openqa.selenium.testworker.TrackableThread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommandResultHolderUnitTest {
  private static Log log = LogFactory.getLog(CommandResultHolderUnitTest.class);

  private static final String sessionId = "1";
  private static final String testResult = "OK";
  
  private static final int cmdTimeout = 3;
  private CommandResultHolder holder;
  
  @Before
  public void setUp() throws Exception {
    configureLogging();
    holder = new CommandResultHolder(sessionId, cmdTimeout);
    log.info("Start test: " + getName());
  }

  private String getName() {
    fail("The pain");
    return null;
  }

  private void configureLogging() throws Exception {
      LoggingManager.configureLogging(new RemoteControlConfiguration(), true);
      Logger logger = Logger.getLogger("");
      for (Handler handler : logger.getHandlers()) {
          if (handler instanceof StdOutHandler) {
              handler.setFormatter(new TerseFormatter(true));
              break;
          }
      }
  }
  
  @After
  public void tearDown() throws Exception {
      LoggingManager.configureLogging(new RemoteControlConfiguration(), true);
  }

  @Test public void getCommandGeneratesTimeoutStringWhenNoResult() {
	long now = System.currentTimeMillis();
	String result = holder.getResult();
	long after = System.currentTimeMillis();
	assertNotNull(result);
	assertEquals(CommandResultHolder.CMD_TIMED_OUT_MSG, result);
	assertTrue(after - now >= (cmdTimeout*999)); // at least timeout seconds
  }
	
  @Test public void getCommandGeneratesNullMsgWhenPoisoned() throws Throwable {
	TrackableRunnable internalGetter = new TrackableRunnable() {
		@Override
		public Object go() throws Throwable {
			String result = holder.getResult();
	        log.debug(Thread.currentThread().getName() + " got result: " + result);
	        return result;
		}
	};
	String name = "launching getter";
    TrackableThread t = new TrackableThread(internalGetter, name);
    t.start();
    holder.poisonPollers();
    assertEquals(CommandResultHolder.CMD_NULL_RESULT_MSG,
	         t.getResult());
  }

  @Test public void simpleSingleThreaded() throws Throwable {
    injectContent(testResult, true);
    expectContent(testResult);
    assertTrue(holder.isEmpty()); // command got picked up.
  }

  private void injectContent(String content, boolean expected) throws Throwable {
	boolean actual = holder.putResult(content);
	assertEquals(content + "result got sent", expected, actual);
  }

  private void expectContent(String expected) throws Throwable {
    String actual = holder.getResult();
    assertEquals(expected + " result retrieved", expected, actual);
  }
  
}
