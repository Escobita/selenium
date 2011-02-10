package org.openqa.selenium.safari;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.*;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kurniady
 * Date: 1/12/11
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafariDriver extends RemoteWebDriver {

  private SafariBinary binary;
  private SafariConnection connection;

  private SafariDriver(DelegatingCommandExecutor executor) {
    super(executor, DesiredCapabilities.safari());
    executor.setDelegate(connection);
  }
  public SafariDriver() {
    this(new DelegatingCommandExecutor());
  }

  @Override
  protected void startClient() {
    try {
      DelegatingCommandExecutor executor = (DelegatingCommandExecutor) getCommandExecutor();

      connection = new SafariConnection();
      int port = connection.listen();

      binary = new SafariBinary();
      binary.start(port);

      connection.waitUntilConnected();
      executor.setDelegate(connection);

    } catch (IOException e) {
      throw new WebDriverException("Failed to start Safari", e);
    } catch (InterruptedException e) {
      throw new WebDriverException("Interrupted while starting Safari", e);
    }
  }

  @Override
  protected void stopClient() {
    binary.quit();
    connection.quit();
  }

  private SafariConnection createConnection() {
    connection = new SafariConnection();
    connection.listen();
    return connection;
  }

  private static class DelegatingCommandExecutor implements CommandExecutor {

    private CommandExecutor delegate;

    private void setDelegate(CommandExecutor delegate) {
      this.delegate = delegate;
    }

    public Response execute(Command command) throws IOException {
      if (delegate != null) {
        return delegate.execute(command);
      } else {
        throw new WebDriverException("Delegate not set yet.");
      }
    }
  }
}
