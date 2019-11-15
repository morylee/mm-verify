package org.mm.core.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AesCoderUtil {

	//参数分别代表 算法名称/加密模式/数据填充方式
	private static final String ALGORITHM_PKCS5 = "AES/ECB/PKCS5Padding";
	
	/**
	* 加密
	* @param content 加密的字符串
	* @param encryptKey key值
	* @return
	* @throws Exception
	*/
	
	public static String encrypt(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		
		Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "AES");
//		IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = cipher.doFinal(content.getBytes("utf-8"));
		
		// 采用base64算法进行转码,避免出现中文乱码
		return Base64.encodeBase64String(b);
	}
	
	/**
	* 解密
	* @param encryptStr 解密的字符串
	* @param decryptKey 解密的key值
	* @return
	* @throws Exception
	*/
	public static String decrypt(String encryptStr, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		
		Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "AES");
//		IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		// 采用base64算法进行转码,避免出现中文乱码
		byte[] encryptBytes = Base64.decodeBase64(encryptStr);
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		
		return new String(decryptBytes);
	}

}
