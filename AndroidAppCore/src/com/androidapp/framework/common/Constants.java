/*
    Android Client Core, Constant
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.common;

import android.os.Environment;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-7
 * 
 **/
public abstract class Constants {

	/** 公共参数 **/
	public final static String APP_ID = "app_id";
	public final static String APP_ID_VALUE = "2013122400000003";
	public final static String USER_ID = "user_id";
	public final static String TOKEN = "token";
	public final static String SIGN = "sign";
	public final static String CC = "cc";
	public final static String VER = "ver";
	public final static String VER_VALUE = "1.0.0";
	public final static String ACTION = "action";
	public final static String ACTION_REGEX = "action=";
	public final static String AUTHENTICATE = "authenticate";
	public static final String USER_PUBLIC_ID = "USER_PUBLIC_ID"; // 获取技师的pub_id  
	public static final String USER_PUBLIC_NAME = "USER_PUBLIC_NAME"; //获取技师的pub_name 
	
	public static final String THEME = "Theme";
	public static final String LANG = "Lang";
	
	/** SD卡公共路径 **/ 
	public static String LOCAL_BASE_PATH = Environment.getExternalStorageDirectory() + "/cnlaunch/";
		
	/** google根据经纬度得到具体地址url */
	public static final String GOOGLE_GEOCODE_PATH = "http://maps.google.com/maps/api/geocode/json";
	
	/** webservice参数 **/
	public final static String MYCAR_WEBSERVICE_URL = "http://mycar.x431.com/services/";
	public final static String UC_WEBSERVICE_URL = "http://uc.x431.com/services/";
	public final static String WEBSERVICE_NAMESPACE = "http://www.x431.com";
	public final static String WEBSERVICE_SOAPACION = "";

	/** 支付模块参数,正式环境 */
	public static final String ALIPAY_WEBSITE_PATH = "http://mycar.x431.com/services/alipay/enterMobileAlipay.action?orderSn=";
	public static final String PAYPAL_WEBSITE_PATH = "http://mycar.x431.com/services/paypal/enterMobilePaypal.action?orderSn=";
	public static final int PAYPAL_INIT_SUCCESS = 2000;
	
	/**到期提醒**/
	public final static String EXPIRED_REMIND = "expired_remind";	
}
