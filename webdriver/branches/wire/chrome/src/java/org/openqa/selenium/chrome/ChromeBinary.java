/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium.chrome;

import com.google.common.annotations.VisibleForTesting;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ChromeBinary {

  private static final int BACKOFF_INTERVAL = 2500;

  private static int linearBackoffCoefficient = 1;

  private final ChromeProfile profile;
  private final ChromeExtension extension;
  private final ExecutorService executorService;

  private Process chromeProcess = null;
  private FutureTask<Integer> babySitter = null;

  /**
   * @param profile The profile to use.
   * @param extension The extension to use.
   */
  public ChromeBinary(ChromeProfile profile, ChromeExtension extension) {
    this(profile, extension, Executors.newSingleThreadExecutor());
  }

  /**
   * @param profile The profile to use.
   * @param extension The extension to use.
   * @param executorService The serviced used to monitor whether the Chrome
   *     subprocess unexpectedly dies.
   */
  private ChromeBinary(ChromeProfile profile, ChromeExtension extension,
                       ExecutorService executorService) {
    this.profile = profile;
    this.extension = extension;
    this.executorService = executorService;
  }

  /**
   * Starts the Chrome process for WebDriver.
   *
   * @throws IOException wrapped in WebDriverException if process couldn't be
   *     started.
   */
  public void start() throws IOException {
    try {
      chromeProcess = new ProcessBuilder(
          getChromeFile(),
          "--user-data-dir=" + profile.getDirectory().getAbsolutePath(),
          "--load-extension=" + extension.getDirectory().getAbsolutePath(),
          "--activate-on-launch",
          "--no-first-run",
          "--disable-hang-monitor",
          "--disable-popup-blocking",
          "--disable-prompt-on-repost",
          "about:blank")
          .start();

      // Create a baby sitter to monitor whether Chrome dies before we manually
      // kill it.
      babySitter = createBabySitter(chromeProcess);
      executorService.execute(babySitter);

    } catch (IOException e) {
      throw new WebDriverException(e);
    }
    try {
      Thread.sleep(BACKOFF_INTERVAL * linearBackoffCoefficient);
    } catch (InterruptedException e) {
      //Nothing sane to do here
    }
  }

  private static FutureTask<Integer> createBabySitter(final Process child) {
    return new FutureTask<Integer>(new Callable<Integer>() {
      public Integer call() throws Exception {
        return child.waitFor();
      }
    });
  }

  public void kill() {
    if (babySitter != null) {
      babySitter.cancel(true);
      babySitter = null;
    }

    if (chromeProcess != null) {
      chromeProcess.destroy();
      chromeProcess = null;
    }
  }

  /**
   * Tests whether the Chrome process is alive and running. Will return
   * {@code false} if Chrome was never {@link #start() started}, or if
   * Chrome was {@link #kill() killed}. Note that this only tests if the
   * Chrome process is still running, it does not test whether it is
   * responsive.
   *
   * @return Whether the Chrome process is alive.
   */
  public boolean isAlive() {
    return babySitter != null && !babySitter.isDone();
  }

  @VisibleForTesting Process getChromeProcess() {
    return chromeProcess;
  }

  public void incrementBackoffBy(int diff) {
    linearBackoffCoefficient += diff;
  }

  /**
   * Locates the Chrome executable on the current platform.
   * First looks in the webdriver.chrome.bin property, then searches
   * through the default expected locations.
   * @return chrome.exe
   * @throws IOException if file could not be found/accessed
   */
  protected String getChromeFile() throws IOException {
    File chromeFile = null;
    String chromeFileSystemProperty = System.getProperty(
        "webdriver.chrome.bin");
    if (chromeFileSystemProperty != null) {
      chromeFile = new File(chromeFileSystemProperty);
    } else {
      StringBuilder chromeFileString = new StringBuilder();
      if (Platform.getCurrent().is(Platform.XP)) {
        chromeFileString.append(System.getProperty("user.home"))
                        .append("\\Local Settings\\Application Data\\")
                        .append("Google\\Chrome\\Application\\chrome.exe");
      } else if (Platform.getCurrent().is(Platform.VISTA)) {
        //HOPEFULLY this is somewhat consistent...
        chromeFileString.append(System.getProperty("java.io.tmpdir"))
                        .append("..\\")
                        .append("Google\\Chrome\\Application\\chrome.exe");
      } else if (Platform.getCurrent().is(Platform.UNIX)) {
        chromeFileString.append("/usr/bin/google-chrome");
      } else if (Platform.getCurrent().is(Platform.MAC)) {
        String[] paths = new String[] {
          "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
          "/Users/" + System.getProperty("user.name") +
              "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"};
        boolean foundPath = false;
        for (String path : paths) {
          File binary = new File(path);
          if (binary.exists()) {
            chromeFileString.append(binary.getCanonicalFile());
            foundPath = true;
            break;
          }
        }
        if (!foundPath) {
          throw new WebDriverException("Couldn't locate Chrome.  " +
              "Set webdriver.chrome.bin");
        }
      } else {
        throw new WebDriverException("Unsupported operating system.  " +
            "Could not locate Chrome.  Set webdriver.chrome.bin");
      }
      chromeFile = new File(chromeFileString.toString());
    }
    return chromeFile.getCanonicalFile().toString();
  }
}
