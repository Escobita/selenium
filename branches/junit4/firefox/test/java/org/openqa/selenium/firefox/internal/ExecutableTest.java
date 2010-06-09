package org.openqa.selenium.firefox.internal;



import org.junit.Test; import static org.junit.Assert.*;

import java.io.File;

public class ExecutableTest {

  @Test
  public void testEnvironmentDiscovery() {
    Executable env = new Executable(null);
    File exe = env.getFile();
    assertNotNull(exe);
    assertFalse(exe.isDirectory());
  }
}
