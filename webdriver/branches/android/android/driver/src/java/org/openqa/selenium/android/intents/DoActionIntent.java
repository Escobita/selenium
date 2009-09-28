package org.openqa.selenium.android.intents;

import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.AndroidDriver.Callback;

import java.io.Serializable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DoActionIntent extends BroadcastReceiver {

  public void broadcast(int sessionId, String context, Session.Actions action,
      Serializable[] args, Context sender, Callback callback) {
    strCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_DOACTION);
    Log.d("DoActionIntent", "Sending intent: " + intent.getAction() +
        ", SessionId: " + sessionId + ", Context: " + context + ", action: " +
        action.name());
    
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    intent.putExtra("Action", action.name());
    if (args != null && args.length > 0)
      for(int i=0; i<args.length; i++)
        intent.putExtra("arg" + i, args[i]);
    
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK,
        null, null);
  }


  @Override
  public void onReceive(Context ctx, Intent intent) {
    Log.e("AndroidDriver", "Received intent: " + intent.getAction());

    for(String s : getResultExtras(true).keySet())
      Log.e("Intent extra", "Extra: " + s);
    
    int sessId = getResultExtras(true).getInt("SessionId", -1);
    String res = getResultExtras(true).getString("Result");
    
    Log.d("DoActionIntent", "Got result for session: " + sessId + ", result: " +
        res);
    
    if (sessId == -1) {
        Log.e("AndroidDriver", "Error in received intent: " + intent.toString());
        return;
    }

    strCallback.getString(res);
  }

  public static DoActionIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new DoActionIntent();
    }
    
    return mInstance;
  }

  private Callback strCallback;
  private static Object syncObject_ = new Object();
  private static DoActionIntent mInstance = null;

}
