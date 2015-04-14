/*
    Android Client Core, MyLocationManager
    Copyright (c) 2015 NF
     
*/

package com.androidapp.framework.utils.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import com.androidapp.framework.common.Constants;
import com.androidapp.framework.common.parse.JsonMananger;
import com.androidapp.framework.network.async.AsyncTaskManager;
import com.androidapp.framework.network.async.OnDataListener;
import com.androidapp.framework.network.http.AsyncHttpClient;
import com.androidapp.framework.network.http.AsyncHttpResponseHandler;
import com.androidapp.framework.network.http.HttpException;
import com.androidapp.framework.network.http.RequestParams;
import com.androidapp.framework.utils.NLog;
import com.androidapp.framework.utils.lang.LangManager;

/**
 * [定位管理类]
 * 
 * 使用说明：
 * 
 * 	MyLocationManager locationmgr = MyLocationManager.getInstance(mContext);
 *	locationmgr.setLocationListener(new MyLocationListener() {
 *		@Override
 *		public void onLocation(double latitude, double longitude, String address) {
 *			NLog.e("MineFragment", "latitude: " + latitude + " longitude: " + longitude);
 *			NLog.e("MineFragment", "address: " + address);
 *		}
 *	});
 *	locationmgr.startLocation();
 *
 * @author mashidong
 * @version 1.0
 * @date 2014-4-2
 * 
 **/
public class MyLocationManager implements LocationListener, OnDataListener{

	private final String tag = MyLocationManager.class.getSimpleName();
	private final int REQ_GEOCODER_CODE = 10021;
	
	private Context mContext;
	private static MyLocationManager instance;
	private LocationManager locationMgr;
	private MyLocationListener locationListener;
	private AsyncHttpClient asyncHttpClient;
	private AsyncTaskManager asyncTaskManager;
	/** 纬度 **/ 
	private double latitude;
	/** 经度 **/ 
	private double longitude; 
	
	/**
	 * 得到单例模式LocationManager对象
	 * @return
	 */
	public static MyLocationManager getInstance(Context context) {
		if (instance == null) {
			synchronized (MyLocationManager.class) {
				if (instance == null) {
					instance = new MyLocationManager(context);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 构造方法
	 * @param context
	 */
	private MyLocationManager(Context context) {
		mContext = context;
		asyncHttpClient = AsyncHttpClient.getInstance(context);
		asyncTaskManager = AsyncTaskManager.getInstance(mContext);
		locationMgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
    /**
     * 判断GPS是否打开
     * @return
     */
    public boolean isGPSEnabled(){
        return locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    /**
     * 进入设置页面设置GPS
     * @param activity
     */
    public void settingGPS(Activity activity){
    	if(!isGPSEnabled()){
    		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
    		activity.startActivityForResult(intent, Activity.RESULT_OK); 
    	}
    }
    
    /**
     * 开始定位，默认为基站定位
     */
    public void startLocation(){
    	startLocation(LocationManager.NETWORK_PROVIDER);
    }
    
	/**
	 * 开始定位
	 * @param provider
	 */
	public void startLocation(String provider){
		Location location = locationMgr.getLastKnownLocation(provider);
		sendLocation(location);
		locationMgr.requestLocationUpdates(provider, 1000, 0, this);
	}
	
	/**
	 * 停止定位
	 */
	public void stopLocation(){
		if(locationMgr != null){
			locationMgr.removeUpdates(this);
		}
	}
	
	/**
	 * 发生地理位置
	 * @param location
	 */
	public void sendLocation(Location location){
		if(location != null){
			latitude = location.getLatitude();
			longitude = location.getLongitude(); 
			
			RequestParams params = new RequestParams();
			params.put("latlng", latitude+","+longitude);
			params.put("sensor", "true");
			params.put("language", LangManager.getLanguage());
			
			asyncHttpClient.get(Constants.GOOGLE_GEOCODE_PATH, params, new AsyncHttpResponseHandler(){
				
				@Override
				@Deprecated
				public void onSuccess(String result) {
					try {
						if(locationListener != null){
							if(!TextUtils.isEmpty(result)){
								LocationResponse res = JsonMananger.getInstance().jsonToBean(result, LocationResponse.class);
								if(res != null && res.getStatus().equalsIgnoreCase("ok")){
									List<LocationResults> list = res.getResults();
									if(list != null && list.size() > 0){
										String address = list.get(0).getFormatted_address();
										NLog.e(tag, "network latitude: " + latitude + " longitude: " + longitude + " address: " + address);
										locationListener.onLocation(latitude, longitude, address);
									}
								}
							}
						}else{
							NLog.e(tag ,"LocateManager MyLocationListener is not null.");
						}
					} catch (HttpException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				@Deprecated
				public void onFailure(Throwable error, String content) {
					asyncTaskManager.request(REQ_GEOCODER_CODE, MyLocationManager.this);
				}
				
			});
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		sendLocation(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	public MyLocationListener getLocationListener() {
		return locationListener;
	}

	public void setLocationListener(MyLocationListener locationListener) {
		this.locationListener = locationListener;
	}

	@Override
	public Object doInBackground(int requsetCode) throws HttpException {
		try {
			Geocoder gc = new Geocoder(mContext, Locale.getDefault()); 
			List<Address> list = gc.getFromLocation(latitude, longitude, 1);
			
			StringBuilder addressBuilder = new StringBuilder();
			for (Address address : list) { 
				addressBuilder.append(address.getCountryName()); 
				addressBuilder.append(address.getLocality()); 
				for(int i=0;i<address.getMaxAddressLineIndex();i++){
					addressBuilder.append(address.getAddressLine(i));
				}
			}
			
			return addressBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if(result != null){
			if(locationListener != null){
				NLog.e(tag, "Geocoder latitude: " + latitude + " longitude: " + longitude + " address: " + result);
				locationListener.onLocation(latitude, longitude, String.valueOf(result));
			}else{
				NLog.e(tag ,"LocateManager MyLocationListener is not null.");
			}
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}
}
