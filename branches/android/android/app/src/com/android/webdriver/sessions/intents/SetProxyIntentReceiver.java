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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SetProxyIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    String proxyServer = intent.getExtras().getString("Host");
    String proxyPort = intent.getExtras().getString("Port");
    
    // Set the device proxy
    System.getProperties().put("proxySet",
        proxyServer.length() > 0 ? "true" : "false");
    System.getProperties().put("proxyHost", proxyServer);
    System.getProperties().put("proxyPort", proxyPort);
  }
}
