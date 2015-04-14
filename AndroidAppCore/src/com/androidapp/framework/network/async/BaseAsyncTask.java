/*
    Android Client Core, BaseAsyncTask
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.network.async;

import org.apache.http.HttpException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

/**
 * [异步加载类]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class BaseAsyncTask extends AsyncTask<Object, Integer, Object> {

	@SuppressWarnings("unused")
	private final String tag = BaseAsyncTask.class.getSimpleName();
	
	private DownLoad bean = null;
	private Context mContext;
	
	/**
	 * 构造方法
	 * @param bean
	 * @param context
	 */
	public BaseAsyncTask(DownLoad bean, Context context) {
        this.bean = bean;
        this.mContext = context;
    }
	
	/**
	 * 判断网络是否可用
	 * @param context
	 * @param isCheckNetwork 是否需要检查网络
	 * @return
	 */
	public boolean isNetworkConnected(Context context, boolean isCheckNetwork) {
		if(isCheckNetwork){
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			return ni != null && ni.isConnectedOrConnecting();
		}
		return true;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		try {
			if(bean.getListener() == null){
				throw new HttpException("BaseAsyncTask listener is not null.");
			}
			//判断网络是否可用
			if(isNetworkConnected(mContext, bean.isCheckNetwork)){
				Object result = bean.getListener().doInBackground(bean.getRequestCode());
				bean.setState(AsyncTaskManager.REQUEST_SUCCESS_CODE);
				bean.setResult(result);
			}else{
				bean.setState(AsyncTaskManager.HTTP_NULL_CODE);
			}
		}catch (Exception e) {
			e.printStackTrace();
			if(e instanceof HttpException){
				bean.setState(AsyncTaskManager.HTTP_ERROR_CODE);
			}else{
				bean.setState(AsyncTaskManager.REQUEST_ERROR_CODE);
			}
			bean.setResult(e);
			return bean;
		} 
		
		return bean;
	}

	@Override
	protected void onPostExecute(Object result) {
		DownLoad bean = (DownLoad)result;
		switch(bean.getState()){
			//请求成功
			case AsyncTaskManager.REQUEST_SUCCESS_CODE:
				bean.getListener().onSuccess(bean.getRequestCode(), bean.getResult());
				break;
				
			//网络不可用或者请求失败
			case AsyncTaskManager.REQUEST_ERROR_CODE:
			case AsyncTaskManager.HTTP_NULL_CODE:
			case AsyncTaskManager.HTTP_ERROR_CODE:
				bean.getListener().onFailure(bean.getRequestCode(), bean.getState(), bean.getResult());
				break;
			
			default:
				bean.getListener().onFailure(bean.getRequestCode(), bean.getState(), bean.getResult());
				break;
		}
	}

}
