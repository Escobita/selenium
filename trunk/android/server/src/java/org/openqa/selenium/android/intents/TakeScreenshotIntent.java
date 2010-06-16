/*
Copyright 2010 WebDriver committers
Copyright 2010 Google Inc.

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

package org.openqa.selenium.android.intents;

import org.openqa.selenium.android.Callback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TakeScreenshotIntent extends BroadcastReceiver {
  private Callback callback;
  private static final String LOG_TAG = TakeScreenshotIntent.class.getName();
  private static final TakeScreenshotIntent INSTANCE = new TakeScreenshotIntent();

  private TakeScreenshotIntent() {
  }

  public static TakeScreenshotIntent getInstance() {
    return INSTANCE;
  }

  public void broadcast(Context sender, boolean result, Callback callback) {
    this.callback = callback;
    Intent intent = new Intent(Action.TAKE_SCREENSHOT);
    Log.d(LOG_TAG, "Sending intent: " + Action.TAKE_SCREENSHOT);
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK, null, null);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.e(LOG_TAG, "received intent: " + intent.getAction());
    byte[] rawPng = getResultExtras(true).getByteArray(BundleKey.SCREENSHOT);
    if (rawPng == null) {
      Log.e(LOG_TAG, "Error in receiving intent, raw image is null");
    }
    callback.byteArrayCallback(rawPng);
  }

}
