package org.openqa.selenium.android.intents;

import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.AndroidDriver.Callback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GetUrlIntent extends BroadcastReceiver {

  public void broadcast(int sessionId, String context,
      Context sender, Callback callback) {
    urlCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_GETURL);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK,
        null, null);
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("AndroidDriver", "Received intent: " + intent.getAction());

      String url = getResultExtras(true).getString("URL");
      if (url == null) {
          Log.e("AndroidDriver", "Error in received intent: " +
              intent.toString());
          return;
      }

      urlCallback.getString(url);
  }

  public static GetUrlIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new GetUrlIntent();
    }
    
    return mInstance;
  }

  private Callback urlCallback;
  private static Object syncObject_ = new Object();
  private static GetUrlIntent mInstance = null;

}
