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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Global repository to store configuration values of the application.
 * Provides methods for updating and retrieving individual
 * configuration settings.
 * <p>
 * This is a singleton object. {@link #createInstance(Context)} should be
 * called to initialize or re-initialize class instance.
 */
public class PreferencesRepository {

  /**
   * Private constructor: {@link #createInstance(Context)} should be used
   * to initialize this object. 
   */
  private PreferencesRepository() {
    // This populates the default values from the preferences XML file.
    PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
    mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
  }
  
  /**
   * Returns UI execution mode setting (True -- simple mode,
   * False -- multi-session mode).
   * 
   * @return Execution mode: True - simple mode, False - multi-session mode.
   */
  public boolean getMode() {
    return mPrefs.getBoolean("simple_working_mode", true);
  }

  /**
   * Store the execution mode setting.
   * <p>
   * Note: this method doesn't actually change the execution mode.
   *  
   * @param isSimpleMode True for simple mode, False for multi-session mode.
   */
  public void setMode(boolean isSimpleMode) {
    Editor editor = mPrefs.edit();
    editor.putBoolean("simple_working_mode", isSimpleMode);
    if (!editor.commit())
      Alerts.Message(mContext, "Error saving preferences.");
  }

  /**
   * Creates or re-initializes singleton instance of the class.
   * @param context Android application context to use settings for. 
   */
  public static void createInstance(Context context) {
    mContext = context;
    mInstance = new PreferencesRepository();
  }

  /**
   * Returns instance of {@link PreferencesRepository} if initialized.
   * @return instance of {@link PreferencesRepository}
   * @throws RuntimeException if instance had not been initialized
   *    calling {@link #createInstance(Context)}.
   */
  public static PreferencesRepository getInstance() {
    if (mInstance == null)
      throw new RuntimeException("PreferencesRepository has to be created " +
          " by createInstance method");
    
    return mInstance;
  }
  
  private static Context mContext = null;
  private static PreferencesRepository mInstance = null;
  private SharedPreferences mPrefs;
}
