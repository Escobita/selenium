package org.openqa.selenium.chrome;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.internal.CircularOutputStream;
import org.openqa.selenium.remote.internal.SubProcess;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class ChromeBinary {
  
  private static final String CHROME_LOG_FILE_PROPERTY = "webdriver.chrome.logFile";
  private static final int BACKOFF_INTERVAL = 2500;

  private volatile int linearBackoffCoefficient = 1;

  private final ChromeProfile profile;
  private final ChromeExtension extension;
  private final int port;
  private final SubProcess chromeProcess;

  /**
   * @param profile The Chrome profile to use.
   * @param extension The extension to launch Chrome with.
   * @throws WebDriverException If an error occurs locating the Chrome executable.
   * @see ChromeBinary(ChromeProfile, ChromeExtension, int)
   */
  public ChromeBinary(ChromeProfile profile, ChromeExtension extension) {
    this(profile, extension, 0);
  }

  /**
   * Creates a new instance for managing an instance of Chrome using the given
   * {@code profile} and {@code extension}.
   *
   * @param profile The Chrome profile to use.
   * @param extension The extension to launch Chrome with.
   * @param port Which port to start Chrome on, or 0 for any free port.
   * @throws WebDriverException If an error occurs locating the Chrome executable.
   */
  public ChromeBinary(ChromeProfile profile, ChromeExtension extension, int port) {
    this.profile = profile;
    this.extension = extension;
    this.port = port == 0 ? findFreePort() : port;

    String chromeFile;
    try {
      chromeFile = getChromeFile();
    } catch (IOException e) {
      throw new WebDriverException(e);
    }

    ProcessBuilder builder = new ProcessBuilder(
          chromeFile,
          "--user-data-dir=" + profile.getDirectory().getAbsolutePath(),
          "--load-extension=" + extension.getDirectory().getAbsolutePath(),
          "--activate-on-launch",
          "--homepage=about:blank",
          "--no-first-run",
          "--disable-hang-monitor",
          "--disable-popup-blocking",
          "--disable-prompt-on-repost",
          "--no-default-browser-check",
          String.format("http://localhost:%d/chromeCommandExecutor", this.port));

    File logFile = getLogFile();
    this.chromeProcess = logFile == null
        ? new SubProcess(builder)
        : new SubProcess(builder, new CircularOutputStream(logFile));
  }

  private static File getLogFile() {
    String logFile = System.getProperty(CHROME_LOG_FILE_PROPERTY);
    return logFile == null ? null : new File(logFile);
  }

  private static int findFreePort() {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(0);
      return serverSocket.getLocalPort();
    } catch (IOException e) {
      throw new WebDriverException(e);
    } finally {
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException ignored) {
          // Oh well
        }
      }
    }
  }

  public ChromeProfile getProfile() {
    return profile;
  }

  public ChromeExtension getExtension() {
    return extension;
  }

  public int getPort() {
    return port;
  }

  /**
   * Starts the Chrome process for WebDriver.
   */
  public void start() {
    chromeProcess.launch();
    try {
      Thread.sleep(BACKOFF_INTERVAL * linearBackoffCoefficient);
    } catch (InterruptedException e) {
      //Nothing sane to do here
    }
  }

  /**
   * @return Whether the Chrome process managed by this instance is still
   *     running.
   */
  public boolean isRunning() {
    return chromeProcess.isRunning();
  }

  /**
   * Kills the Chrome process managed by this instance.
   */
  public void kill() {
    chromeProcess.shutdown();
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
