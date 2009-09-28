package com.android.webdriver.sessions.intents;

import com.android.webdriver.sessions.SessionRepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GetTitleIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WebDriver", "Received intent: " + intent.getAction());

		int sessId = intent.getExtras().getInt("SessionId", -1);
		String ctx = intent.getExtras().getString("Context");
		if (sessId == -1 || ctx == null || ctx.length() <= 0) {
			Log.e("WebDriver", "Error in received intent: " + intent.toString());
			return;
		}

		String title =
			SessionRepository.getInstance().getById(sessId).getTitle();
		Bundle res = new Bundle();
		res.putString("Title", title);
		Log.d("WebDriver", "Returning title: " + title);
		this.setResultExtras(res);
	}

}
