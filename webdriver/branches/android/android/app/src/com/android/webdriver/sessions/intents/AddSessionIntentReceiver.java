package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AddSessionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WebDriver", "Received intent: " + intent.getAction());

		String ctx = intent.getExtras().getString("Context");
		if (ctx == null || ctx.length() <= 0) {
			Log.e("WebDriver", "Error in received intent: " + intent.toString());
			return;
		}

		Session sess = SessionRepository.getInstance().add(ctx);
		Bundle res = new Bundle();
		res.putInt("SessionId", sess.getSessionId());
		Log.d("WebDriver", "Session created with Id: " + sess.getSessionId());
		this.setResultExtras(res);
	}
	
}
