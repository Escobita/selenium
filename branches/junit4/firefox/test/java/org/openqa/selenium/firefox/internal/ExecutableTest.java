package org.openqa.selenium.firefox.internal;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ExecutableTest {

  @Test public void environmentDiscovery() {
    Executable env = new Executable(null);
    File exe = env.getFile();
    assertNotNull(exe);
    assertFalse(exe.isDirectory());
  }
}
