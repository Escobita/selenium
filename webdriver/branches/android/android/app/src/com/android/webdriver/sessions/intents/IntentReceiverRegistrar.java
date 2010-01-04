package com.android.webdriver.sessions.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class IntentReceiverRegistrar {
	public IntentReceiverRegistrar(Context context) {
		mContext = context;
	}
	
	public void registerReceiver(BroadcastReceiver receiver, String action) {
		mContext.registerReceiver(receiver, new IntentFilter(action));
	}
	
	private Context mContext;
}
