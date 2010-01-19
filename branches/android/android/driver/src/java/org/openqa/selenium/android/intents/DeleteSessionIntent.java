package org.openqa.selenium.android.intents;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.Callback;

public class DeleteSessionIntent extends BroadcastReceiver {
  public void broadcast(int sessionId, String context, Context sender,
      Callback callback) {

    intCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_DELETESESSION);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    
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

  public static DeleteSessionIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new DeleteSessionIntent();
    }
    
    return mInstance;
  }

  private Callback intCallback;
  private static Object syncObject_ = new Object();
  private static DeleteSessionIntent mInstance = null;
}
