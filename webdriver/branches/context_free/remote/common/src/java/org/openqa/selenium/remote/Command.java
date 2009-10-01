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

package org.openqa.selenium.remote;

public class Command {

  private SessionId sessionId;
  private String methodName;
  private Object[] parameters;

  public Command(SessionId sessionId, String methodName, Object... parameters) {
    this.sessionId = sessionId;
    this.methodName = methodName;
    this.parameters = parameters;
  }

  public SessionId getSessionId() {
    return sessionId;
  }

  public String getMethodName() {
    return methodName;
  }

  public Object[] getParameters() {
    return parameters;
  }
}
