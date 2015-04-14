package com.androidapp.framework.network.networkconnection;

import android.net.NetworkInfo;
import android.os.Looper;

public class NetworkObserverAdpter extends NetworkObserver{

	public NetworkObserverAdpter(Looper looper) {
		super(looper);
	}

	@Override
	public void networkIsConnected(NetworkInfo networkInfo) {
		
	}

	@Override
	public void networkIsDisconnected(NetworkInfo networkInfo) {
		// TODO Auto-generated method stub
		
	}

}
