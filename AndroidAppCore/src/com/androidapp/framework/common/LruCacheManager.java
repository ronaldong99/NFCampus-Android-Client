/*
    Android Client Core, LruCacheManager
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.common;

import android.util.LruCache;

/**
 * [LruCacheManager, 该缓存类为内存静态缓存，该类可以设置缓存个数，
 * 以先进先出的方式缓存对象，所以不用去担心释放问题，可用于页面之前传大量数据的参数]
 * 
 * @author: devin.hu
 * @version: 1.0
 * @date: Oct 29, 2013
 */
public class LruCacheManager {

	/**
	 * lruCache缓存对象
	 */
	private LruCache<String, Object> lruCache;
	/**
	 * LruCacheManager实例
	 */
	private static LruCacheManager instance;
	/**
	 * 缓存对象个数
	 */
	private static int CACHE_SIZE = 30;

	
	/**
	 *  构造方法
	 * @param cacheSize 缓存对象个数
	 */
	private LruCacheManager(int cacheSize) {
		this.lruCache = new LruCache<String, Object>(cacheSize);
	}

	/**
	 * 得到单例模式，默认缓存个数为30的LruCacheManager对象
	 * @return
	 */
	public static LruCacheManager getInstance() {
		return getInstance(CACHE_SIZE);
	}
	
	/**
	 * 得到单例模式LruCacheManager对象，缓存大小为cacheSize
	 * @param cacheSize 缓存个数
	 * @return
	 */
	public static LruCacheManager getInstance(int cacheSize) {
		if (instance == null) {
			synchronized (LruCacheManager.class) {
				if (instance == null) {
					instance = new LruCacheManager(cacheSize);
				}
			}
		}
		return instance;
	}

	/**
	 * put
	 * @param key String
	 * @param value Object
	 */
	public void put(String key, Object value) {
		lruCache.put(key, value);
	}

	/**
	 * get
	 * @param key String
	 * @return Object
	 */
	public Object get(String key) {
		return lruCache.get(key);
	}

	/**
	 * remove 
	 * @param key String
	 * @return Object
	 */
	public Object remove(String key) {
		return lruCache.remove(key);
	}

	/**
	 * evictAll
	 */
	public void evictAll() {
		lruCache.evictAll();
	}

	/**
	 * maxSize
	 * @return int
	 */
	public int maxSize() {
		return lruCache.maxSize();
	}

	/**
	 * size
	 * @return int
	 */
	public int size() {
		return lruCache.size();
	}

	/**
	 * trimToSize
	 * @param maxSize
	 */
	public void trimToSize(int maxSize) {
		lruCache.trimToSize(maxSize);
	}
}
