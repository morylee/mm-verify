package org.mm.core.util;

import java.util.HashMap;
import java.util.Map;

import org.mm.core.support.email.Email;
import org.mm.core.support.email.EmailModule;
import org.mm.core.support.email.EmailSender;

/**
 * 发送邮件辅助类
 * 
 * @author LiChenhui
 * @version 2017-09-14 14:58
 */
public final class EmailUtil {
	private EmailUtil() {
	}

	/**
	 * 发送邮件
	 */
	public static final boolean sendEmail(Email email) {
		// 初始化邮件引擎
		EmailSender sender = new EmailSender(email.getHost());
		sender.setNamePass(email.getName(), email.getPassword(), email.getKey());
		if (sender.setFrom(email.getFrom()) == false)
			return false;
		if (sender.setTo(email.getSendTo()) == false)
			return false;
		if (email.getCopyTo() != null && sender.setCopyTo(email.getCopyTo()) == false)
			return false;
		if (sender.setSubject(email.getTopic()) == false)
			return false;
		if (sender.setBody(email.getBody()) == false)
			return false;
		if (email.getFileAffix() != null) {
			for (int i = 0; i < email.getFileAffix().length; i++) {
				if (sender.addFileAffix(email.getFileAffix()[i]) == false)
					return false;
			}
		}
		// 发送
		return sender.sendout();
	}
	
	public static final boolean sendRegisterCaptcha(String email, String captcha, String path) {
		if (captcha == null || "".equals(captcha)) return false;
		
		Map<String, String> params = new HashMap<>();
		params.put("title", "用户注册");
		params.put("captcha", captcha);
		params.put("path", path);
		String body = EmailModule.sendCaptchaModule(params);
		
		Email emailObj = new Email(email, "用户注册 - 可信验", body);
		return sendEmail(emailObj);
	}

}
