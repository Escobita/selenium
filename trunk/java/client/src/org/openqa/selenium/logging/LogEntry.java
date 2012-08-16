/*
Copyright 2007-2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

Portions copyright 2011 Software Freedom Conservancy

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

import com.google.common.collect.ImmutableMap;

import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

/**
 * Represents a single log statement.
 */
public class LogEntry {

  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  private final Level level;
  private final long timestamp;
  private final String message;

  /**
   * @param level     the severity of the log entry
   * @param timestamp UNIX Epoch timestamp at which this log entry was created
   * @param message ew  the log entry's message
   */
  public LogEntry(Level level, long timestamp, String message) {
    this.level = level;
    this.timestamp = timestamp;
    this.message = message;
  }

  /**
   * Gets the logging entry's severity.
   *
   * @return severity of log statement
   */
  public Level getLevel() {
    return level;
  }

  /**
   * Gets the timestamp of the log statement in seconds since UNIX Epoch.
   *
   * @return timestamp as UNIX Epoch
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Gets the log entry's message.
   *
   * @return the log statement
   */
  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return String.format("[%s] [%s] %s",
                         DATE_FORMAT.format(new Date(timestamp * 1000L)), level, message);
  }

  @SuppressWarnings("unused")
  public JSONObject toJson() {
    return new JSONObject(ImmutableMap.of(
        "timestamp", timestamp,
        "message", message,
        "level", level.getName()));
  }

}