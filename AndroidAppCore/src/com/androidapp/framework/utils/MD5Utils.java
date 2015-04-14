/*
    Android Client Core, MD5Utils
    Copyright (c) 2015 NF
     
*/

package com.androidapp.framework.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * [A brief description]
 * 
 * @author mashidong
 * @version 1.0
 * @date 2014-3-6
 * 
 **/
public class MD5Utils {

	public static String encrypt(String str){
		
        MessageDigest messageDigest = null;
        try{
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e){
        	e.printStackTrace();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++){
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1){
            	md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            }else{
            	md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String signByMD5(String source) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(source.getBytes());
        byte[] result = md5.digest();
        return toHexString(result);
    }
    
    private static String toHexString(byte[] source) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexResult = new char[source.length * 2];
        int index = 0;
        for (byte b : source) {
            hexResult[index++] = hexChars[b >>> 4 & 0xf];
            hexResult[index++] = hexChars[b & 0xf];
        }
        return new String(hexResult);
    }
}
