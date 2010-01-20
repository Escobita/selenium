/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.SessionRepository;
import com.android.webdriver.sessions.Session.Actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DoActionIntentReceiverLite extends BroadcastReceiver {

  /**
   * Interface definition for a callback to be invoked when any property of
   * the session is changed.
   */
  public interface ActionRequestListener {
      /**
       * Request to perform an action
       *
       * @param action Action to perform.
       * @param params Argument of an action.
       * @return Result of the action or null if no result was returned.
       */
      Object onActionRequest(Actions action, Object[] params);
  }
  
  public void setActionRequestListener(ActionRequestListener listener) {
    mActionRequestListener = listener;
  }

  public void removeActionRequestListener(ActionRequestListener listener) {
    if (listener == mActionRequestListener)
      mActionRequestListener = null;
  }
  
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
    
    Object result = null;
    if (mActionRequestListener != null)
      result = mActionRequestListener.onActionRequest(Actions.valueOf(action),
          args);

    actionRes = result != null ? result.toString() : "";
    
    Log.d("WebDriverLite:DoActionIntentReceiver", "Action: " + action +
        ", result length: " + actionRes.length());

    // Returning the result
    Bundle res = new Bundle();
    res.putInt("SessionId", SessionRepository.SINGLE_SESSION_ID);
    res.putString("Result", actionRes);
    this.setResultExtras(res);
  }

  private ActionRequestListener mActionRequestListener;
}
