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

import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Intent receiver for handling addition of a new session.
 * When working in single-session mode, no actual session is created,  
 * and the returned session id is always the same.
 */
public class AddSessionIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("WebDriverLite", "Received intent: " + intent.getAction());

      // Returning a default session id.
      Bundle res = new Bundle();
      res.putInt("SessionId", SessionRepository.SINGLE_SESSION_ID);
      Log.d("WebDriver", "Returning default session id: 1000");
      this.setResultExtras(res);
  }
}
