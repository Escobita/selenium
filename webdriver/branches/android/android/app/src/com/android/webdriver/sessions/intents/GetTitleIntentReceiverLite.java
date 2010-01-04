package com.android.webdriver.sessions.intents;

import com.android.webdriver.app.SingleSessionActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GetTitleIntentReceiverLite extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WebDriverLite", "Received intent: " + intent.getAction());

		String title = ((SingleSessionActivity)context).getWebViewTitle();
		
		Bundle res = new Bundle();
		res.putString("Title", title);
		Log.d("WebDriverLite", "Returning title: " + title);
		this.setResultExtras(res);
	}

}
