package com.geal.lightningauth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.v("qauth", "GCMIntentService: onError: "+arg1);

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.v("qauth", "GCMIntentService: onMessage: "+arg1.getStringExtra("challenge"));

	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Log.v("qauth", "GCMIntentService: onRegistered with reg id: "+arg1);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.v("qauth", "GCMIntentService: onUnregistered with reg id: "+arg1);

	}

}
