/*
    Android Client Core, OnDataListener
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.network.async;

import com.androidapp.framework.network.http.HttpException;

/**
 * [异步请求监听]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public interface OnDataListener {

	/**
	 * 耗时操作将在该方法中实现，比如发网络请求等
	 * @param requestCode 请求code
	 * @return
	 * @throws HttpException
	 */
	public Object doInBackground(int requestCode) throws HttpException;
	
	/**
	 * 请求成功回调方法
	 * @param requestCode 请求code
	 * @param result 结果
	 */
	public void onSuccess(int requestCode, Object result);
	
	/**
	 * 请求失败回调方法
	 * @param requestCode 请求code
	 * @param state 状态
	 * @param result 结果
	 */
	public void onFailure(int requestCode, int state, Object result);
}
