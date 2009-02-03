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

package org.openqa.selenium.firefox.internal;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.Command;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxLauncher;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class NewProfileExtensionConnection extends AbstractExtensionConnection {
  private static long TIMEOUT = 45;
  private static TimeUnit TIMEOUT_UNITS = TimeUnit.SECONDS;
  private FirefoxBinary process;
  private FirefoxProfile profile;

  public NewProfileExtensionConnection(Lock lock, FirefoxBinary binary, FirefoxProfile profile, String host) throws IOException {
    this.profile = profile;
    lock.lock(TIMEOUT, TIMEOUT_UNITS);
    try {
      int portToUse = determineNextFreePort(host, profile.getPort());

      process = new FirefoxLauncher(binary).startProfile(profile, portToUse);

      setAddress(host, portToUse);

      connectToBrowser(TIMEOUT_UNITS.toMillis(TIMEOUT));
    } finally {
      lock.unlock();
    }
  }


  protected int determineNextFreePort(String host, int port) throws IOException {
    // Attempt to connect to the given port on the host
    // If we can't connect, then we're good to use it
    int newport;

    for (newport = port; newport < port + 200; newport++) {
      Socket socket = new Socket();
      InetSocketAddress address = new InetSocketAddress(host, newport);

      try {
        socket.bind(address);
        return newport;
      } catch (BindException e) {
        // Port is already bound. Skip it and continue
      } finally {
        socket.close();
      }
    }

    throw new WebDriverException(String.format("Cannot find free port in the range %d to %d ", port, newport));
  }

  public void quit() {
        try {
            sendMessageAndWaitForResponse(WebDriverException.class, new Command(null, "quit"));
        } catch (Exception e) {
            // this is expected
        }

        if (Platform.getCurrent().is(Platform.WINDOWS)) {
        	quitOnWindows();
        } else {
            quitOnOtherPlatforms();
        }

        profile.clean();    
    }

  private void quitOnOtherPlatforms() {
    // Wait for process to die and return
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      throw new WebDriverException(e);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }

  private void quitOnWindows() {
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      throw new WebDriverException(e);
    }
  }
}