package org.openqa.grid.internal;

/*
Copyright 2011 WebDriver committers
Copyright 2011 Software Freedom Conservancy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import junit.framework.Assert;

import org.junit.Test;
import org.openqa.grid.internal.mock.MockedNewSessionRequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.openqa.grid.common.RegistrationRequest.APP;
import static org.openqa.grid.common.RegistrationRequest.MAX_INSTANCES;

public class GridShutdownTest {


  @Test(timeout = 500000)
  public void shutdown() throws InterruptedException {

    final Map<String, Object> ff = new HashMap<String, Object>();
    ff.put(APP, "FF");
    ff.put(MAX_INSTANCES, 1);

    final Registry registry = Registry.newInstance();

    RemoteProxy p1 =
        RemoteProxyFactory.getNewBasicRemoteProxy(ff, "http://machine1:4444", registry);
    registry.add(p1);
    registry.setThrowOnCapabilityNotPresent(true);

    MockedNewSessionRequestHandler newSessionRequest = new MockedNewSessionRequestHandler(registry, ff);
    newSessionRequest.process();

    final int before = getCurrentThreadCount();
    final CountDownLatch latch = new CountDownLatch(numRequests());
    List<Thread> threads = new ArrayList<Thread>();
    for (int i = 0; i < numRequests(); i++) {
      final Thread thread = new Thread(new Runnable() { // Thread safety reviewed
        public void run() {
          latch.countDown();
          MockedNewSessionRequestHandler
              newSessionRequest =
              new MockedNewSessionRequestHandler(registry, ff);
          newSessionRequest.process();
        }
      }, "TestThread" + i);
      threads.add( thread);
      thread.start();
    }
    latch.await();
    Assert.assertEquals(before + 5, getCurrentThreadCount());
    registry.stop();
    for (Thread thread : threads) {
      thread.join();
    }
    Assert.assertEquals(before , getCurrentThreadCount());
  }

  private int getCurrentThreadCount() {
    return Thread.currentThread().getThreadGroup().activeCount();
  }

  private int numRequests() {
    return 5;
  }

}
