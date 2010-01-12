package org.openqa.selenium;

/**
 * Thrown when attempting to add or delete a cookie under a different domain
 * than the current URL.
 *
 * @see org.openqa.selenium.WebDriver.Options#addCookie(Cookie)
 * @see org.openqa.selenium.WebDriver.Options#deleteCookie(Cookie)
 */
public class InvalidCookieDomainException extends WebDriverException {
  public InvalidCookieDomainException(String message) {
    super(message);
  }
}
