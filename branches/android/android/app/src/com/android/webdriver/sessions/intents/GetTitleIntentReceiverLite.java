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

public class GetTitleIntentReceiverLite extends BroadcastReceiver {

  /**
   * Interface definition for a callback to be invoked when title is requested.
   */
  public interface TitleRequestListener {
      /**
       * Returns current title of the WebView's content.
       *
       * @return Title of current web page loaded in the WebView.
       */
      String onTitleRequest();
  }

  public void setTitleRequestListener(TitleRequestListener listener) {
    mTitleRequestListener = listener;
  }

  public void removeTitleRequestListener(TitleRequestListener listener) {
    if (listener == mTitleRequestListener)
      mTitleRequestListener = null;
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriverLite", "Received intent: " + intent.getAction());

    String title = "";
    
    if (mTitleRequestListener != null)
      title = mTitleRequestListener.onTitleRequest();
    
    Bundle res = new Bundle();
    res.putString("Title", title);
    Log.d("WebDriverLite", "Returning title: " + title);
    this.setResultExtras(res);
  }

  private TitleRequestListener mTitleRequestListener;
}
