package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NavigationIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WebDriver", "Received intent: " + intent.getAction());

		int sessionId = intent.getIntExtra("SessionId", -1);
		String url = intent.getExtras().getString("URL");
		if (sessionId <= 0) {
			Log.e("WebDriver", "Error in received intent: " + intent.toString());
			return;
		}

		SessionRepository.getInstance().getById(sessionId).setUrl(url);
	}
}
