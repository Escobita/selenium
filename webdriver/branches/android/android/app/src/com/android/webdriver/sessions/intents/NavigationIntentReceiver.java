package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NavigationIntentReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    boolean resultOk = true;
    int sessionId = intent.getIntExtra("SessionId", -1);
    String url = intent.getExtras().getString("URL");
    if (sessionId <= 0) {
      Log.e("WebDriver", "Error in received intent: " + intent.toString());
      resultOk = false;
    } else {
      try {
        SessionRepository.getInstance().getById(sessionId).setUrl(url, false);
      } catch(Exception e) {
        Log.e("WebDriver", "Exception while navigating to URL: " +
            url + ", message: " + e.getMessage());
        e.printStackTrace();
        resultOk = false;
      }
    }

    Bundle res = new Bundle();
    res.putBoolean("OK", resultOk);
    Log.d("WebDriver", "Navigated OK: " + resultOk);
    this.setResultExtras(res);
  }
}
