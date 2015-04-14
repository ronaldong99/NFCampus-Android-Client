/*
    Android Client Core, DownloadManager
    Copyright (c) 2015 NF
     
*/
package com.androidapp.framework.network.download;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.androidapp.framework.common.Constants;
import com.androidapp.framework.common.PreferencesManager;
import com.androidapp.framework.network.http.AsyncHttpClient;
import com.androidapp.framework.network.http.AsyncHttpResponseHandler;
import com.androidapp.framework.network.http.BreakpointHttpResponseHandler;
import com.androidapp.framework.utils.MD5Utils;
import com.androidapp.framework.utils.NLog;

/**
 * [下载器管理类，支持并发、暂停、继续、删除任务操作以及断点续传]
 * 
	DownloadManager downloadMgr = DownloadManager.getInstance();
	downloadMgr.setDownLoadCallback(new DownLoadCallback(){
		
		@Override
		public void onLoading(String url, int bytesWritten, int totalSize) {
			super.onLoading(url, bytesWritten, totalSize);
		}
		
		@Override
		public void onSuccess(String url) {
			super.onSuccess(url);
		}
		
		@Override
		public void onFailure(String url, String strMsg) {
			super.onFailure(url, strMsg);
		}
	});
	
	//添加下载任务
	downloadMgr.addHandler(url);
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-12
 * 
 **/
public class DownloadManager extends Thread {

	private final String tag = DownloadManager.class.getSimpleName();
	private static final int DEFAULT_MAX_CONNECTIONS = 3;
	private static DownloadManager instance;
	private HandlerQueue mhandlerQueue;
	private List<AsyncHttpResponseHandler> mDownloadinghandlers;
	private List<AsyncHttpResponseHandler> mPausinghandlers;
	private AsyncHttpClient asyncHttpClient;
	private DownLoadCallback mDownLoadCallback;
	private ThreadPoolExecutor threadPool;
	private Boolean isRunning = false;

	/**
	 * 得到DownloadManager实例
	 * @param rootPath
	 * @return
	 */
	public static DownloadManager getInstance() {
		if (instance == null) {
			synchronized (DownloadManager.class) {
				if (instance == null) {
					instance = new DownloadManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 构造方法
	 * @param rootPath
	 */
	private DownloadManager() {
		mhandlerQueue = new HandlerQueue();
		mDownloadinghandlers = new ArrayList<AsyncHttpResponseHandler>();
		mPausinghandlers = new ArrayList<AsyncHttpResponseHandler>();
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_CONNECTIONS);
	}

	/**
	 * 设置下载回调监听事件
	 * @param downLoadCallback
	 */
	public void setDownLoadCallback(DownLoadCallback downLoadCallback) {
		this.mDownLoadCallback = downLoadCallback;
	}

	/**
	 * 开始下载
	 */
	public void startManage() {
		if (!isAlive()) {
			isRunning = true;
			this.start();
			if (mDownLoadCallback != null) {
				mDownLoadCallback.sendStartMessage();
			}
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		isRunning = false;
		pauseAllHandler();
		if (mDownLoadCallback != null) {
			mDownLoadCallback.sendStopMessage();
		}
		interrupt();
//		this.stop();
		instance = null;
	}

	/**
	 * 判断线程是否运行
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void run() {
		while (isRunning) {
			BreakpointHttpResponseHandler handler = (BreakpointHttpResponseHandler) mhandlerQueue.poll();
			if (handler != null) {
				mDownloadinghandlers.add(handler);
				handler.setInterrupt(false);
				
				asyncHttpClient = new AsyncHttpClient();
				asyncHttpClient.setThreadPool(threadPool);
				
				//初始化下载链接签名信息
				if(handler.getParams() != null && handler.getContext() != null){
					String user_id = PreferencesManager.getInstance(handler.getContext()).get(Constants.USER_ID);
					String token = PreferencesManager.getInstance(handler.getContext()).get(Constants.TOKEN);
					
					String sign = handler.getParams().getParamValueString() + token;
					asyncHttpClient.addHeader(Constants.SIGN, MD5Utils.encrypt(sign));
					asyncHttpClient.addHeader(Constants.CC, user_id);
				}
				
				asyncHttpClient.get(handler.getContext(), handler.getUrl(), handler.getParams(), handler);
			}
		}
	}

	/**
	 * 浏览器下载
	 * @param context
	 * @param uriString 下载资源地址
	 */
	public void addHandler(Context context, String uriString){
		if(TextUtils.isEmpty(uriString)){
			throw new IllegalArgumentException("addHandler uriString is not null.");
		}
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
	}
	
	
	/**
	 * 添加一个任务
	 * @param url
	 * @param params 参数RequestParams
	 */
	public void addHandler(DownloadParam downloadParam) {
			
		if(downloadParam == null){
			NLog.e(tag, "addHandler downloadParam is not null.");
			return;
		}
		
		String fileName = downloadParam.getFileName();
		if (TextUtils.isEmpty(downloadParam.getUrl()) || TextUtils.isEmpty(fileName)) {
			NLog.e(tag, "addHandler url or fileName is not null.");
			return;
		}
		
		if (TextUtils.isEmpty(downloadParam.getDownPath())) {
			NLog.e(tag, "addHandler downPath is not null.");
			return;
		}
		
		if(hasHandler(fileName)){
			NLog.e(tag, "addHandler fileName: " + fileName +" is exist.");
			return;
		}
		
		broadcastAddHandler(fileName);
		mhandlerQueue.offer(newAsyncHttpResponseHandler(downloadParam));
		
		if (!isAlive()) {
			startManage();
		}
	}

	/**
	 * 发送添加广播
	 * @param url
	 */
	private void broadcastAddHandler(String fileName){
		broadcastAddHandler(fileName, false);
	}

	/**
	 * 发送添加广播
	 * @param url
	 * @param isInterrupt
	 */
	private void broadcastAddHandler(String fileName, boolean isInterrupt) {
		if (mDownLoadCallback != null) {
			mDownLoadCallback.sendAddMessage(fileName, isInterrupt);
		}
	}

	public void reBroadcastAddAllhandler() {

		BreakpointHttpResponseHandler handler = null;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mDownloadinghandlers.get(i);
			broadcastAddHandler(handler.getFileName(), handler.isInterrupt());
		}
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mhandlerQueue.get(i);
			broadcastAddHandler(handler.getFileName());
		}
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mPausinghandlers.get(i);
			broadcastAddHandler(handler.getFileName());
		}
	}

	public boolean hasHandler(String fileName) {

		BreakpointHttpResponseHandler handler;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mDownloadinghandlers.get(i);
			if (fileName.equals(handler.getFileName())) {
				return true;
			}
		}
		
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			try{
				handler = (BreakpointHttpResponseHandler) mhandlerQueue.get(i);
			}
			catch (IndexOutOfBoundsException e){
				e.printStackTrace();
				return false;
			}
			
			if (null == handler){
				return false;
			}
			if (fileName.equals(handler.getFileName())) {
				return true;
			}
		}
		
