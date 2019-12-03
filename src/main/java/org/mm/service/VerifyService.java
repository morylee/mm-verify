package org.mm.service;

import java.util.HashMap;
import java.util.Map;

import org.mm.core.Constants;
import org.mm.core.captcha.CaptchaUtil;
import org.mm.core.exception.NotFoundException;
import org.mm.core.util.RedisUtil;
import org.mm.model.Captcha;
import org.mm.model.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyService {

	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private CaptchaService captchaService;
	
	/**
	 * 生成验证码
	 * @param website 网站信息
	 * @return
	 */
	public Map<String, Object> init(Website website) {
		String bgPath = null;
		if (redisUtil.hasKey(website.getWebKey())) {
			Map<Object, Object> tempBg = redisUtil.hmget(website.getWebKey());
			if (tempBg != null && !tempBg.isEmpty()) {
				Integer usedTimes = (Integer) tempBg.get("times");
				if (usedTimes == null) usedTimes = 0;
				if (usedTimes < 3) {
					bgPath = (String) tempBg.get("background");
					redisUtil.hset(website.getWebKey(), "times", usedTimes + 1);
				} else {
					redisUtil.del(website.getWebKey());
				}
			}
		}
		if (bgPath == null) {
			bgPath = CaptchaUtil.backgroundPath(website.getThemeNum(), website.getScalingRatio());
			if (bgPath == null) throw new NotFoundException("验证码生成失败，请重试！");
			redisUtil.hset(website.getWebKey(), "background", bgPath);
			redisUtil.hset(website.getWebKey(), "times", 1);
		}
		
		Map<String, Object> result = CaptchaUtil.securityGenerate(bgPath, website.getSecMode(), website.getSecLevel());
		Map<String, Object> resPub = new HashMap<String, Object>();
		String key = (String) result.get("key");
		resPub.put("success", result.get("ok"));
		resPub.put("md", website.getSecMode());
		resPub.put("k", key);
		resPub.put("rpk", result.get("rsaPubKey"));
		resPub.put("bg", result.get("background"));
		resPub.put("srs", result.get("series"));
		resPub.put("cls", result.get("cols"));
		resPub.put("gd", result.get("guide"));
		resPub.put("ih", result.get("iconY"));
		resPub.put("n", result.get("times"));
		captchaService.create(key, website);
		redisUtil.hset(key, Constants.REQUEST_ORIGIN_KEY, website.getUrl()); // 用于验证校验验证码请求的源地址
		
		return resPub;
	}
	
	/**
	 * 验证
	 * @param key 验证码Key
	 * @param positions 用户交互坐标
	 * @return
	 */
	public Map<String, Object> verify(String key, String[][] positions) {
		Map<String, Object> result = new HashMap<>();
		
		boolean success = CaptchaUtil.securityVerify(key, positions);
		boolean usable = CaptchaUtil.usable(key);
		result.put("success", success);
		result.put("expired", !usable);
		if (success) {
			Captcha captcha = captchaService.findByKey(key);
			String token = CaptchaUtil.generateToken(captcha.getApiKey(), key);
			if (token == null) {
				captchaService.verify(key, Captcha.State.VerifyFailed);
				result.put("success", false);
			} else {
				captchaService.verify(key, Captcha.State.VerifySuccess);
				result.put("result", token);
			}
		} else {
			if (usable) {
				captchaService.verify(key, Captcha.State.Verified);
			} else {
				captchaService.verify(key, Captcha.State.VerifyFailed);
			}
		}
		
		return result;
	}

}
