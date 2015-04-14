/*
    Android Client Core, SyncHttpTransportSE
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.network.http;

import org.ksoap2.transport.HttpTransportSE;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-17
 * 
 **/
public class SyncHttpTransportSE extends HttpTransportSE {

	/** 默认超时时间为30s **/
	public static int timeout = 30000;

	public SyncHttpTransportSE(String url) {
		super(url);
	}

	public SyncHttpTransportSE(String url, int timeout) {
		super(url);
		if (timeout <= 30000) {
			SyncHttpTransportSE.timeout = 30000;
		} else {
			SyncHttpTransportSE.timeout = timeout;
		}
	}

	// 尤其注意此方法
//	public ServiceConnection getServiceConnection() throws IOException {
//		return new ServiceConnectionSE(this.url, timeout);
//	}

}