		return false;
	}

	public int getTotalhandlerCount() {
		return mhandlerQueue.size() + mDownloadinghandlers.size() + mPausinghandlers.size();
	}


	/**
	 * 根据url删除下载任务
	 * @param url
	 */
	public synchronized void deleteHandler(String fileName) {
		
		BreakpointHttpResponseHandler handler = null;
		
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mDownloadinghandlers.get(i);
			if (handler != null && handler.getFileName().equals(fileName)) {
				File file = handler.getTargetFile();
				if (file.exists()){
					file.delete();
				}
				File tempFile = handler.getTempFile();
				if (tempFile.exists()) {
					tempFile.delete();
				}
				handler.setInterrupt(true);
				completehandler(handler);
				return;
			}
		}
		
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mhandlerQueue.get(i);
			if (handler != null && handler.getFileName().equals(fileName)) {
				mhandlerQueue.remove(handler);
			}
		}
		
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mPausinghandlers.get(i);
			if (handler != null && handler.getFileName().equals(fileName)) {
				mPausinghandlers.remove(handler);
			}
		}
	}

	/**
	 * 继续下载
	 * @param url
	 */
	public synchronized void continueHandler(String fileName) {
		BreakpointHttpResponseHandler handler = null;
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mPausinghandlers.get(i);
			if (handler != null && handler.getFileName().equals(fileName)) {
				mPausinghandlers.remove(handler);
				mhandlerQueue.offer(handler);
			}
		}
	}

	/**
	 * 全部继续下载
	 * @param url
	 */
	public synchronized void continueAllHandler() {
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_CONNECTIONS);
//		BreakpointHttpResponseHandler handler = null;
//		for (; mPausinghandlers.size() >= 1;) {
//			handler = (BreakpointHttpResponseHandler) mPausinghandlers.get(0);
//			mPausinghandlers.remove(handler);
//			mhandlerQueue.offer(handler);			
//			NLog.e(tag, "continueAllHandler fileName: " + handler.getFileName());
//		}
	}
	
	/**
	 * 根据Url暂停下载任务
	 * @param url
	 */
	public synchronized void pauseHandler(String fileName) {
		BreakpointHttpResponseHandler handler;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (BreakpointHttpResponseHandler) mDownloadinghandlers.get(i);
			if (handler != null && handler.getFileName().equals(fileName)) {
				pausehandler(handler);
			}
		}
	}
	
	/**
	 * 暂停所有下载任务
	 */
	public synchronized void pauseAllHandler() {
		
		AsyncHttpResponseHandler handler = null;

		for (; mhandlerQueue.size() >= 1; ) {
			handler = mhandlerQueue.get(0);
			mhandlerQueue.remove(handler);
//			mPausinghandlers.add(handler);
		}
//
//		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
//			handler = mDownloadinghandlers.get(i);
//			if (handler != null) {
//				pausehandler(handler);
//			}
//		}
		
		mDownloadinghandlers.clear();
		mPausinghandlers.clear();
		threadPool.shutdownNow();
	}
	
	/**
	 * 暂停下载handler
	 * @param handler
	 */
	private synchronized void pausehandler(AsyncHttpResponseHandler handler) {
		BreakpointHttpResponseHandler fileHttpResponseHandler = (BreakpointHttpResponseHandler) handler;
		if (handler != null) {
			fileHttpResponseHandler.setInterrupt(true);
			mDownloadinghandlers.remove(handler);
			mPausinghandlers.add(handler);
		}
	}


	/**
	 * 完成下载任务
	 * @param handler
	 */
	private synchronized void completehandler(AsyncHttpResponseHandler handler) {
		if (mDownloadinghandlers.contains(handler)) {
			mDownloadinghandlers.remove(handler);

			if (mDownLoadCallback != null) {
				mDownLoadCallback.sendFinishMessage(((BreakpointHttpResponseHandler) handler).getFileName());
			}
		}
	}

	/**
	 * 构造一个BreakpointHttpResponseHandler
	 * @param mContext
	 * @param params
	 * @param url
	 * @return
	 */
	private AsyncHttpResponseHandler newAsyncHttpResponseHandler(DownloadParam downloadParam) {
		
		BreakpointHttpResponseHandler handler = new BreakpointHttpResponseHandler(downloadParam) {
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				super.onProgress(bytesWritten, totalSize);
				if (mDownLoadCallback != null) {
					mDownLoadCallback.sendLoadMessage(getFileName(), bytesWritten, totalSize);
				}
			}

			@Override
			public void onSuccess(File file) {
				if (mDownLoadCallback != null) {
					NLog.d(tag, "onSuccess: " + getFileName());
					mDownLoadCallback.sendSuccessMessage(getFileName(), file.getPath());
				}
//				deleteHandler(getFileName());	
			}

			public void onFinish() {
				NLog.d(tag, "onFinish: " + getFileName());
				completehandler(this);				
			}

			public void onStart() {
				if (mDownLoadCallback != null) {
					mDownLoadCallback.onStart();
				}
				NLog.d(tag, "onStart: " + getFileName());
			}

			@Override
			public void onFailure(Throwable error) {
				String message = "";
				if (error != null) {
					message = error.getMessage();
				}
				
				if (mDownLoadCallback != null) {
					NLog.d(tag, "onFailure: " + getFileName());
					mDownLoadCallback.sendFailureMessage(getFileName(), message);
				}
				
				pausehandler(this);
			}
		};

		return handler;
	}
	
	
	/**
	 * [A brief description]
	 * 
	 * @author mashidong
	 * @version 1.0
	 * @date 2014-3-13
	 * 
	 **/
	private class HandlerQueue {
		
		private Queue<AsyncHttpResponseHandler> handlerQueue;

		public HandlerQueue() {
			handlerQueue = new LinkedList<AsyncHttpResponseHandler>();
		}

		public void offer(AsyncHttpResponseHandler handler) {
			handlerQueue.offer(handler);
		}

		public AsyncHttpResponseHandler get(int position) {
			if (position >= size()) {
				return null;
			}
			return ((LinkedList<AsyncHttpResponseHandler>) handlerQueue).get(position);
		}
		
		public AsyncHttpResponseHandler poll() {
			return handlerQueue.poll();
		}

		@SuppressWarnings("unused")
		public boolean remove(int position) {
			return handlerQueue.remove(get(position));
		}

		public boolean remove(AsyncHttpResponseHandler handler) {
			return handlerQueue.remove(handler);
		}
		
		public int size() {
			return handlerQueue.size();
		}
	}

}
