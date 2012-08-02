/*
Copyright 2011 Selenium committers

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

package org.openqa.selenium.support.ui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An implementation of the {@link Wait} interface that may have its timeout and polling interval
 * configured on the fly.
 *
 * <p>
 * Each FluentWait instance defines the maximum amount of time to wait for a condition, as well as
 * the frequency with which to check the condition. Furthermore, the user may configure the wait to
 * ignore specific types of exceptions whilst waiting, such as
 * {@link org.openqa.selenium.NoSuchElementException NoSuchElementExceptions} when searching for an
 * element on the page.
 *
 * <p>
 * Sample usage: <code><pre>
 *   // Waiting 30 seconds for an element to be present on the page, checking
 *   // for its presence once every 5 seconds.
 *   Wait&lt;WebDriver&gt; wait = new FluentWait&lt;WebDriver&gt;(driver)
 *       .withTimeout(30, SECONDS)
 *       .pollingEvery(5, SECONDS)
 *       .ignoring(NoSuchElementException.class);
 *
 *   WebElement foo = wait.until(new Function&lt;WebDriver, WebElement&gt;() {
 *     public WebElement apply(WebDriver driver) {
 *       return driver.findElement(By.id("foo"));
 *     }
 *   });
 * </pre></code>
 *
 * <p>
 * <em>This class makes no thread safety guarantees.</em>
 *
 * @param <T> The input type for each condition used with this instance.
 */
public class FluentWait<T> implements Wait<T> {

  public static Duration FIVE_HUNDRED_MILLIS = new Duration(500, MILLISECONDS);

  private final T input;
  private final Clock clock;
  private final Sleeper sleeper;

  private Duration timeout = FIVE_HUNDRED_MILLIS;
  private Duration interval = FIVE_HUNDRED_MILLIS;
  private String message = null;

  private List<Class<? extends Throwable>> ignoredExceptions = Lists.newLinkedList();

  /**
   * @param input The input value to pass to the evaluated conditions.
   */
  public FluentWait(T input) {
    this(input, new SystemClock(), Sleeper.SYSTEM_SLEEPER);
  }

  /**
   * @param input The input value to pass to the evaluated conditions.
   * @param clock The clock to use when measuring the timeout.
   * @param sleeper Used to put the thread to sleep between evaluation loops.
   */
  public FluentWait(T input, Clock clock, Sleeper sleeper) {
    this.input = checkNotNull(input);
    this.clock = checkNotNull(clock);
    this.sleeper = checkNotNull(sleeper);
  }

  /**
   * Sets how long to wait for the evaluated condition to be true. The default timeout is
   * {@link #FIVE_HUNDRED_MILLIS}.
   *
   * @param duration The timeout duration.
   * @param unit The unit of time.
   * @return A self reference.
   */
  public FluentWait<T> withTimeout(long duration, TimeUnit unit) {
    this.timeout = new Duration(duration, unit);
    return this;
  }

