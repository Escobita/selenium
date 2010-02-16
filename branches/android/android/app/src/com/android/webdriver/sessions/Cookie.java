/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.webdriver.sessions;

/**
 * Class that represents a single cookie.
 * {@link SessionCookieManager} maintains a collection of cookies available
 * per in the Android browser. 
 */
public class Cookie {

  /**
   * Constructor that creates the cookie based on the provided data.
   * 
   * @param domain Cookie domain.
   * @param name Cookie name.
   * @param value Cookie value.
   */
  public Cookie(String domain, String name, String value) {
    mDomain = domain;
    mName = name;
    mValue = value;
  }

  /**
   * Returns cookie domain name.
   * 
   * @return Cookie domain name string.
   */
  public String getDomain() {
    return mDomain;
  }

  /**
   * Returns cookie name.
   * 
   * @return Name of the cookie.
   */
  public String getName() {
    return mName;
  }

  /**
   * Returns cookie value.
   * 
   * @return Value of the cookie.
   */
  public String getValue() {
    return mValue;
  }
  
  // Private members
  String mDomain, mName, mValue;
}
