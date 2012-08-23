/*
Copyright 2012 Software Freedom Conservancy

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

package org.openqa.selenium.logging;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores and retrieves logs in-process (i.e. without any RPCs).
 */
public abstract class LocalLogs implements Logs {

  private static final LocalLogs NULL_LOGGER = new LocalLogs() {
    public LogEntries get(String logType) {
      return new LogEntries(ImmutableList.<LogEntry>of());
    }

    public Set<String> getAvailableLogTypes() {
      return ImmutableSet.of();
    }

    public void addEntry(String logType, LogEntry entry) {
    }
  };

  /**
   * Logger which doesn't do anything.
   */
  public static LocalLogs getNullLogger() {
    return NULL_LOGGER;
  }

  public static LocalLogs getStoringLoggerInstance(Set<String> logTypesToIgnore) {
    return new StoringLocalLogs(logTypesToIgnore);
  }

  public static LocalLogs getHandlerBasedLoggerInstance(LoggingHandler loggingHandler) {
    return new HandlerBasedLocalLogs(loggingHandler);
  }

  /**
   * See documentation of CompositeLocalLogs about the difference between the first
   * LocalLogs instance and the second one.
   * @param predefinedTypeLogger LocalLogs which pre-defines the log types it stores.
   * @param allTypesLogger LocalLogs which can store log entries for all log types.
   * @return
   */
  public static LocalLogs getCombinedLogsHolder(LocalLogs predefinedTypeLogger,
                                                LocalLogs allTypesLogger) {
    return new CompositeLocalLogs(predefinedTypeLogger, allTypesLogger);
  }

  protected LocalLogs() {
  }

  public abstract LogEntries get(String logType);

  public abstract void addEntry(String logType, LogEntry entry);
}