/*
    Android Client Core, ActivityPageManager
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.common;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * [页面堆栈管理类，建议在Baseactivity中实现即可，退出系统请调用exit方法]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class ActivityPageManager {

	private static Stack<Activity> activityStack;
	private static ActivityPageManager instance;


	/**
	 * get the AppManager instance, the AppManager is singleton.
	 */
	public static ActivityPageManager getInstance() {
		if (instance == null) {
			instance = new ActivityPageManager();
		}
		return instance;
	}

	/**
	 * add Activity to Stack
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * remove Activity from Stack
	 */
	public void removeActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.remove(activity);
	}

	/**
	 * get current activity from Stack
	 */
	public Activity currentActivity() {
		if (activityStack != null && activityStack.size() > 0) {
			Activity activity = activityStack.lastElement();
			return activity;
		}
		return null;
	}

	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	@SuppressWarnings("rawtypes")
	public void popAllActivityExceptOne(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}

			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * finish current activity from Stack
	 */
	public void finishActivity() {
		if (activityStack != null && activityStack.size() > 0) {
			Activity activity = activityStack.lastElement();
			finishActivity(activity);
		}
	}

	/**
	 * finish the Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * finish the Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * finish all Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * release all resourse for view
	 * 
	 * @param view
	 */
	public static void unbindReferences(View view) {
		try {
			if (view != null) {
				view.destroyDrawingCache();
				unbindViewReferences(view);
				if (view instanceof ViewGroup) {
					unbindViewGroupReferences((ViewGroup) view);
				}
			}
		} catch (Throwable e) {
			// whatever exception is thrown just ignore it because a crash is
			// always worse than this method not doing what it's supposed to do
		}
	}

	private static void unbindViewGroupReferences(ViewGroup viewGroup) {
		int nrOfChildren = viewGroup.getChildCount();
		for (int i = 0; i < nrOfChildren; i++) {
			View view = viewGroup.getChildAt(i);
			unbindViewReferences(view);
			if (view instanceof ViewGroup)
				unbindViewGroupReferences((ViewGroup) view);
		}
		try {
			viewGroup.removeAllViews();
		} catch (Throwable mayHappen) {
			// AdapterViews, ListViews and potentially other ViewGroups don't
			// support the removeAllViews operation
		}
	}

	@SuppressWarnings("deprecation")
	private static void unbindViewReferences(View view) {
		// set all listeners to null (not every view and not every API level
		// supports the methods)
		try {
			view.setOnClickListener(null);
			view.setOnCreateContextMenuListener(null);
			view.setOnFocusChangeListener(null);
			view.setOnKeyListener(null);
			view.setOnLongClickListener(null);
			view.setOnClickListener(null);
		} catch (Throwable mayHappen) {
			// todo
		}

		// set background to null
		Drawable d = view.getBackground();
		if (d != null) {
			d.setCallback(null);
		}

		if (view instanceof ImageView) {
			ImageView imageView = (ImageView) view;
			d = imageView.getDrawable();
			if (d != null) {
				d.setCallback(null);
			}
			imageView.setImageDrawable(null);
			imageView.setBackgroundDrawable(null);
		}

		// destroy WebView
		if (view instanceof WebView) {
			WebView webview = (WebView) view;
			webview.stopLoading();
			webview.clearFormData();
			webview.clearDisappearingChildren();
			webview.setWebChromeClient(null);
			webview.setWebViewClient(null);
			webview.destroyDrawingCache();
			webview.destroy();
			webview = null;
		}

		if (view instanceof ListView) {
			ListView listView = (ListView) view;
			try {
				listView.removeAllViewsInLayout();
			} catch (Throwable mayHappen) {
			}
			((ListView) view).destroyDrawingCache();
		}
	}

	/**
	 * exit System
	 */
	@SuppressWarnings("deprecation")
	public void exit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			PreferencesManager.getInstance(context).clearAll();
			LruCacheManager.getInstance().evictAll();
			CacheManager.clearAll();
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
