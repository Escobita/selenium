package org.openqa.selenium.android.intents;

import com.android.webdriver.sessions.intents.Intents;

import org.openqa.selenium.android.AndroidDriver.Callback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AddSessionIntent extends BroadcastReceiver {
//  protected final int SUCCESS_RETURN_CODE = 1;
  
  public void broadcast(String context, Context sender, Callback callback) {
    intCallback = callback;
    
    Intent intent = new Intent(Intents.INTENT_ADDSESSION);
    intent.putExtra("Context", context);
    
    sender.sendOrderedBroadcast(intent, null, this, null, Activity.RESULT_OK,
        null, null);
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
      Log.e("AndroidDriver", "Received intent: " + intent.getAction());

      for(String s : getResultExtras(true).keySet())
        Log.e("Intent extra", "Extra: " + s);
      
      int sessId = getResultExtras(true).getInt("SessionId", -1);
      if (sessId == -1) {
          Log.e("AndroidDriver", "Error in received intent: " + intent.toString());
          return;
      }

      intCallback.getInt(sessId);
//      sessionId = sessId;
//      // Adding new session to the repository
//      Sessions.getInstance().add(sessId);
//      Sessions.getInstance().setCurrent(sessId);
      
      //this.setResultCode(SUCCESS_RETURN_CODE);
  }

  public static AddSessionIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new AddSessionIntent();
    }
    
    return mInstance;
  }

  private Callback intCallback;
  private static Object syncObject_ = new Object();
  private static AddSessionIntent mInstance = null;
}
