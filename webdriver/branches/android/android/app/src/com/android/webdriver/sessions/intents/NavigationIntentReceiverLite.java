package com.android.webdriver.sessions.intents;

import com.android.webdriver.app.SingleSessionActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NavigationIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriverLite", "Received intent: " + intent.getAction());

    String url = intent.getExtras().getString("URL");
    
    ((SingleSessionActivity)context).navigateTo(url);

    Bundle res = new Bundle();
    res.putBoolean("OK", true);
    Log.d("WebDriver", "Navigated OK");
    this.setResultExtras(res);
  }
}
