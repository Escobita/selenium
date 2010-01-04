package com.android.webdriver.sessions.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DeleteSessionIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriverLite", "Received intent: " + intent.getAction());

    // Return status
    Bundle res = new Bundle();
    res.putBoolean("OK", true);
    this.setResultExtras(res);
  }
}
