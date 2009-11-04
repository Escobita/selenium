// Copyright 2009 Google Inc.  All Rights Reserved.

package org.openqa.selenium.chrome;

import org.openqa.selenium.WebDriverException;

/**
 * Exception used to signal that Chrome has unexpectedly died.
 * 
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class DeadChromeException extends WebDriverException {
  public DeadChromeException() {
  }

  public DeadChromeException(String message) {
    super(message);
  }

  public DeadChromeException(Throwable cause) {
    super(cause);
  }

  public DeadChromeException(String message, Throwable cause) {
    super(message, cause);
  }
}
