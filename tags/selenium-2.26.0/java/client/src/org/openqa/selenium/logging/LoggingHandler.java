/*
Copyright 2011 Software Freedom Conservatory.

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

import com.google.common.collect.Lists;

import org.openqa.selenium.logging.LogEntry;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A custom handler used to record log entries.
 *
 * This handler queues up log records as they come, up to MAX_RECORDS
 * (currently 1000) records.
 * If it reaches this capacity it will remove the older records
 * from the queue before adding the next one.
 */
public class LoggingHandler extends Handler {

  private static final int MAX_RECORDS = 1000;
  private LinkedList<LogEntry> records = Lists.newLinkedList();
  private static final LoggingHandler instance = new LoggingHandler();

  private LoggingHandler() {}

  public static LoggingHandler getInstance() {
    return instance;
  }

  /**
   *
   * @return an unmodifiable list of LogEntry.
   */
  public synchronized List<LogEntry> getRecords() {
    return Collections.unmodifiableList(records);
  }

  @Override
  public synchronized  void publish(LogRecord logRecord) {
    if (isLoggable(logRecord)) {
      if (records.size() > MAX_RECORDS) {
        records.remove();
      }
      records.add(new LogEntry(logRecord.getLevel(),
          logRecord.getMillis(),
          logRecord.getLoggerName() + " "
              + logRecord.getSourceClassName() + "." + logRecord.getSourceMethodName()
              + " " + logRecord.getMessage()));
    }
  }

  public void attachTo(Logger logger, Level level) {
    Handler[] handlers = logger.getHandlers();
    for (Handler handler : handlers) {
      if (handler == this) {
        // the handler has already been added
        return;
      }
    }
    setLevel(level);
    logger.addHandler(this);
  }

  @Override
  public void flush() {
    records = Lists.newLinkedList();
  }

  @Override
  public synchronized void close() throws SecurityException {
    records.clear();
  }
}
