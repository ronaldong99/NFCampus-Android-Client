/*
    Android Client Core, ErrorCode
    Copyright (c) 2015 NF
     
*/

package com.androidapp.framework.utils;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-26
 * 
 **/
public class ErrorCode {

	/** 未知错误 **/ 
	public static int UNKNOW = 1000;
	/** 变量未定义 **/ 
	public static int VAR_UNDEFINED = 1001;
	/** 变量索引未定义 **/ 
	public static int INDEX_UNDEFINED = 1002; 
	/** 常量未定义 **/ 
	public static int CONST_UNDEFINED = 1003;
	/** 参数无效 **/ 
	public static int PARAM_INVALID = 1015;
	/** 缺少参数 **/ 
	public static int PARAM_MISSED = 1016;
	/** 定义错误 **/ 
	public static int DEFINED_INVALID = 1017;
	
	/** 输入的参数无效 **/ 
	public static int INPUT_PARAM_INVALID = 1022;
	/** 缺少输入参数 **/ 
	public static int INPUT_PARAM_MISSED = 1023;
	/**  **/ 
    public static int INPUT_PARAM_NONUNIQUE = 1024;// input param not unique
	/**  **/ 
    public static int INPUT_VERIFY_CODE_MISSED = 1025;  // input verify code missed

	/** 会话验证失败，请重新登 **/ 
	public static int AUTH_FAILED = 1201; //401
	/**  **/ 
	public static int SESSION_INVALID = 1211;
	/**  **/ 
	public static int SESSION_TIMEOUT = 1212;
	/**  **/ 
	public static int SESSION_ID_INVALID = 1213;
	/** 签名验证失败 **/ 
	public static int SIGN_FAILED = 1214;
	
	/** 文件未找到 **/ 
	public static int FILE_NOTFOUND = 1301;

	/** 数据库查询失败 **/ 
	public static int DB_FAILED = 1401;
	/** 数据库连接失败 **/ 
	public static int DB_CONNECT_FAILED = 1402;

	/** 文件访问失败 **/ 
	public static int FILE_FAILED = 1500;
	/** 创建目录失败 **/ 
	public static int MKDIR_FAILED = 1510;
	
	/** 网络访问失败 **/ 
	public static int NETWORK_FAILED = 1600;

	//logic 17**
	/**  **/ 
	public static int CONFIG_TYPE_INVALID = 1701;
	/**  **/ 
	public static int CONFIG_ITEM_INVALID = 1702;
	/**  **/ 
	public static int SERIALIZE_FAILED = 1731;
	/**  **/ 
	public static int UNSERIALIZE_FAILED = 1732;
	/**  **/ 
	public static int PACK_FAILED = 1733;
	/** 数据解包失败 **/ 
	public static int UNPACK_FAILED = 1734;
	/** 项目未找到  **/ 
	public static int ITEM_NOT_FOUND = 1741;

	/** 图片类型无效，可能是类型不支持 **/ 
	public static int IMAGE_TYPE_INVALID = 1751;
	/** 图片写入文件失败 **/ 
	public static int IMAGE_SAVE_FAILED = 1752;
	/** 令牌验证失败 **/ 
	public static int TOKEN_VERIFY_FAILED = 1761;
}
