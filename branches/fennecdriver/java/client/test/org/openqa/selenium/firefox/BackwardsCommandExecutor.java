package org.openqa.selenium.firefox;

import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.Response;

import java.io.IOException;

public class BackwardsCommandExecutor implements CommandExecutor {

  private LightweightServer server;

  public BackwardsCommandExecutor() {
    server = new LightweightServer();
    server.start();
  }

  public Response execute(Command command) throws IOException {
    server.addCommand(command);
    return server.getResponse();
  }

  public String getUrl() {
    return String.format("http://localhost:%d/xhr", server.getPort());
  }
}
