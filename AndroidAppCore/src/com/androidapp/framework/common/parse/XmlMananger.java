/*
    Android Client Core, JsonMananger
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.common.parse;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * [XML解析管理类]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2015-04-13
 * 
 **/
public class XmlMananger {

	@SuppressWarnings("unused")
	private final String tag = XmlMananger.class.getSimpleName();

	/** xmlMapper对象，采用XStream解析 **/
	private XStream xmlMapper;
	/** JsonMananger实例对象 **/
	private static XmlMananger instance;

	/**
	 * 构造方法
	 */
	private XmlMananger() {
		if (xmlMapper == null) {
			//XppDriver比DomDriver更快
			xmlMapper = new XStream(new XppDriver()) {
				@Override
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new MapperWrapper(next) {
						@SuppressWarnings("rawtypes")
						@Override
						public boolean shouldSerializeMember(Class definedIn, String fieldName) {
							if (definedIn == Object.class) {
								return false;
							}
							return super.shouldSerializeMember(definedIn, fieldName);
						}
					};
				}
			};
			//自动检测注解模式，会使XStream的解析变慢
			//xmlMapper.autodetectAnnotations(true);
		}
	}

	/**
	 * 得到单例模式XmlMananger对象
	 * 
	 * @return
	 */
	public static XmlMananger getInstance() {
		if (instance == null) {
			synchronized (XmlMananger.class) {
				if (instance == null) {
					instance = new XmlMananger();
				}
			}
		}
		return instance;
	}

	/**
	 * 将xml转化成java对象 
	 * @param xml
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T xmlToBean(String xml, Class<T> cls) {
		xmlMapper.processAnnotations(cls);
		T obj = (T) xmlMapper.fromXML(xml);
		return obj;
	}

	/**
	 * 将xml流对象转化成java对象
	 * @param xml
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T xmlToBean(InputStream xml, Class<T> cls) {
		xmlMapper.processAnnotations(cls);
		T obj = (T) xmlMapper.fromXML(xml);
		return obj;
	}

	/**
	 * 将java对象转化成xml
	 * @param xml
	 * @param cls
	 * @return
	 */
	public String beanToXml(Object obj) {
		return xmlMapper.toXML(obj);
	}
	
	/**
	 * 得到XStream解析对象
	 * @return
	 */
	public XStream getXmlMapper() {
		return xmlMapper;
	}

}
