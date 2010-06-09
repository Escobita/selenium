/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium;


import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.internal.ReturnedCookie;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CookieTest {

  @Test public void CanCreateAWellFormedCookie() {
    new ReturnedCookie("Fish", "cod", "", "", null, false, null);
  }

  @Test public void shouldThrowAnExceptionWhenSemiColonExistsInTheCookieAttribute() {
    try {
      new ReturnedCookie("hi;hi", "value", null, null, null, false, null);
      fail();
    } catch (IllegalArgumentException e) {
      //Expected
    }
  }

  @Test public void shouldThrowAnExceptionTheNameIsNull() {
    try {
      new ReturnedCookie(null, "value", null, null, null, false, null);
      fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  @Test public void CookiesShouldAllowSecureToBeSet() {
    Cookie cookie = new ReturnedCookie("name", "value", "", "/", new Date(), true, null);
    assertTrue(cookie.isSecure());
  }
}
