package com.androidapp.framework.network.networkconnection;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

public abstract class NetworkObserver {
	private Handler mHandler;

	public NetworkObserver(Looper looper) {
		mHandler = new Handler(looper);
	}
	
	protected final void dispatch_noneNetwork(final NetworkInfo networkInfo) {
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				networkIsDisconnected(networkInfo);
			}
			
		});
	}
	
	protected final void dispath_Connected(final NetworkInfo networkInfo) {
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				networkIsConnected(networkInfo);
			}
			
		});
	}
	/**
	 * 网络被关闭或者断开
	 * @param networkInfo
	 */
	public abstract void networkIsDisconnected(NetworkInfo networkInfo);
	/**
	 * 网络可用
	 * @param networkInfo
	 */
	public abstract void networkIsConnected(NetworkInfo networkInfo);
}
