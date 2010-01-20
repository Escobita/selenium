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

public class GetCurrentUrlIntentReceiverLite extends BroadcastReceiver {

  /**
   * Interface definition for a callback to be invoked when URL is required.
   */
  public interface UrlRequestListener {
      /**
       * Returns current URL of the WebView.
       *
       * @return URL of current web page loaded in the WebView.
       */
      String onUrlRequest();
  }

  public void setUrlRequestListener(UrlRequestListener listener) {
    mUrlRequestListener = listener;
  }

  public void removeUrlRequestListener(UrlRequestListener listener) {
    if (listener == mUrlRequestListener)
      mUrlRequestListener = null;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    String url = "";
    if (mUrlRequestListener != null)
      url = mUrlRequestListener.onUrlRequest();

    Bundle res = new Bundle();
    res.putString("URL", url);
    Log.d("WebDriverLite", "Returning URL: " + url);
    this.setResultExtras(res);
  }

  private UrlRequestListener mUrlRequestListener;
}
