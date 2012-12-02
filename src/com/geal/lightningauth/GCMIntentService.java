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
		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
		myIntent.putExtra("challenge", arg1.getStringExtra("challenge"));
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(myIntent);

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
