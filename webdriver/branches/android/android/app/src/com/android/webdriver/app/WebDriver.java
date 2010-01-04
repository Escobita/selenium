package com.android.webdriver.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class WebDriver extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.w("WebDriver:MAIN", "Started");

    // This populates the default values from the preferences XML file.
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    SharedPreferences prefs =
      PreferenceManager.getDefaultSharedPreferences(this);

    // Reading working mode from preferences
    boolean mode = prefs.getBoolean("simple_working_mode", true);

    // Starting corresponding activity
    Intent i = new Intent(this,
        mode ? SingleSessionActivity.class : SessionListActivity.class);
    this.startActivity(i);
  }
}
