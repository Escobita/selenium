package org.openqa.grid.internal;

import static org.openqa.grid.common.RegistrationRequest.APP;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.grid.internal.mock.MockedNewSessionRequestHandler;
import org.openqa.grid.internal.mock.MockedRequestHandler;
import org.openqa.grid.web.servlet.handler.RequestType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class SmokeTest {
  private static Registry registry;

  private static Map<String, Object> ie = new HashMap<String, Object>();
  private static Map<String, Object> ff = new HashMap<String, Object>();

  private static RemoteProxy p1;
  private static RemoteProxy p2;

  private static final int MAX = 10;

  private static volatile int ran = 0;

  /**
   * create a hub with 1 IE and 1 FF
   */
  @BeforeClass
  public static void setup() {
    registry = Registry.newInstance();
    ie.put(APP, "IE");
    ff.put(APP, "FF");

    p1 = RemoteProxyFactory.getNewBasicRemoteProxy(ie, "http://machine1:4444", registry);
    p2 = RemoteProxyFactory.getNewBasicRemoteProxy(ff, "http://machine2:4444", registry);
    registry.add(p1);
    registry.add(p2);

  }

  private synchronized static void inc() {
    ran++;
  }


  @Test(timeout = 10000)
  public void method() throws InterruptedException {

    final List<TestSession> sessions = new CopyOnWriteArrayList<TestSession>();

    for (int i = 0; i < MAX; i++) {
      new Thread(new Runnable() {
        public void run() {
          MockedRequestHandler newSessionRequest = new MockedNewSessionRequestHandler(registry, ie);
          newSessionRequest.process();
          TestSession session = newSessionRequest.getTestSession();
          inc();
          sessions.add(session);
        }
      }).start();
    }

    for (int i = 0; i < MAX; i++) {
      new Thread(new Runnable() {
        public void run() {
          MockedRequestHandler newSessionRequest = new MockedNewSessionRequestHandler(registry, ff);
          newSessionRequest.process();
          TestSession session = newSessionRequest.getTestSession();
          inc();
          sessions.add(session);
        }
      }).start();
    }

    // 2 run on the hub.
    while (registry.getActiveSessions().size() != 2) {
      Thread.sleep(50);
    }

    // while the rest waits.
    while (registry.getNewSessionRequestCount() != 18) {
      Thread.sleep(50);
    }

    int stopped = 0;
    // all the tests ran via the registry.
    while (stopped < 2* MAX) {
      for (TestSession session : sessions) {
        MockedRequestHandler stopSessionRequest = new MockedRequestHandler(registry);
        stopSessionRequest.setSession(session);
        stopSessionRequest.setRequestType(RequestType.STOP_SESSION);
        stopSessionRequest.process();
        stopped++;
        sessions.remove(session);
      }
    }
    // all request got the stop session request
    Assert.assertEquals(2 * MAX, stopped);
    // nothing left waiting
    Assert.assertEquals(0, registry.getNewSessionRequestCount());
    
    // nothing active. Waiting in case a stopSessionRequest.process() isn't finish. It's async.
    while(registry.getActiveSessions().size()!=0){
    	 Thread.sleep(10);
    }
    Assert.assertEquals(0, registry.getActiveSessions().size());
    
    // everything was started.
    Assert.assertEquals(2 * MAX, ran);

  }

  @AfterClass
  public static void teardown() {
    registry.stop();
  }
}
