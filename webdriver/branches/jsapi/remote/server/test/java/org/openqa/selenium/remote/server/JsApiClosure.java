package org.openqa.selenium.remote.server;

/**
 * Forms a simple closure around an object.
 *
 * @param <T> The type of object held by the closure.
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class JsApiClosure<T> {

  private T value;

  public T get() {
    return value;
  }

  public void set(T newValue) {
    value = newValue;
  }
}
