package com.androidapp.framework.network.networkconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkChangedBroadcastReceiver extends BroadcastReceiver {
	
	@SuppressWarnings("unused")
	private static final String TAG = "NetWorkChangedBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			NetworkObervable networkObervable = NetworkObervable.getInstance();
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfos = manager.getActiveNetworkInfo();
			networkObervable.onChanged(networkInfos);
		}
	}

}
