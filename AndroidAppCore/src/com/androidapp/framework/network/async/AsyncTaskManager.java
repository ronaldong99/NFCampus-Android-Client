/*
    Android Client Core, AsyncTaskManager
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.network.async;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Build;

import com.androidapp.framework.utils.NLog;

/**
 * 异步框架类，该类主要实现将耗时操作放在异步线程里面操作，如页面发送请求等。
 * 
 * 发生请求成功: REQUEST_SUCCESS_CODE = 200
 * 发生请求失败: REQUEST_ERROR_CODE = -999
 * 网络有问题: HTTP_ERROR_CODE = -200
 * 网络不可用: HTTP_NULL_CODE = -400
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class AsyncTaskManager {

	/** 日志对象 **/ 
	private final String tag = AsyncTaskManager.class.getSimpleName();
	
	/** 发生请求成功 **/
	public static final int REQUEST_SUCCESS_CODE = 200; 
	/** 发生请求失败 **/
	public static final int REQUEST_ERROR_CODE = -999; 
	/** 网络有问题 **/
	public static final int HTTP_ERROR_CODE = -200;
	/** 网络不可用 **/
	public static final int HTTP_NULL_CODE = -400;
	
	/** 默认下载请求码**/
	public static final int DEFAULT_DOWNLOAD_CODE = 10000;
	
	/** 线程池最多线程数 **/ 
	public final int MAX_CONNECTIONS_NUM = 10;
	
	private Context mContext;
	private static AsyncTaskManager instance;
	private static ExecutorService mExecutorService;
	private static Map<Integer, WeakReference<BaseAsyncTask>> requestMap;

	
	/**
	 * 构造方法
	 * @param context
	 */
	private AsyncTaskManager(Context context) {
		mContext = context;
		mExecutorService = Executors.newFixedThreadPool(MAX_CONNECTIONS_NUM);
		requestMap = new WeakHashMap<Integer, WeakReference<BaseAsyncTask>>();
	}
	
	/**
	 * 单例模式得到AsyncTaskManager实例对象
	 * @param context
	 * @return
	 */
	public static AsyncTaskManager getInstance(Context context) {
		if (instance == null) {
			synchronized (AsyncTaskManager.class) {
				if (instance == null) {
					instance = new AsyncTaskManager(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 发送请求， 默认是需要检查网络的
	 * @param requestCode 请求code
	 * @param listener回调
	 */
	public void request(int requestCode, OnDataListener listener){
		request(requestCode, true, listener);
	}
	
	/**
	 * 发送请求
	 * @param requestCode 请求code
	 * @param isCheckNetwork 是否需要检查网络
	 * @param listener 回调
	 */
	public void request(int requestCode, boolean isCheckNetwork, OnDataListener listener){
		DownLoad bean = new DownLoad(requestCode, isCheckNetwork, listener);
		if(requestCode > 0){
			BaseAsyncTask mAsynctask = new BaseAsyncTask(bean, mContext);
			//after version 2.3 added executeOnExecutor method. 
			//before 2.3 only run five asyntask, more than five must wait 
			if(Build.VERSION.SDK_INT >= 11){
				mAsynctask.executeOnExecutor(mExecutorService);
			}else{
				mAsynctask.execute();
			}
			requestMap.put(requestCode, new WeakReference<BaseAsyncTask>(mAsynctask));
		}else{
			NLog.e(tag, "the error is requestCode < 0");
		}
	}
	
	/**
	 * 根据requestCode取消请求
	 * @param requestCode
	 */
	public void cancelRequest(int requestCode) {
		WeakReference<BaseAsyncTask> requestTask = requestMap.get(requestCode);
    	if(requestTask != null) {
    		BaseAsyncTask request = requestTask.get();
            if(request != null) {
            	request.cancel(true);
            	request = null;
            }
        }
        requestMap.remove(requestCode);
	}
	
	
    /**
     * 取消所有请求
     */
    public void cancelRequest() {
        if(requestMap != null){
            Iterator<Entry<Integer, WeakReference<BaseAsyncTask>>> it = requestMap.entrySet().iterator();
            while(it.hasNext()){
                Entry<Integer, WeakReference<BaseAsyncTask>> entry = (Entry<Integer, WeakReference<BaseAsyncTask>>) it.next();
                Integer requestCode = entry.getKey();
                cancelRequest(requestCode);
            }
            requestMap.clear();
        }
    }
	
}
