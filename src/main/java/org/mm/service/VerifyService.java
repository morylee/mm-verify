package org.mm.service;

import java.util.HashMap;
import java.util.Map;

import org.mm.core.img.ImageCaptchaUtil;
import org.mm.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyService {

	public static class Account {
		private String webKey;
		private Integer secLevel;
		private Integer captchaMode;
		private Integer themeNum;
		private Double size;
		
		public Account(String webKey, Integer secLevel, Integer captchaMode, Integer themeNum, Double size) {
			this.webKey = webKey;
			this.secLevel = secLevel;
			this.captchaMode = captchaMode;
			this.themeNum = themeNum;
			this.size = size;
		}
		
		public String getWebKey() {
			return this.webKey;
		}
		public Integer getSecLevel() {
			return this.secLevel;
		}
		public Integer getCaptchaMode() {
			return this.captchaMode;
		}
		public Integer getThemeNum() {
			return this.themeNum;
		}
		public Double getSize() {
			return this.size;
		}
	}
	
	public static Account account= new Account("0a1b2c3d4e5f6a7b8c", 3, ImageCaptchaUtil.CaptchaMode.Click.getValue(), null, 1.5);
	
	@Autowired
	private RedisUtil redisUtil;
	
	public Map<String, Object> init(Account account) {
		String bgPath = null;
		if (redisUtil.hasKey(account.getWebKey())) {
			Map<Object, Object> tempBg = redisUtil.hmget(account.getWebKey());
			if (tempBg != null && !tempBg.isEmpty()) {
				Integer usedTimes = (Integer) tempBg.get("times");
				if (usedTimes < 3) {
					bgPath = (String) tempBg.get("background");
					redisUtil.hset(account.getWebKey(), "times", usedTimes + 1);
				} else {
					redisUtil.del(account.getWebKey());
				}
			}
		}
		if (bgPath == null) {
			bgPath = ImageCaptchaUtil.backgroundPath(account.getThemeNum());
			redisUtil.hset(account.getWebKey(), "background", bgPath);
			redisUtil.hset(account.getWebKey(), "times", 1);
		}
		
		Map<String, Object> result = ImageCaptchaUtil.securityGenerate(bgPath, account.getCaptchaMode(), account.getSecLevel());
		Map<String, Object> resPub = new HashMap<String, Object>();
		resPub.put("success", result.get("ok"));
		resPub.put("md", account.getCaptchaMode());
		resPub.put("k", result.get("key"));
		resPub.put("rpk", result.get("rsaPubKey"));
		resPub.put("bg", result.get("background"));
		resPub.put("srs", result.get("series"));
		resPub.put("cls", result.get("cols"));
		resPub.put("gd", result.get("guide"));
		resPub.put("ih", result.get("iconY"));
		resPub.put("n", result.get("times"));
		
		return resPub;
	}
	
	public Map<String, Object> verify(String key, String[][] positions) {
		Map<String, Object> result = new HashMap<>();
		
		boolean isExpire = ImageCaptchaUtil.isExpire(key);
		result.put("expired", isExpire);
		if (isExpire) {
			result.put("success", false);
			return result;
		}
		
		boolean success = ImageCaptchaUtil.securityVerify(key, positions);
		result.put("success", success);
		if (success) {
			String token = ImageCaptchaUtil.generateToken(account.getWebKey(), key);
			if (token == null) {
				result.put("success", false);
			} else {
				result.put("result", token);
			}
		}
		
		return result;
	}

}
