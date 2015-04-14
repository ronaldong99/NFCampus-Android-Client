/*
    Android Client Core, LangData
    Copyright (c) 2015 NF
     
 */

package com.androidapp.framework.utils.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;

import com.androidapp.framework.utils.NLog;

/**
 * [语言管理类，得到各国的语言id和语言简码]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-21
 * 
 **/
public class LangManager {

	private static final String tag = LangManager.class.getSimpleName();

	/**
	 * 获取所有用到的国家语言列表信息
	 * @return
	 */
	public static List<LangInfo> getLangList() {

		List<LangInfo> list = new ArrayList<LangInfo>();

		list.add(new LangInfo("1", "德语", Lang.DE));
		list.add(new LangInfo("2", "日文", Lang.JP));
		list.add(new LangInfo("3", "俄罗斯", Lang.RU));
		list.add(new LangInfo("4", "法语", Lang.FR));
		list.add(new LangInfo("5", "西班牙", Lang.ES));
		list.add(new LangInfo("6", "葡萄牙", Lang.PT, Lang.BR));
		list.add(new LangInfo("7", "波兰", Lang.PL));
		list.add(new LangInfo("8", "土耳其", Lang.TR));
		list.add(new LangInfo("9", "荷兰语", Lang.NL));
		list.add(new LangInfo("10", "希腊", Lang.GR));
		list.add(new LangInfo("11", "匈牙利语", Lang.HU));
		list.add(new LangInfo("12", "阿拉伯语", Lang.AR, Lang.EG));
		list.add(new LangInfo("13", "丹麦语", Lang.DA, Lang.DK));
		list.add(new LangInfo("14", "韩语", Lang.KO, Lang.KR));
		list.add(new LangInfo("15", "波斯语", Lang.FA, Lang.IR));
		list.add(new LangInfo("16", "罗马尼亚语", Lang.RO));
		list.add(new LangInfo("17", "塞尔维亚语", Lang.SR, Lang.RS));
		list.add(new LangInfo("18", "芬兰语", Lang.FI));
		list.add(new LangInfo("19", "瑞典语", Lang.SV, Lang.SE));
		list.add(new LangInfo("20", "捷克语", Lang.CS, Lang.CZ));
		list.add(new LangInfo("221", "香港", Lang.HK, Lang.TW));
		list.add(new LangInfo("1001", "英语", Lang.EN));
		list.add(new LangInfo("1002", "中文", Lang.CN));
		list.add(new LangInfo("1003", "意大利", Lang.IT));
		
		return list;
	}

	/**
	 * 根据code得到langId，默认是英语
	 * 
	 * @param langCode
	 * @return
	 */
	public static String getLangId(String langCode) {
		if (TextUtils.isEmpty(langCode)) {
			NLog.e(tag, "getLangId langCode is not null.");
			return "1001";
		}

		List<LangInfo> list = getLangList();
		for (LangInfo bean : list) {
			if (langCode.equalsIgnoreCase(bean.getCode())
					|| langCode.equalsIgnoreCase(bean.getCode1())) {
				return bean.getLangId();
			}
		}

		return "1001";
	}

	/**
	 * 根据langId得到Code，默认是英语
	 * 
	 * @param langId
	 * @return
	 */
	public static String getLangCode(String langId) {
		if (TextUtils.isEmpty(langId)) {
			NLog.e(tag, "getLangCode langId is not null.");
			return Lang.EN;
		}

		List<LangInfo> list = getLangList();
		for (LangInfo bean : list) {
			if (langId.equals(bean.getLangId())) {
				return bean.getCode();
			}
		}

		return Lang.EN;
	}

	/**
	 * 根据langId得到Code1, 如果code1为空则返回code，默认是英语
	 * 
	 * @param langId
	 * @return
	 */
	public static String getLangCode1(String langId) {
		if (TextUtils.isEmpty(langId)) {
			NLog.e(tag, "getLangCode langId is not null.");
			return Lang.EN;
		}

		List<LangInfo> list = getLangList();
		for (LangInfo bean : list) {
			if (langId.equals(bean.getLangId())) {
				if (TextUtils.isEmpty(bean.getCode1())) {
					return bean.getCode();
				}
				return bean.getCode1();
			}
		}

		return Lang.EN;
	}

	/**
	 * 获取手机当前语言
	 * 
	 * @return
	 */
	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * 获取手机当前语言的区域
	 * 
	 * @return
	 */
	public static String getCountry() {
		return Locale.getDefault().getCountry();
	}
}
