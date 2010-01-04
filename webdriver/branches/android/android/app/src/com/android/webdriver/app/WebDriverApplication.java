package com.android.webdriver.app;

import android.app.Application;
import android.util.Log;

public class WebDriverApplication extends Application {

  @Override
  public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();
    Log.d("Application", "Created");
  }
}
