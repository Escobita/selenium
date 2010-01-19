package org.openqa.selenium.android.intents;

import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.Callback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GetTitleIntent extends BroadcastReceiver {

  public void broadcast(int sessionId, String context,
      Context sender, Callback callback) {
    titleCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_GETTITLE);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK,
        null, null);
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("AndroidDriver", "Received intent: " + intent.getAction());

      for(String s : getResultExtras(true).keySet())
        Log.d("Intent extra", "Extra: " + s);
      
      String title = getResultExtras(true).getString("Title");
      if (title == null) {
          Log.e("AndroidDriver", "Error in received intent: " + intent.toString());
          return;
      }

      titleCallback.getString(title);
  }

  public static GetTitleIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new GetTitleIntent();
    }
    
    return mInstance;
  }

  private Callback titleCallback;
  private static Object syncObject_ = new Object();
  private static GetTitleIntent mInstance = null;
}
