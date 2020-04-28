package org.mm.core.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.annotation.PropertySource;

/**
 * 加载配置
 * 
 * @author LiChenhui
 * @version 2017年9月5日 下午3:19:19
 */
@PropertySource(value = {"classpath:i18n/messages*.properties"})
public final class Resources {
	/** 国际化信息 */
	private static final Map<String, ResourceBundle> MESSAGES = new HashMap<String, ResourceBundle>();

	/** 国际化信息 */
	public static String getMessage(String key, Object... params) {
		//Locale locale = LocaleContextHolder.getLocale();
		// 写死中国时区
		Locale locale = Locale.CHINA;
		ResourceBundle message = MESSAGES.get(locale.getLanguage());
		if (message == null) {
			synchronized (MESSAGES) {
				message = MESSAGES.get(locale.getLanguage());
				if (message == null) {
					message = ResourceBundle.getBundle("i18n/messages", locale);
					MESSAGES.put(locale.getLanguage(), message);
				}
			}
		}
		if (params != null && params.length > 0) {
			return String.format(message.getString(key), params);
		}
		return message.getString(key);
	}

	/** 清除国际化信息 */
	public static void flushMessage() {
		MESSAGES.clear();
	}
}
