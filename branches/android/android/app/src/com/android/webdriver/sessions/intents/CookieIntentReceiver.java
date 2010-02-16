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

import com.android.webdriver.sessions.Cookie;
import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.SessionCookieManager;
import com.android.webdriver.sessions.SessionRepository;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Intent receiver for handling cookie-related requests.
 * Passes the request to the {@link SessionCookieManager} to be processed.
 */
public class CookieIntentReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("WebDriver", "Received intent: " + intent.getAction());

      int sessId = intent.getExtras().getInt("SessionId", -1);
      String action = intent.getExtras().getString("Action");
      if (sessId == -1) {
          Log.e("WebDriver", "Error in received intent: " + intent.toString());
          return;
      }
      
      if (action == null || action.length() <= 0) {
          Log.e("WebDriver", "Action cannot be empty in intent: " +
                  intent.toString());
          return;
      }
      
      Object[] args = new Object[0];
      if (intent.getExtras() != null && intent.getExtras().size() > 3) {
          args = new Object[intent.getExtras().size()-3];
          int argCount=0;
          for (String key : intent.getExtras().keySet())
              if (!key.equals("SessionId") && !key.equals("Action"))
                      args[argCount++] = intent.getExtras().get(key);
      }
      
      Log.d("WebDriver:CookieIntentReceiver", "Session id: " + sessId +
          ", action: " + action + ", args #: " + args.length + 
          ((args.length > 0) ? ", argument #1: " + args[0] : ""));

      String result = "";
      SessionCookieManager cookieManager = SessionCookieManager.getInstance();
      Session session = SessionRepository.getInstance().getById(sessId);
      String domain = extractDomain(session.getLastUrl());
      
      switch(SessionCookieManager.CookieActions.valueOf(action)) {
        case GET:
          Cookie cookie = cookieManager.getCookie(domain, (String) args[0]);
          if (cookie != null)
            result = cookie.getValue();
          break;
        case REMOVE_ALL:
          cookieManager.removeAllCookies(domain);
          break;
        case GET_ALL:
          result = cookieManager.getCookiesAsString(domain);
          break;
        case REMOVE:
          cookieManager.remove(domain, (String) args[0]);
          break;
        case ADD:
          cookieManager.addCookie(domain, (String)args[0], (String)args[1]);
          break;
      }
      
      // Returning the result
      Bundle res = new Bundle();
      res.putInt("SessionId", sessId);
      res.putString("Result", result);
      this.setResultExtras(res);
  }
  
  private String extractDomain(String url) {
    URL url_;
    try {
      url_ = new URL(url);
    } catch (MalformedURLException e) {
      return null;
    }
    return url_.getHost();
  }
}
