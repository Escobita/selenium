package org.openqa.selenium.safari;

import com.google.common.collect.Lists;
import org.openqa.selenium.Platform;
import org.openqa.selenium.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kurniady
 * Date: 1/12/11
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafariBinary {

  private final File pathToSafariBinary;

  private Process process;

  public SafariBinary(File pathToSafariBinary) {
    this.pathToSafariBinary = pathToSafariBinary;
  }

  public SafariBinary() {
    this(lookupSafariBinary());
  }

  private File makeDummyFile(int port) throws IOException {
    // TODO(kurniady): make this happen in a tmpdir, and make this work on Windows too
    File file = new File("LAUNCHINGSAFARIDRIVERONPORT_" + port + ".html");
    PrintWriter writer = new PrintWriter(file);
    writer.print("<html><head><meta http-equiv=\"refresh\" content=\"0;url=http://localhost:" +
        port + "/init_webdriver\" /></head></html>");
    writer.close();
    file.deleteOnExit();
    return file;
  }

  public void start(int port) throws IOException {
    final File dummyFile = makeDummyFile(port);

    List<String> commands = Lists.newArrayList();
    commands.add(pathToSafariBinary.getPath());
    commands.add(dummyFile.getAbsolutePath());

    ProcessBuilder builder = new ProcessBuilder(commands);

    process = builder.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        ProcessUtils.killProcess(process);
      }
    });
  }

  public void quit() {
    ProcessUtils.killProcess(process);
  }

  private static File lookupSafariBinary() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return new File("/Applications/Safari.app/Contents/MacOS/Safari");
    } else {
      throw new RuntimeException("SafariDriver is for OSX only at the moment.");
    }
  }
}
