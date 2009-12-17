package org.openqa.selenium.internal.selenesedriver;

import com.thoughtworks.selenium.Selenium;

import java.util.Map;

public interface SeleneseFunction<T> {
  T apply(Selenium selenium, Map<String, ?> parameters);
}
