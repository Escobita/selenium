package org.openqa.grid.internal;

/*
Copyright 2007-2011 WebDriver committers

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

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

/**
 * A set of RemoteProxies.
 *
 * Obeys the iteration guarantees of CopyOnWriteArraySet
 */
@ThreadSafe
public class ProxySet implements Iterable<RemoteProxy> {

  private final Set<RemoteProxy> proxies = new CopyOnWriteArraySet<RemoteProxy>();

  private static final Logger log = Logger.getLogger(ProxySet.class.getName());


  /**
   * killing the timeout detection threads.
   */
  public void teardown() {
    for (RemoteProxy proxy : proxies) {
      proxy.teardown();
    }
  }

  public boolean hasCapability(Map<String, Object> requestedCapability) {
    for (RemoteProxy proxy : proxies) {
      if (proxy.hasCapability(requestedCapability)) {
        return true;
      }
    }
    return false;
  }

  private void forceRelease(RemoteProxy proxy) {
    // Find the original proxy. While the supplied one is logically equivalent, it may be a fresh object with
    // an empty TestSlot list, which doesn't figure into the proxy equivalence check.  Since we want to free up
    // those test sessions, we need to operate on that original object.
    for (RemoteProxy p : proxies) {
      if (p.equals(proxy)) {
        proxies.remove(p);

        for (TestSlot slot : p.getTestSlots()) {
          slot.forceRelease();
        }
      }
    }
  }

  public void removeIfPresent(RemoteProxy proxy){
    if (proxies.contains(proxy)) {
      log.warning(String.format(
          "Proxy '%s' was previously registered.  Cleaning up any stale test sessions.", proxy));

      forceRelease(proxy);
    }
  }

  public void add(RemoteProxy proxy) {
    proxies.add(proxy);
  }

  public boolean contains(RemoteProxy o) {
    return proxies.contains(o);
  }

  public List<RemoteProxy> getBusyProxies() {
    List<RemoteProxy> res = new ArrayList<RemoteProxy>();
    for (RemoteProxy proxy : proxies) {
      if (proxy.isBusy()) {
        res.add(proxy);
      }
    }
    return res;
  }

  public boolean isEmpty() {
    return proxies.isEmpty();
  }

  public List<RemoteProxy> getSorted() {
    List<RemoteProxy> sorted = new ArrayList<RemoteProxy>(proxies);
    Collections.sort(sorted);
    return sorted;
  }

  public Iterator<RemoteProxy> iterator() {
    return proxies.iterator();
  }

  public int size() {
    return proxies.size();
  }
}
