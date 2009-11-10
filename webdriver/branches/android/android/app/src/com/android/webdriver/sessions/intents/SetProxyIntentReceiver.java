package com.android.webdriver.sessions.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SetProxyIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriver", "Received intent: " + intent.getAction());

    String proxyServer = intent.getExtras().getString("Host");
    String proxyPort = intent.getExtras().getString("Port");
    
    // Set the device proxy
    System.getProperties().put("proxySet",
        proxyServer.length() > 0 ? "true" : "false");
    System.getProperties().put("proxyHost", proxyServer);
    System.getProperties().put("proxyPort", proxyPort);
  }
}
