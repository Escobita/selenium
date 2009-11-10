package org.openqa.selenium.android.intents;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.AndroidDriver.Callback;

public class NavigateIntent extends BroadcastReceiver {
  public void broadcast(int sessionId, String context, Context sender,
      String url, boolean blocking, Callback callback) {
    
    intCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_NAVIGATE);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    intent.putExtra("URL", url);
    intent.putExtra("Blocking", blocking); // TODO: Implement blocking navigation
    
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK,
      null, null);
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("AndroidDriver", "Received intent: " + intent.getAction());

      boolean resultOk = getResultExtras(true).getBoolean("OK", false);
      if (intCallback != null)
        intCallback.getInt(resultOk ? 1 : 0);
  }

  public static NavigateIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new NavigateIntent();
    }
    
    return mInstance;
  }

  private Callback intCallback;
  private static Object syncObject_ = new Object();
  private static NavigateIntent mInstance = null;
}
