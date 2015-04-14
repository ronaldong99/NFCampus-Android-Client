/*
    Android Client Core, DownloadInfo
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.network.download;

import android.content.Context;

import com.androidapp.framework.network.http.RequestParams;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-13
 * 
 **/
public class DownloadParam {

	private Context context;
	private RequestParams params;
	private String versionNo;
	private String url;
	private String fileName;
	private String downPath;
	private long fileSize;

	public Context getContext() {
		return context;
	}

	public void setContext(Context mContext) {
		this.context = mContext;
	}

	public RequestParams getParams() {
		return params;
	}

	public void setParams(RequestParams params) {
		this.params = params;
	}
	
	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownPath() {
		return downPath;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	
}
