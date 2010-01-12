package com.android.webdriver.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesRepository {

  private PreferencesRepository() {
    // This populates the default values from the preferences XML file.
    PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
    mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
  }
  
  public boolean getMode() {
    return mPrefs.getBoolean("simple_working_mode", true);
  }
  
  public void setMode(boolean isSimpleMode) {
    Editor editor = mPrefs.edit();
    editor.putBoolean("simple_working_mode", isSimpleMode);
    if (!editor.commit())
      Alerts.Message(mContext, "Error saving preferences.");
  }
  
  public static void createInstance(Context context) {
    mContext = context;
    mInstance = new PreferencesRepository();
  }
  
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
