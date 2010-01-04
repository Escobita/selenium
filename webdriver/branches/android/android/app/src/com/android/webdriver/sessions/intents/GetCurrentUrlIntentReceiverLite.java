package com.android.webdriver.sessions.intents;

import com.android.webdriver.app.SingleSessionActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GetCurrentUrlIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    String url = ((SingleSessionActivity)context).getLastUrl();

    Bundle res = new Bundle();
    res.putString("URL", url);
    Log.d("WebDriverLite", "Returning URL: " + url);
    this.setResultExtras(res);
  }
}
