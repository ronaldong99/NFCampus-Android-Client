/*
    Android Client Core, LangInfo
    Copyright (c) 2015 NF
     
 */
package com.androidapp.framework.utils.lang;

/**
 * [语言信息model]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-21
 * 
 **/
public class LangInfo {

	private String langId;
	private String language;
	private String code;
	private String code1;

	public LangInfo() {

	}
	
	/**
	 * 构造方法
	 * @param langId
	 * @param language
	 * @param code
	 */
	public LangInfo(String langId, String language, String code) {
		this.langId = langId;
		this.language = language;
		this.code = code;
	}

	/**
	 * 构造方法
	 * @param langId
	 * @param language
	 * @param code
	 * @param code1
	 */
	public LangInfo(String langId, String language, String code, String code1) {
		this.langId = langId;
		this.language = language;
		this.code = code;
		this.code1 = code1;
	}

	public String getLangId() {
		return langId;
	}

	public void setLangId(String langId) {
		this.langId = langId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

}
