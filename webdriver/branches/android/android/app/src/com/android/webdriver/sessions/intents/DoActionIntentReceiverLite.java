package com.android.webdriver.sessions.intents;

import com.android.webdriver.app.SingleSessionActivity;
import com.android.webdriver.sessions.Session.Actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DoActionIntentReceiverLite extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("WebDriverLite", "Received intent: " + intent.getAction());

    String action = intent.getExtras().getString("Action");
    if (action == null || action.length() <= 0) {
      Log.e("WebDriverLite", "Action cannot be empty in intent: " +
          intent.toString());
      return;
	}

    Object[] args = new Object[0];
    if (intent.getExtras() != null && intent.getExtras().size() > 3) {
      args = new Object[intent.getExtras().size()-3];
      int argCount=0;
      for (String key : intent.getExtras().keySet())
        if (!key.equals("SessionId") && !key.equals("Context") &&
            !key.equals("Action"))
          args[argCount++] = intent.getExtras().get(key);
    }

    Log.d("WebDriverLite:DoActionIntentReceiver", "Action: " + action +
        ", Actions.action: " + Actions.valueOf(action).name() +
        ", args #: " + args.length + 
        ((args.length > 0) ? ", argument #1: " + args[0] : ""));

    String actionRes = "";

    switch(Actions.valueOf(action)) {
      case EXECUTE_JAVASCRIPT:
        if (args.length == 1)
          actionRes =
            ((SingleSessionActivity)context).executeJS(args[0].toString());
        else
          Log.w("WebDriverLite:ExecuteJavaScript",
              "Incorrect arguments for intent: " + intent.getAction());
        break;
      case GET_PAGESOURCE:
        actionRes = ((SingleSessionActivity)context).executeJS(
          "window.webdriver.resultMethod(document.documentElement.outerHTML);");        
        break;
      case NAVIGATE_BACK:
        ((SingleSessionActivity)context).navigateBackOrForward(-1);
        break;
      case NAVIGATE_FORWARD:
        ((SingleSessionActivity)context).navigateBackOrForward(1);
        break;
      case REFRESH:
        ((SingleSessionActivity)context).reload();
        break;
    }

    Log.d("WebDriverLite:DoActionIntentReceiver", "Action: " + action +
        ", result length: " + actionRes.length());

    // Returning the result
    Bundle res = new Bundle();
    res.putInt("SessionId", SingleSessionActivity.SINGLE_SESSION_ID);
    res.putString("Result", actionRes);
    this.setResultExtras(res);
  }
}
