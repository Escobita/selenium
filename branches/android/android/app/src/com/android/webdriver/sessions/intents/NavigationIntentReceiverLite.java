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
import android.os.Bundle;
import android.util.Log;

public class NavigationIntentReceiverLite extends BroadcastReceiver {

  /**
   * Interface definition for a callback to be invoked when any navigation
   * is requested.
   */
  public interface NavigateRequestListener {
      /**
       * Request to navigate the WebView to a given URL.
       *
       * @param url URL to navigate to.
       */
      void onNavigateRequest(String url);
  }
  
  public void setNavigateRequestListener(NavigateRequestListener listener) {
    mNavigateRequestListener = listener;
  }

  public void removeNavigateRequestListener(NavigateRequestListener listener) {
    if (listener == mNavigateRequestListener)
      mNavigateRequestListener = null;
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriverLite", "Received intent: " + intent.getAction());

    String url = intent.getExtras().getString("URL");
    
    if (mNavigateRequestListener != null)
      mNavigateRequestListener.onNavigateRequest(url);
    
    Bundle res = new Bundle();
    res.putBoolean("OK", true);
    Log.d("WebDriver", "Navigated OK");
    this.setResultExtras(res);
  }

  private NavigateRequestListener mNavigateRequestListener;
}
