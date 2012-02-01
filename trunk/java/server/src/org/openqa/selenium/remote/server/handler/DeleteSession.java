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

// Copyright 2008 Google Inc.  All Rights Reserved.

package org.openqa.selenium.remote.server.handler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.server.Session;
import org.openqa.selenium.remote.server.rest.ResultType;
import org.openqa.selenium.server.log.LoggingManager;
import org.openqa.selenium.server.log.PerSessionLogHandler;

/**
 * Handles the browser side of the delete. Removing from DriverSessions happens outside this class.
 */
public class DeleteSession extends WebDriverHandler {

  public DeleteSession(Session session) {
    super(session);
  }

  public ResultType call() throws Exception {

    WebDriver driver = getDriver();
    if (driver == null) {
      return ResultType.SUCCESS;
    }

    driver.quit();

    // Yes, this is funky. See javadocs on PerSessionLogHandler#clearThreadTempLogs for details.
    final PerSessionLogHandler logHandler = LoggingManager.perSessionLogHandler();
      /*
          We may be storing logging information on 2 different threads, the servlet container
          thread and the thread executing commands
          All this ugliness would go away if we just handled create and delete of sessions fully
          inside ResultConfig because then we could avoid switching threads and there will
          not be logevents that do not have a session present
          Additionally; if we ever get non-session bound logging here, it will come in
          the incorrect order. But that should only happen on create/delete, right ?
       */
    logHandler.transferThreadTempLogsToSessionLogs(getSessionId());
    return ResultType.SUCCESS;
  }

  @Override
  public String toString() {
    return String.format("[delete session: %s]", getRealSessionId());
  }
}
