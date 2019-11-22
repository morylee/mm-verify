package org.mm.core.security;

import org.mm.core.security.coder.BASE64Encoder;

public class Base64CoderUtil {

	/**
	 * BASE64解码
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static final String decrypt(String key) {
		try {
			return new String(new BASE64Encoder().decode(key), "utf-8");
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}

	/**
	 * BASE64编码
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static final String encrypt(String key) {
		try {
			return new BASE64Encoder().encode(key.getBytes("utf-8"));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
	}

}
