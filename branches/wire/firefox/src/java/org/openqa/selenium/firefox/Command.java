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

public class Command {
    private final String sessionId;
    private final String elementId;
    private final String commandName;
    private final Object[] parameters;

    public Command(String sessionId, String commandName, Object... parameters) {
        this(sessionId, null, commandName, parameters);
    }

    public Command(String sessionId, String elementId, String commandName, Object... parameters) {
        this.sessionId = sessionId;
        this.elementId = elementId;
        this.commandName = commandName;
        this.parameters = parameters;
    }


    public String getSessionId() {
      return sessionId;
    }

    public String getElementId() {
        return elementId;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
