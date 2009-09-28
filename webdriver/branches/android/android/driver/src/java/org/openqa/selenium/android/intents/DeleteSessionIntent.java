package org.openqa.selenium.android.intents;

import android.content.Context;
import android.content.Intent;

import com.android.webdriver.sessions.intents.Intents;

public class DeleteSessionIntent {
  public void broadcast(int sessionId, String context, Context sender) {
    Intent intent = new Intent(Intents.INTENT_DELETESESSION);
    intent.putExtra("SessionId", sessionId);
    intent.putExtra("Context", context);
    sender.sendBroadcast(intent);
  }
  
  public static DeleteSessionIntent getInstance() {
    synchronized (syncObject_) {
      if (mInstance == null)
        mInstance = new DeleteSessionIntent();
    }
    
    return mInstance;
  }

  private static Object syncObject_ = new Object();
  private static DeleteSessionIntent mInstance = null;
}