  /**
   * Sets the message to be displayed when time expires.
   *
   * @param message to be appended to default.
   * @return A self reference.
   */
  public FluentWait<T> withMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * Sets how often the condition should be evaluated.
   *
   * <p>
   * In reality, the interval may be greater as the cost of actually evaluating a condition function
   * is not factored in. The default polling interval is {@link #FIVE_HUNDRED_MILLIS}.
   *
   * @param duration The timeout duration.
   * @param unit The unit of time.
   * @return A self reference.
   */
  public FluentWait<T> pollingEvery(long duration, TimeUnit unit) {
    this.interval = new Duration(duration, unit);
    return this;
  }

  /**
   * Configures this instance to ignore specific types of exceptions while waiting for a condition.
   * Any exceptions not whitelisted will be allowed to propagate, terminating the wait.
   *
   * @param types The types of exceptions to ignore.
   * @return A self reference.
   */
  public FluentWait<T> ignoreAll(Collection<Class<? extends Throwable>> types) {
    ignoredExceptions.addAll(types);
    return this;
  }

  /**
   * @see #ignoreAll(Collection)
   */
  public FluentWait<T> ignoring(Class<? extends Throwable> exceptionType) {
    return this.ignoreAll(ImmutableList.<Class<? extends Throwable>>of(exceptionType));
  }

  /**
   * @see #ignoreAll(Collection)
   */
  public FluentWait<T> ignoring(Class<? extends Throwable> firstType,
                                Class<? extends Throwable> secondType) {

    return this.ignoreAll(ImmutableList.<Class<? extends Throwable>>of(firstType, secondType));
  }

  /**
   * Repeatedly applies this instance's input value to the given predicate until the timeout expires
   * or the predicate evaluates to true.
   *
   * @param isTrue The predicate to wait on.
   * @throws TimeoutException If the timeout expires.
   */
  public void until(final Predicate<T> isTrue) {
    until(new Function<T, Boolean>() {
      public Boolean apply(T input) {
        return isTrue.apply(input);
      }
    });
  }

  /**
   * Repeatedly applies this instance's input value to the given function until one of the following
   * occurs:
   * <ol>
   * <li>the function returns neither null nor false,</li>
   * <li>the function throws an unignored exception,</li>
   * <li>the timeout expires,
   * <li>
   * <li>the current thread is interrupted</li>
   * </ol>
   *
   * @param isTrue the parameter to pass to the {@link ExpectedCondition}
   * @param <V> The function's expected return type.
   * @return The functions' return value if the function returned something different
   *         from null or false before the timeout expired.
   * @throws TimeoutException If the timeout expires.
   */
  public <V> V until(Function<? super T, V> isTrue) {
    long end = clock.laterBy(timeout.in(MILLISECONDS));
    Throwable lastException = null;
    while (true) {
      try {
        V value = isTrue.apply(input);
        if (value != null && Boolean.class.equals(value.getClass())) {
          if (Boolean.TRUE.equals(value)) {
            return value;
          }
        } else if (value != null) {
          return value;
        }
      } catch (Throwable e) {
        lastException = propagateIfNotIngored(e);
      }

      // Check the timeout after evaluating the function to ensure conditions
      // with a zero timeout can succeed.
      if (!clock.isNowBefore(end)) {
        String toAppend = message == null ?
            " waiting for " + isTrue.toString() : ": " + message;

        String timeoutMessage = String.format("Timed out after %d seconds%s",
            timeout.in(SECONDS), toAppend);
        if (lastException instanceof RuntimeException || lastException == null) {
          throw timeoutException(timeoutMessage, (RuntimeException) lastException);
        }
        throw timeoutException(timeoutMessage, lastException);
      }

      try {
        sleeper.sleep(interval);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new WebDriverException(e);
      }
    }
  }

  private Throwable propagateIfNotIngored(Throwable e) {
    for (Class<? extends Throwable> ignoredException : ignoredExceptions) {
      if (ignoredException.isInstance(e)) {
        return e;
      }
    }
    throw Throwables.propagate(e);
  }

  /**
   * Throws a timeout exception. This method may be overridden to throw an exception that is
   * idiomatic for a particular test infrastructure, such as an AssertionError in JUnit4.
   *
   * @param message The timeout message.
   * @param lastException The last exception to be thrown and subsequently suppressed while waiting
   *        on a function.
   * @return Nothing will ever be returned; this return type is only specified as a convenience.
   */
  protected RuntimeException timeoutException(String message, Throwable lastException) {
    throw new TimeoutException(message, lastException);
  }

  /**
   * Kept for backwards compatibility. New code, in particular code using {@link #ignoring(Class)}
   * with {@link Throwable}s should override {@link #timeoutException(String, Throwable)}.
   * @param message The timeout message.
   * @param lastException The last exception to be thrown and subsequently suppressed while waiting
   *        on a function.
   * @return Nothing will ever be returned; this return type is only specified as a convenience.
   * @deprecated Use {@link #timeoutException(String, Throwable)}. This method is scheduled for
   *             removal in the Selenium 2.26 release.
   */
  @Deprecated
  protected RuntimeException timeoutException(String message, RuntimeException lastException) {
    throw timeoutException(message, (Throwable) lastException);
  }
}
