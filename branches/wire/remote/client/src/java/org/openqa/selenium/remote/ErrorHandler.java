package org.openqa.selenium.remote;

import org.openqa.selenium.WebDriverException;

import static org.openqa.selenium.remote.ErrorCodes.SUCCESS;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps exceptions to status codes for sending over the wire.
 *
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class ErrorHandler {

  private final ErrorCodes errorCodes = new ErrorCodes();

  @SuppressWarnings({"unchecked"})
  public Response throwIfResponseFailed(Response response) throws RuntimeException {
    if (response.getStatus() == SUCCESS) {
      return response;
    }

    // This doesn't fit into the wire protocol spec.
    // TODO(jmleyba): Verify it never occurs and delete.
    if (response.getValue() instanceof StackTraceElement[]) {
      WebDriverException runtimeException = new WebDriverException();
      runtimeException.setStackTrace((StackTraceElement[]) response.getValue());
      throw runtimeException;
    }

    RuntimeException toThrow = null;
    Map rawErrorData;

    if (response.getValue() == null) {
      rawErrorData = new HashMap<String, Object>();
    } else {
      try {
        rawErrorData =  (Map) response.getValue();
      } catch (ClassCastException e) {
        throw new WebDriverException(String.valueOf(response.getValue()));
      }
    }

    try {
      String screenGrab = (String) rawErrorData.get("screen");
      String message = (String) rawErrorData.get("message");

      Class<? extends RuntimeException> exceptionType = null;
      if (rawErrorData.containsKey("class")) {
        String className = (String) rawErrorData.get("class");
        try {
          Class clazz = Class.forName(className);
          if (RuntimeException.class.isAssignableFrom(clazz)) {
            exceptionType = (Class<? extends RuntimeException>) clazz;
          }
        } catch (ClassNotFoundException ignored) {
          // Ok, fall-through to status code value.
        }
      }

      if (exceptionType == null) {
        exceptionType = errorCodes.getExceptionType(response.getStatus());
      }

      if (screenGrab != null) {
        try {
          Constructor<? extends RuntimeException> constructor =
              exceptionType.getConstructor(String.class, Throwable.class);
          toThrow = constructor.newInstance(message, new ScreenshotException(screenGrab));
        } catch (Exception e) {
          // Fine. Fall through.
        } catch (OutOfMemoryError e) {
          // It can happen sometimes. Fall through.
        }
      }

      if (toThrow == null) {
        try {
          Constructor<? extends RuntimeException> constructor =
              exceptionType.getConstructor(String.class);
          toThrow = constructor.newInstance(message);
        } catch (NoSuchMethodException e) {
          toThrow = new WebDriverException(message);
        } catch (InvocationTargetException e) {
          toThrow = new WebDriverException(message);
        } catch (InstantiationException e) {
          toThrow = new WebDriverException(message);
        } catch (IllegalAccessException e) {
          toThrow = new WebDriverException(message);
        }
      }

      List<Map> elements = (List<Map>) rawErrorData.get("stackTrace");
      if (elements != null) {
        StackTraceElement[] trace = new StackTraceElement[elements.size()];
        int lastInsert = 0;
        for (Map values : elements) {
          // I'm so sorry.
          Long lineNumber = (Long) values.get("lineNumber");
          if (lineNumber == null) {
            continue;
          }

          // Gracefully handle remote servers that don't (or can't) send back
          // complete stack trace info. At least some of this information should
          // be included...
          String className = values.containsKey("className")
              ? String.valueOf(values.get("className")) : "<class not specified>";
          String methodName = values.containsKey("methodName")
              ? String.valueOf(values.get("methodName")) : "<method not specified>";
          String fileName = values.containsKey("fileName")
              ? String.valueOf(values.get("fileName")) : "<file not specfied>";

          trace[lastInsert++] = new StackTraceElement(
              className, methodName, fileName, lineNumber.intValue());
          }

          if (lastInsert == elements.size()) {
          toThrow.setStackTrace(trace);
        }
      }
    } catch (Exception e) {
      toThrow = new WebDriverException(e);
    }

    throw toThrow;
  }

}
