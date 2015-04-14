/*
    Android Client Core, MyLocationListener
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.utils.location;


/**
 * [地里位置监听]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-4-11
 * 
 **/
public interface MyLocationListener {

	/**
	 * 获取地里位置
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @param address 对应经纬度的地址信息
	 */
	public void onLocation(double latitude, double longitude, String address);
}
