package com.globalways.csscli.tools;

import java.security.MessageDigest;

/**
 * MD5加密的方法类
 * 
 * @author James.Fan
 *
 */
public class MD5 {

	/**
	 * 获取MD5加密字符串
	 * 
	 * @param param
	 *            要加密的字符串
	 * @return 加密字符串
	 */
	public String getMD5(String param) {
		String result = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(param.getBytes());
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			// 16 Encryption
			result = new String(str).substring(8, 24);
			// 32 Encryption
			// result = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
