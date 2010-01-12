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

public class DeleteSessionIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    int sessionId = intent.getIntExtra("SessionId", -1);
    if (sessionId <= 0) {
      Log.e("WebDriver", "Error in received intent: " + intent.toString());
    } else {
      SessionRepository.getInstance().removeById(sessionId);
    }
    
    // Return status
    Bundle res = new Bundle();
    res.putBoolean("OK", true);
    this.setResultExtras(res);
  }
}
