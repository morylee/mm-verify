package org.mm.core.util;

import org.apache.commons.lang3.StringUtils;

public final class HidingStringUtil {
	public static String hidingEmail(String email) {
		StringBuffer sb;
		try {
			if (email == null || email.length() == 0) return email;
			String[] emails = email.split("@");
			
			sb = new StringBuffer();
			if (emails[0].length() <= 2) {
				sb.append(email.substring(0, 1));
			} else if (emails[0].length() <= 4) {
				sb.append(email.substring(0, 2));
			} else {
				sb.append(email.substring(0, 3));
			}
			sb.append("******@").append(emails[1]);
		} catch (Exception e) {
			return email;
		}
		
		return sb.toString();
	}
	
	public static String hidingIp(String ip) {
		String[] ips;
		try {
			if (ip == null || ip.length() == 0) return ip;
			ips = ip.split("\\.");
			
			for (int i = 0; i < ips.length; i++) {
				if (i > 1) {
					ips[i] = "*";
				}
			}
		} catch (Exception e) {
			return ip;
		}
		
		return StringUtils.join(ips, ".");
	}
	
	public static String defaultHidingString(String str) {
		StringBuffer sb;
		try {
			if (str == null || str.length() == 0) return str;
			int length = str.length();
			
			sb = new StringBuffer();
			if (length <= 2) {
				sb.append(str.substring(0, 1)).append("*");
			} else {
				sb.append(str.substring(0, 1)).append(hideString(length - 2)).append(str.substring(length - 1));
			}
		} catch (Exception e) {
			return str;
		}
		
		return sb.toString();
	}
	
	public static String defaultHidingString(String str, Integer foreLength, Integer endLength) {
		StringBuffer sb;
		try {
			if (str == null || str.length() == 0) return str;
			int length = str.length();
			
			sb = new StringBuffer();
			if (length > foreLength + endLength) {
				sb.append(str.substring(0, foreLength))
				.append(hideString(length - foreLength - endLength))
				.append(str.substring(length - endLength));
			} else {
				return defaultHidingString(str);
			}
		} catch (Exception e) {
			return str;
		}
		
		return sb.toString();
	}
	
	public static String hideString(Integer length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append("*");
		}
		
		return sb.toString();
	}
	
	public static String hidingIDCard(String idCard) {
		return defaultHidingString(idCard, 3, 4);
	}
	
	public static String hidingBankCard(String bankCard) {
		return defaultHidingString(bankCard, 0, 4);
	}
	
	public static String hidingMobile(String mobile) {
		return defaultHidingString(mobile, 3, 4);
	}
	
	public static String hidingAlipay(String alipay) {
		if (alipay == null || alipay.length() == 0) return alipay;
		if (alipay.indexOf("@") > 0) {
			return hidingEmail(alipay);
		} else {
			return hidingMobile(alipay);
		}
	}
	
	public static String hidingWechat(String wechat) {
		return defaultHidingString(wechat, 3, 4);
	}
	
	public static String hidingRealName(String name) {
//		if (name == null || name.length() == 0) return name;
//		Integer length = name.length();
//		if (length <= 2) {
//			return defaultHidingString(name, 1, 0);
//		}
		return defaultHidingString(name, 1, 0);
	}
}
