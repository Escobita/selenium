package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DeleteSessionIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    int sessionId = intent.getIntExtra("SessionId", -1);
    if (sessionId <= 0) {
      Log.e("WebDriver", "Error in received intent: " + intent.toString());
      return;
    }

    SessionRepository.getInstance().removeById(sessionId);

    // Return status
    Bundle res = new Bundle();
    res.putBoolean("OK", true);
    this.setResultExtras(res);
  }
}
