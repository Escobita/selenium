package org.openqa.selenium.android.intents;

import android.content.Context;
import android.content.Intent;

import com.android.webdriver.sessions.intents.Intents;

public class SetProxyIntent {
  public void broadcast(int sessionId, String context,
      String host, String port, Context sender) {
    Intent intent = new Intent(Intents.INTENT_DELETESESSION);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    intent.putExtra("Host", context);
    intent.putExtra("Port", context);
    sender.sendBroadcast(intent);
  }
  
  public static SetProxyIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new SetProxyIntent();
    }
    
    return mInstance;
  }

  private static Object syncObject_ = new Object();
  private static SetProxyIntent mInstance = null;

}
