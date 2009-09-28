package org.openqa.selenium.android.intents;

import android.content.Context;
import android.content.Intent;

import com.android.webdriver.sessions.intents.Intents;

public class NavigateIntent {
  public void broadcast(int sessionId, String context, Context sender,
      String url) {
    Intent intent = new Intent(Intents.INTENT_NAVIGATE);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    intent.putExtra("URL", url);
    sender.sendBroadcast(intent);
  }
  
  public static NavigateIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new NavigateIntent();
    }
    
    return mInstance;
  }

  private static Object syncObject_ = new Object();
  private static NavigateIntent mInstance = null;
}
