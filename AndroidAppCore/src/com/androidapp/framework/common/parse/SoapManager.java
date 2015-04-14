/*
    Android Client Core, SoapManager
    Copyright (c) 2015 NF
     
*/

package com.androidapp.framework.common.parse;

import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.text.TextUtils;

import com.androidapp.framework.common.CacheManager;
import com.androidapp.framework.utils.NLog;

/**
 * [SoapObject解析管理类，将SoapObject转成json字符串]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-20
 * 
 **/
public class SoapManager {

	private final String tag = SoapManager.class.getSimpleName();
	/** SoapObject解析对象 **/ 
	private static SoapManager instance;
	
	/**
	 * 得到单例模式SoapManager对象
	 * 
	 * @return
	 */
	public static SoapManager getInstance() {
		if (instance == null) {
			synchronized (SoapManager.class) {
				if (instance == null) {
					instance = new SoapManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * SoapObject转java对象方法，确定结果没有list，可以调用这个方法
	 * @param soapObject
	 * @param clazz
	 * @return
	 */
	public <T> String soapToJson(SoapObject soapObject, Class<T> clazz) { 
		return soapToJson(soapObject, clazz, new String[]{});
	}
	
	/**
	 * SoapObject转java对象方法，如果返回结果中有List对象，需调用改方法
	 * @param soapObject
	 * @param clazz
	 * @param listNodeNames list的节点的名字
	 * @return
	 */
	public <T> String soapToJson(SoapObject soapObject, Class<T> clazz, String...listNodeNames) { 
		
		long start = System.currentTimeMillis();
		
		JSONObject jsonResult = new JSONObject();
		HashSet<String> hashset = new HashSet<String>();
		
		//将SoapObject转json字符串
		getSoapresult(soapObject, jsonResult, hashset);
		
		String json = getSoapJsonResult(jsonResult, hashset, listNodeNames);
		NLog.e(tag, "soapToJson: " + json.toString());
		
		long end = System.currentTimeMillis();
		NLog.e(tag, "soapToJson take time : " + (end-start));
		
		if(NLog.isDebug){
			CacheManager.saveTestData(json, clazz.getSimpleName() + ".txt");
		}
		
        return json;
    }  
	  
	/**
	 * 将SoapObject转json字符串
	 * @param soapresult SoapObject
	 * @param jsonResult JSONObject
	 */
	private void getSoapresult(SoapObject soapresult, JSONObject jsonResult, HashSet<String> hashset){
		try {
			if (soapresult != null) {
				PropertyInfo proinfo = null;
				SoapObject childsoap = null;
				JSONObject childJson = null;
				String curName = null;
				
				int count = soapresult.getPropertyCount();
				for(int i=0; i<count ;i++){
					
					//获取当前节点的信息
					proinfo = new PropertyInfo();
					soapresult.getPropertyInfo(i, proinfo);
					curName = proinfo.getName();
					
					//判断当前节点的类型，如果是SoapPrimitive则可以直接取值，如果是SoapObject则继续解析
					if(SoapPrimitive.class == proinfo.getType()){
						jsonResult.put(curName, proinfo.getValue());
					}
					else if(SoapObject.class == proinfo.getType()){
						
						childJson = new JSONObject();
						childsoap = (SoapObject) soapresult.getProperty(i);
						
						//处理相同名字的节点，到时候好替换成JSONArray
						if(jsonResult.has(curName)){
							hashset.add(curName);
							curName = curName + i;
							hashset.add(curName);
						}
						
						//递归调用自己，获取子节点
						getSoapresult(childsoap, childJson, hashset);
						
						//将子节点添加到根节点下面
						jsonResult.put(curName, childJson);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	  
	/**
	 * 获取SoapObject中数组转化成JSONArray
	 * @param soapresult
	 * @param childJson
	 * @param arrayJson
	 */
	private String getSoapJsonResult(JSONObject jsonResult, HashSet<String> hashset, String...listNodeNames){
		try {
			String jsonkey = "";
			Iterator<?> it = null;
        	JSONObject tempJson = null;
        	JSONArray jsonArray = new JSONArray();
        	
        	if(jsonResult != null && listNodeNames != null && listNodeNames.length > 0){
        		
        		tempJson = new JSONObject(jsonResult.toString());
        		it = tempJson.keys();
        		
        		for(String nodeName : listNodeNames){
        			while(it.hasNext()){
        				jsonkey = it.next().toString();
        				String index = jsonkey.replace(nodeName, "");
        				
        				if(nodeName.equals(jsonkey)){
        					
        					boolean flag = true;
        					JSONObject itemObj = (JSONObject) jsonResult.remove(jsonkey);
        					Iterator<?> itemIt = itemObj.keys();
        					
        					while(itemIt.hasNext()){
                				String itemkey = itemIt.next().toString();
                				Iterator<String> setIt = hashset.iterator();
                	        	while(setIt.hasNext()){
                	        		String setkey = setIt.next().toString();
                	        		if(itemkey.equals(setkey)){
                	        			flag = false;
                	        			jsonArray.put((JSONObject) itemObj.get(itemkey));
                	        			break;
                	        		}
                	        	}
        					}
        					
        					if(flag){
        						jsonArray.put(itemObj);
        					}
        					
        				}else{
        					if(jsonkey.indexOf(nodeName) > -1 && !TextUtils.isEmpty(index) && TextUtils.isDigitsOnly(index) && Integer.parseInt(index) > 0){
        						jsonArray.put((JSONObject) jsonResult.remove(jsonkey));
        					}
        				}
        			}
        			jsonResult.put(nodeName, jsonArray);
        		}
        	}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
}
