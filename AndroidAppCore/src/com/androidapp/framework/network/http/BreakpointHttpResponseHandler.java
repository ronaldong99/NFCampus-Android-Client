/*
    Android Client Core, BreakpointHttpResponseHandler
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.network.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import android.content.Context;
import android.text.TextUtils;

import com.androidapp.framework.network.download.DownloadParam;
import com.androidapp.framework.utils.MD5Utils;
import com.androidapp.framework.utils.NLog;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-12
 * 
 **/
public class BreakpointHttpResponseHandler extends AsyncHttpResponseHandler {

	private final String tag = BreakpointHttpResponseHandler.class.getSimpleName();
	
	/** 临时文件后缀 **/ 
	private static final String TEMP_SUFFIX = ".download";

	private Context context;
	/** 请求url **/
	private String url;
	/** 保存文件名称，最好唯一 **/ 
	private String fileName;
	/** 临时文件，上一次保存的数据 **/
	private File tempFile;
	/** 最后目标文件 **/ 
	private File targetFile;

	/** 上一次保存文件的大小 **/
	private long previousFileSize;
	/** 文件总大小 **/ 
	private long totalSize;
	/** 已经下载文件的大小 **/ 
	private long downloadSize;
	/** 文件大小 **/ 
	private long fileSize;
	/** 是否暂停标识  **/ 
	private boolean interrupt = false;
	/** RandomAccessFile **/ 
	private RandomAccessFile randomAccessFile;
	/** RequestParams **/ 
	private RequestParams params;

	/**
	 * 构造方法
	 */
	public BreakpointHttpResponseHandler(DownloadParam downloadParam) {
		this.context = downloadParam.getContext();
		this.url = downloadParam.getUrl();
		this.params = downloadParam.getParams();
		this.fileSize = downloadParam.getFileSize();
		
		if(TextUtils.isEmpty(downloadParam.getFileName())){
			this.fileName = getFileName(url);
		}else{
			this.fileName = downloadParam.getFileName();
		}
		
		this.targetFile = new File(downloadParam.getDownPath(), fileName);
		this.tempFile = new File(downloadParam.getDownPath(), fileName + TEMP_SUFFIX);
	}
	
	/**
	 * 根据url得到文件名
	 * @param url
	 * @return
	 */
	public String getFileName(String url){
		StringBuilder fileName = new StringBuilder(MD5Utils.encrypt(url));
		if(!TextUtils.isEmpty(url)){
			if(url.indexOf(".") > 0){
				int index = url.lastIndexOf(".");
				fileName.append(url.substring(index, url.length()));
			}
		}
		return fileName.toString();
	}

	public void onSuccess(File file) {
		 
    }

    public void onSuccess(int statusCode, File file) {
        onSuccess(file);
    }

    public void onSuccess(int statusCode, Header[] headers, File file) {
        onSuccess(statusCode, file);
    }

    @SuppressWarnings("deprecation")
	public void onFailure(Throwable e, File response) {
        onFailure(e);
    }

    public void onFailure(int statusCode, Throwable e, File response) {
        onFailure(e, response);
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, File response) {
        onFailure(statusCode, e, response);
    }
	    
	@Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onFailure(statusCode, headers, error, getTargetFile());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccess(statusCode, headers, getTargetFile());
    }
	    
	@Override
	public void sendResponseMessage(HttpResponse response) {
		
		if (!Thread.currentThread().isInterrupted() && !interrupt) {
		
			Throwable error = null;
			InputStream instream = null;
			StatusLine status = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			
	        try {	        	
	        	NLog.d(tag, "download fileName: " + fileName);
				if (entity == null) {
					throw new IOException("Fail download. entity is null.");
				}
				
				// 处理contentLength
				long contentLength = entity.getContentLength();
				if(contentLength <= 0){
					if(fileSize > 0){
						contentLength = fileSize;
					}else{
						throw new IOException("Fail download. contentLength = " + contentLength);
					}
				}
	
				//获取当前下载文件流
				instream = entity.getContent();
				if (instream == null) {
					throw new IOException("Fail download. instream is null.");
				}
				
				randomAccessFile = new RandomAccessFile(tempFile, "rw");
//				randomAccessFile.seek(randomAccessFile.length());
				
//				if(tempFile.exists()) {previousFileSize = tempFile.length();}
				previousFileSize = 0;	// 先拿掉断点续传，服务器还没有好。chenwenhan 2014-08-14
				totalSize = contentLength + previousFileSize;

				NLog.d(tag, "previousFileSize: " + 
						previousFileSize + ", " + randomAccessFile.length());
				int length, count = 0;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((length = instream.read(buffer)) != -1 && !Thread.currentThread().isInterrupted() && !interrupt) {
					count += length;
					downloadSize = count + previousFileSize;
					randomAccessFile.write(buffer, 0, length);
					sendProgressMessage((int)downloadSize, (int)totalSize);
				}
				
				NLog.d(tag, "contentLength: " + 
						contentLength + ", count: " + count);				
				
				//判断下载大小与总大小不一致
				if (!Thread.currentThread().isInterrupted() && !interrupt) {
					if (downloadSize != totalSize && totalSize != -1) {
						throw new IOException("Fail download. totalSize not eq downloadSize.");
					}
				}
					
			} catch (IllegalStateException e) {
				e.printStackTrace();
				error = e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				error = e;
			} catch (IOException e) {
				e.printStackTrace();
				error = e;
			}finally {
				try {
					if (instream != null) instream.close();
					if (randomAccessFile != null) randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
					error = e;
				}
			}
	        
	        // additional cancellation check as getResponseData() can take non-zero time to process
			if (!Thread.currentThread().isInterrupted() && !interrupt) {
			    if (status.getStatusCode() >= 300 || error != null) {
			        sendFailureMessage(status.getStatusCode(), response.getAllHeaders(),  error.getMessage().getBytes(), 
			        		new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
			    }else{
			    	if (targetFile.exists() && totalSize == targetFile.length()) {
						 NLog.e(tag, "Output file already exists. Skipping download.");
						 sendProgressMessage((int)totalSize, (int)totalSize);
					}else{
						tempFile.renameTo(targetFile);
					}
	                sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), "success".getBytes());
			    }
			}
		}
	}

	
	public Context getContext() {
		return context;
	}

	public void setContext(Context mContext) {
		this.context = mContext;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	public File getTempFile() {
		return tempFile;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
	}

	public long getPreviousFileSize() {
		return previousFileSize;
	}

	public void setPreviousFileSize(long previousFileSize) {
		this.previousFileSize = previousFileSize;
	}

	public boolean isInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}

	public RequestParams getParams() {
		return params;
	}

	public void setParams(RequestParams params) {
		this.params = params;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
