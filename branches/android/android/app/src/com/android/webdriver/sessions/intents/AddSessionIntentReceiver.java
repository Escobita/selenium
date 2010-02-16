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

import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Intent receiver for handling addition of a new session.
 * Creates a new session with assistance of the session repository 
 * and returns session id as a result.
 */
public class AddSessionIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    // Extracting the WebDriver context from intent's arguments.
    String ctx = intent.getExtras().getString("Context");
    if (ctx == null || ctx.length() <= 0) {
      Log.e("WebDriver", "Error in received intent: " + intent.toString());
      return;
    }

    // Creating a new session. 
    Session sess = SessionRepository.getInstance().add(ctx);
    
    // Returning the session id.
    Bundle res = new Bundle();
    res.putInt("SessionId", sess.getSessionId());
    Log.d("WebDriver", "Session created with Id: " + sess.getSessionId());
    this.setResultExtras(res);
  }
}
