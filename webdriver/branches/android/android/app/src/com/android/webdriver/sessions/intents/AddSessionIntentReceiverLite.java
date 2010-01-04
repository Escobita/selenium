package com.android.webdriver.sessions.intents;

import com.android.webdriver.app.SingleSessionActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AddSessionIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("WebDriverLite", "Received intent: " + intent.getAction());

      Bundle res = new Bundle();
      res.putInt("SessionId", SingleSessionActivity.SINGLE_SESSION_ID);
      Log.d("WebDriver", "Returning default session id: 1000");
      this.setResultExtras(res);
  }
}
