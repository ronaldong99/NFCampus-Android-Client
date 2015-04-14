/*
    Android Client Core, DownLoad
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.network.async;

/**
 * [下载对象]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class DownLoad {

	/** 请求code **/
	private int requestCode;
	/** 是否刷新，true表示刷新，false表示从缓存取，如果缓存没有还是会从网络取 **/
	private boolean isRefresh;
	/** 下载状态 **/
	private int state;
	/** 返回结果 **/
	private Object result;
	/** 是否需要检查网络 **/ 
	boolean isCheckNetwork;
	/** 处理监听 **/
	private OnDataListener listener;

	public DownLoad() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param requestCode
	 * @param downflag
	 * @param downUrl
	 * @param listener
	 */
	public DownLoad(int requestCode, boolean isCheckNetwork, OnDataListener listener) {
		this.requestCode = requestCode;
		this.isCheckNetwork = isCheckNetwork;
		this.listener = listener;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public void setRefresh(boolean isRefresh) {
		this.isRefresh = isRefresh;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean isCheckNetwork() {
		return isCheckNetwork;
	}

	public void setCheckNetwork(boolean isCheckNetwork) {
		this.isCheckNetwork = isCheckNetwork;
	}

	public OnDataListener getListener() {
		return listener;
	}

	public void setListener(OnDataListener listener) {
		this.listener = listener;
	}

	@Override
	public String toString() {
		return "DownLoad [requestCode=" + requestCode + ", isRefresh="
				+ isRefresh + ", state=" + state + ", result=" + result
				+ ", listener=" + listener + "]";
	}

}
