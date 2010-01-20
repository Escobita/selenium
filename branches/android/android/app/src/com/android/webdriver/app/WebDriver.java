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

package com.android.webdriver.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Main activity.
 * 
 * Loads program configuration and starts the UI
 * according to the working mode.
 */
public class WebDriver extends Activity {

  public static final int DEFAULT_REQUEST_CODE = 1001;
  public static final int RESULT_SWITCH_MODE = 1002;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.w("WebDriver:MAIN", "Started");

    // Initializing preference manager and loading its defaults
    PreferencesRepository.createInstance(this);

    // Start main activity 
    startMainScreen();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != DEFAULT_REQUEST_CODE || resultCode != RESULT_SWITCH_MODE)
      return;

    // Mode switching required -- re-launch main activity in a new mode 
    startMainScreen();
  }
  
  private void startMainScreen() {
    // Reading working mode from preferences
    boolean mode = PreferencesRepository.getInstance().getMode();

    // Starting corresponding activity
    Intent i = new Intent(this,
        mode ? SingleSessionActivity.class : SessionListActivity.class);
    this.startActivityForResult(i, DEFAULT_REQUEST_CODE);
  }
  
}
