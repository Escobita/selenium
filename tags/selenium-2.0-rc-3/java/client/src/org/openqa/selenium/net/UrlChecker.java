package org.openqa.selenium.net;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Polls a URL until a HTTP 200 response is received.
 */
public class UrlChecker {

  private static final Logger log = Logger.getLogger(UrlChecker.class.getName());

  private static final int CONNECT_TIMEOUT_MS = 500;
  private static final int READ_TIMEOUT_MS = 1000;
  private static final long POLL_INTERVAL_MS = 500;

  private final TimeLimiter timeLimiter;

  public UrlChecker() {
    this(createSimpleTimeLimiter());
  }

  @VisibleForTesting
  UrlChecker(TimeLimiter timeLimiter) {
    this.timeLimiter = timeLimiter;
  }

  private static TimeLimiter createSimpleTimeLimiter() {
    ExecutorService executor = Executors.newFixedThreadPool(1);
    return new SimpleTimeLimiter(executor);
  }

  public void waitUntilAvailable(long timeout, TimeUnit unit, final URL... urls)
      throws TimeoutException {
    long start = System.nanoTime();
    log.info("Waiting for " + urls);
    try {
      timeLimiter.callWithTimeout(new Callable<Void>() {
        public Void call() throws InterruptedException {
          HttpURLConnection connection = null;

          while (true) {
            for (URL url : urls) {
              try {
                log.info("Polling " + url);
                connection = connectToUrl(url);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                  return null;
                }
              } catch (IOException e) {
                // Ok, try again.
              } finally {
                if (connection != null) {
                  connection.disconnect();
                }
              }
            }
            MILLISECONDS.sleep(POLL_INTERVAL_MS);
          }
        }
      }, timeout, unit, true);
    } catch (UncheckedTimeoutException e) {
      throw new TimeoutException(String.format(
          "Timed out waiting for %s to be available after %d ms",
          urls, MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS)), e);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public void waitUntilUnavailable(long timeout, TimeUnit unit, final URL url)
      throws TimeoutException {
    long start = System.nanoTime();
    log.info("Waiting for " + url);
    try {
      timeLimiter.callWithTimeout(new Callable<Void>() {
        public Void call() throws InterruptedException {
          HttpURLConnection connection = null;

          while (true) {
            try {
              log.info("Polling " + url);
              connection = connectToUrl(url);
              if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
              }
            } catch (IOException e) {
              return null;
            } finally {
              if (connection != null) {
                connection.disconnect();
              }
            }

            MILLISECONDS.sleep(POLL_INTERVAL_MS);
          }
        }
      }, timeout, unit, true);
    } catch (UncheckedTimeoutException e) {
      throw new TimeoutException(String.format(
          "Timed out waiting for %s to become unavailable after %d ms",
          url, MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS)), e);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private HttpURLConnection connectToUrl(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
    connection.setReadTimeout(READ_TIMEOUT_MS);
    connection.connect();
    return connection;
  }

  public static class TimeoutException extends Exception {
    public TimeoutException(String s, Throwable throwable) {
      super(s, throwable);
    }
  }
}
