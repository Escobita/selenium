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

package org.openqa.selenium.firefox;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.remote.DriverCommand;

public class Command {
    private final String sessionId;
    private final DriverCommand commandName;
    private final Map<String, ?> parameters;

    public Command(String sessionId, DriverCommand commandName) {
      this(sessionId, commandName, new HashMap<String, Object>());
    }

    public Command(String sessionId, DriverCommand commandName, Map<String, ?> parameters) {
        this.sessionId = sessionId;
        this.commandName = commandName;
        this.parameters = parameters;
    }


    public String getSessionId() {
      return sessionId;
    }

    public DriverCommand getCommandName() {
        return commandName;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }
}
