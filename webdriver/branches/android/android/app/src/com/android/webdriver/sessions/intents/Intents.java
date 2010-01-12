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

package com.android.webdriver.sessions.intents;

public final class Intents {
	public static final String INTENT_NAVIGATE =
		new String("com.android.webdriver.intent.NAVIGATE");

	public static final String INTENT_ADDSESSION =
		new String("com.android.webdriver.intent.ADD_SESSION");

	public static final String INTENT_DELETESESSION =
		new String("com.android.webdriver.intent.DELETE_SESSION");
	
	public static final String INTENT_GETTITLE =
		new String("com.android.webdriver.intent.GET_TITLE");

	public static final String INTENT_DOACTION =
		new String("com.android.webdriver.intent.DO_ACTION");

    public static final String INTENT_SETPROXY =
      new String("com.android.webdriver.intent.SET_PROXY");

    public static final String INTENT_GETURL =
      new String("com.android.webdriver.intent.GET_URL");
    
    public static final String INTENT_COOKIES =
      new String("com.android.webdriver.intent.GET_COOKIES");
}
