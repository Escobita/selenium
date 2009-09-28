package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.SessionRepository;
import com.android.webdriver.sessions.Session.Actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DoActionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WebDriver", "Received intent: " + intent.getAction());

		int sessId = intent.getExtras().getInt("SessionId", -1);
		String ctx = intent.getExtras().getString("Context");
		String action = intent.getExtras().getString("Action");
		if (sessId == -1 || ctx == null || ctx.length() <= 0) {
			Log.e("WebDriver", "Error in received intent: " + intent.toString());
			return;
		}
		
		if (action == null || action.length() <= 0) {
			Log.e("WebDriver", "Action cannot be empty in intent: " +
					intent.toString());
			return;
		}
		
		Log.d("WebDriver:DoActionIntentReceiver", "Session id: " + sessId +
				", context: " + context + ", action: " + action +
				", Actions.action: " + Actions.valueOf(action).name());
		
		Object[] args = new Object[intent.getExtras().size()-3];
		int argCount=0;
		for (String key : intent.getExtras().keySet())
			if (!key.equals("SessionId") && !key.equals("Context") &&
				!key.equals("Action"))
					args[argCount++] = intent.getExtras().get(key);

		Session sess = SessionRepository.getInstance().getById(sessId);
		String actionRes = (String)sess.PerformAction(Actions.valueOf(action),
				args);
		
		Log.d("WebDriver:DoActionIntentReceiver", "Action: " + action +
				", result: " + actionRes);

		// Returning the result
		Bundle res = new Bundle();
		res.putInt("SessionId", sessId);
		res.putString("Result", actionRes);
		Log.d("WebDriver", "Returning result: " + actionRes + " for action: "
				+ action);
		this.setResultExtras(res);
	}
}
