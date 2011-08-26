package org.openqa.grid.internal;
/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kristian Rosenvold
 */
public class TestThreadCounter {

  private final AtomicInteger started = new AtomicInteger();
  private final AtomicInteger exceptions = new AtomicInteger();
  private final AtomicInteger completed = new AtomicInteger();

  public Thread start(Runnable runnable) {
    final RunnableWrapper wrapper = new RunnableWrapper(runnable);
    final Thread thread = new Thread(wrapper);
    thread.start();
    return thread;
  }


  final class RunnableWrapper extends Thread {
    private final Runnable target;

    RunnableWrapper(Runnable target) {
      this.target = target;
    }

    public void run() {
      started.incrementAndGet();
      try {
        target.run();
      } catch (RuntimeException t) {
        exceptions.incrementAndGet();
        throw t;
      } finally {
        completed.incrementAndGet();
      }
    }
  }

  public void waitUntilDone(int done) throws InterruptedException {
    int i = 0;
    while (done != completed.get() && i++ < 20){
      if (i > 20) throw new RuntimeException("Time out waiting for completion");
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException( e);
      }
    }
  }


  public void waitUntilStarted(int num) throws InterruptedException {
    int i = 0;
    while (num != started.get() && i++ < 20){
      if (i > 20) throw new RuntimeException("Time out waiting for completion");
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException( e);
      }
    }
  }

}
