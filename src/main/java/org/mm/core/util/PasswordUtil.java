package org.mm.core.util;

import org.mm.core.security.Md5CoderUtil;

public class PasswordUtil {

	public static String securityPwd(String password) {
		int midIndex = password.length() / 2;
		String newPwd = password.substring(0, midIndex) + Md5CoderUtil.len16(password) + password.substring(midIndex);
		return Md5CoderUtil.len32(newPwd);
	}
	
	public static String securityPwd(String password, String salt) {
		int midIndex = password.length() / 2;
		String newPwd = password.substring(0, midIndex) + salt + password.substring(midIndex);
		return Md5CoderUtil.len32(newPwd);
	}

}
