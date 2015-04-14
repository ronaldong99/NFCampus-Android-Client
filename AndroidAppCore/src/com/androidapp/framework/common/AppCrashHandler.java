/*
    Android Client Core, AppCrashHandler
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
 
/**
 * [系统异常退出处理类，在系统如果出现异常时，记录异常保存本地或服务器，方便异常定位，并给出用户友好的提示。]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class AppCrashHandler implements UncaughtExceptionHandler {

	@SuppressWarnings("unused")
	private final String tag = AppCrashHandler.class.getSimpleName();
	
	private Context mContext;
	
	//the CrashHandler instance
	private static AppCrashHandler instance;
	
	//the default exception handler class
	private UncaughtExceptionHandler mDefaultHandler;
	
	//the crashReport by save the crash info
	private Properties crashReport = new Properties();
	
	private final String TRACE = "trace"; 
	private final String EXCEPTION = "exception"; 
	private final String VERSIONNAME = "versionName"; 
	private final String VERSIONCODE = "versionCode"; 
	
	private final String PREFIX = "crash_";
	private final String PATTERN = "yyyy-MM-dd hh:mm:ss";
	private final String SUFFIX = ".cr";
	
	
	/**
	 * get the CrashHandler instance
	 * @return
	 */
	public static AppCrashHandler getInstance() {
		if(instance == null){
			instance = new AppCrashHandler();
		}
		return instance;
	}

	/**
	 * init method
	 * @param context
	 */
	public void init(Context context){
		mContext = context;
	    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
	}
	

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handlerException(ex) && mDefaultHandler != null){
			//if user don't handle the Throwable, the mDefaultHandler object will handler it.
			mDefaultHandler.uncaughtException(thread, ex);
		}else{
			try {
				Thread.sleep(5000);
				//the app will exit out after 5s. 
				ActivityPageManager.getInstance().exit(mContext);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * custom handler the exception.
	 * @param ex
	 * @return
	 */
	private boolean handlerException(Throwable ex){
		if(ex == null ){
			return true;
		}
		
		final String msg = ex.getLocalizedMessage();
		if(TextUtils.isEmpty(msg)){
			return false;
		}
		
		new Thread(){

			@Override
			public void run() {
				Looper.prepare();
				//在这里，弹出友好提示框
				
				Looper.loop();
			}
			
		}.start();
		
		//收集设备信息
		conllectCrashDeviceInfo(mContext);
		
		//保存错误报告
		saveCrashInfo(ex);
		
		//发送报告到服务器，删除本地报告
		sendCrashReport();
		
		return true;
	}


	/**
	 * 收集设备信息
	 * @param context
	 */
	private void conllectCrashDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if(pi != null){
				crashReport.put(VERSIONNAME, String.valueOf(pi.versionName));
				crashReport.put(VERSIONCODE, String.valueOf(pi.versionCode));
				
			}
			
			//get the device info by build class 
			Field[] fieldList = Build.class.getDeclaredFields();
			if(fieldList != null){
				for(Field device : fieldList){
					device.setAccessible(true);
					crashReport.put(device.getName(), String.valueOf(device.get(null)));
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存错误报告到本地
	 * @param ex
	 */
	private void saveCrashInfo(Throwable ex) {
		try {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			
			Throwable cause = ex.getCause();
			while(cause != null){
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			
			String result = writer.toString();
			printWriter.close();
			
			crashReport.put(EXCEPTION, ex.getLocalizedMessage());
			crashReport.put(TRACE, result);
			
			String fileName = getCrashFileName();
			FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			crashReport.store(fos, mContext.getPackageName());
			fos.flush();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到日期格式的错误报告文件名
	 * @return
	 */
	public String getCrashFileName(){
		StringBuilder fileName = new StringBuilder(PREFIX);
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
		fileName.append(sdf.format(date));
		
		fileName.append(SUFFIX);
		return fileName.toString();
	}
	
	/**
	 * 发生错误日志报告到服务器，并删除本地报告
	 */
	public void sendCrashReport(){
		File filesDir = mContext.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(SUFFIX);
			}
		};
		
		String[] list = filesDir.list(filter);
		if(list != null && list.length > 0){
			for(String fileName : list){
				File file = new File(mContext.getFilesDir(), fileName);
				if(file.exists()){
					//TODO 发送报告到服务器
					//TODO 删除本地报告
					//file.delete();
				}
			}
		}
	}
}
