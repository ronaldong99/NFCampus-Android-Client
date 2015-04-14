package com.androidapp.framework.network.networkconnection;

import java.util.LinkedList;
import java.util.List;

import android.net.NetworkInfo;

public class NetworkObervable {
	private List<NetworkObserver> observers;
	private static NetworkObervable instance; 
	public NetworkObervable() {
		observers = new LinkedList<NetworkObserver>();
	}
	
	public static NetworkObervable getInstance() {
		if (instance == null) {
			instance = new NetworkObervable();
		}
		return instance;
	}
	
	public void addObserver(NetworkObserver observer) {
		observers.add(observer);	
	}
	
	public void removeObserver(NetworkObserver observer) {
		observers.remove(observer);
	}

	public void onChanged(NetworkInfo networkInfo) {
		for (NetworkObserver observer : observers) {
			if (observer != null) {
				dispatchChange(observer, networkInfo); 
			}
		}
	}

	private void dispatchChange(NetworkObserver observer,
			NetworkInfo networkInfo) {
		if (networkInfo != null) {
			if (networkInfo.isConnected() && networkInfo.isAvailable()) {
				observer.dispath_Connected(networkInfo);
			} else {
				observer.dispatch_noneNetwork(networkInfo);
			}
		} else {
			observer.dispatch_noneNetwork(networkInfo);
		}
	}
}
