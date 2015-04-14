/*
    Android Client Core, NToast
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * [Toast工具类]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-13
 * 
 **/
public class NToast {
	
	public static void shortToast(Context context, int resId) {
		showToast(context, context.getString(resId), Toast.LENGTH_SHORT);
	}
	
	public static void shortToast(Context context, String text) {
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	public static void longToast(Context context, int resId) {
		showToast(context, context.getString(resId), Toast.LENGTH_LONG);
	}
	
	public static void longToast(Context context, String text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}
	
	public static void showToast(Context context, String text, int duration) {
		if(!TextUtils.isEmpty(text)){
			Toast.makeText(context, text, duration).show();
		}
	}
}
