package org.mm.core.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5CoderUtil {

	public static String len32(String str) {
		String strMd5 = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			
			int i;
			StringBuffer buf = new StringBuffer();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			strMd5 = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return strMd5;
	}
	
	public static String len16(String str) {
		String baseMd5 = len32(str);
		if (baseMd5 != null) baseMd5 = baseMd5.substring(8, 24);
		return baseMd5;
	}
}
