/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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


package org.openqa.grid.web.servlet.beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.grid.internal.TestSlot;

public class SlotsLines {
  Map<MiniCapability, List<TestSlot>> slots = new HashMap<MiniCapability, List<TestSlot>>();


  public void add(TestSlot slot) {
    MiniCapability c = new MiniCapability(slot);
    List<TestSlot> l = slots.get(c);
    if (l == null) {
      l = new ArrayList<TestSlot>();
      slots.put(c, l);
    }
    l.add(slot);
  }

  public Set<MiniCapability> getLinesType() {
    return slots.keySet();
  }

  public List<TestSlot> getLine(MiniCapability cap) {
    return slots.get(cap);
  }
}
