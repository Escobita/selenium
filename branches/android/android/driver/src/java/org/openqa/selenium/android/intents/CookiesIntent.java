package org.openqa.selenium.android.intents;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.webdriver.sessions.SessionCookieManager;
import com.android.webdriver.sessions.SessionCookieManager.CookieActions;
import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.android.Callback;

import java.io.Serializable;

public class CookiesIntent extends BroadcastReceiver {

  public String broadcastSync(int sessionId, String context,
      CookieActions action, Serializable[] args, Context sender) {
    
    cookieValue = "";
    final Object syncObj_ = new Object();
    broadcast(sessionId, context, action, args, sender,
        new Callback() {
          @Override
          public void getString(String arg0) {
            synchronized (this) {
              cookieValue = arg0;
              synchronized (syncObj_) {
                syncObj_.notifyAll();
              }
            }
          }
        }
    );

    synchronized (syncObj_) {
      try {
        syncObj_.wait(AndroidDriver.COMMAND_TIMEOUT);
      } catch (InterruptedException ie) { }
    }
    
    return cookieValue;
  }
  
  
  public void broadcast(int sessionId, String context,
      SessionCookieManager.CookieActions action,
      Serializable[] args, Context sender, Callback callback) {
    strCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_DOACTION);
    Log.d("CookiesIntent", "Sending intent: " + intent.getAction() +
        ", SessionId: " + sessionId + ", Context: " + context + ", action: " +
        action.name());
    
    if (sessionId <= 0) {
      // Wrong session: definitely an error
      Log.e("CookiesIntent:broadcast", "Error in session sending intent, id:" +
          sessionId + ", action: " + action + " stack: ");
      for (StackTraceElement el : Thread.currentThread().getStackTrace())
        Log.e("CookiesIntent:broadcast", el.toString());
      
      return;   // Not sending 
    }

    // Packing all intent arguments
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
    Log.d("AndroidDriver", "Received intent: " + intent.getAction());

    for(String s : getResultExtras(true).keySet())
      Log.d("Intent extra", "Extra: " + s);
    
    int sessId = getResultExtras(true).getInt("SessionId", -1);
    String res = getResultExtras(true).getString("Result");
    
    if (res == null)
      res = "";
    
    Log.d("CookiesIntent", "Got result for session: " + sessId +
        ", result: " + res);
    
    if (sessId == -1) {
        Log.e("AndroidDriver", "Error in received intent: " + intent.toString());
        return;
    }

    if (strCallback != null) {
      Log.d("CookiesIntent:onReceive", "Invoking callback");
      strCallback.getString(res);
    }
  }

  
  public static CookiesIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new CookiesIntent();
    }
    
    return mInstance;
  }

  private Callback strCallback;
  private static Object syncObject_ = new Object();
  private static CookiesIntent mInstance = null;
  private String cookieValue = "";
}
