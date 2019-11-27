package org.mm.service;

import java.util.HashMap;
import java.util.Map;

import org.mm.mapper.CaptchaMapper;
import org.mm.model.Captcha;
import org.mm.model.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

	@Autowired
	private CaptchaMapper mapper;
	
	public void add(Captcha captcha) {
		mapper.add(captcha);
	}
	
	public void update(Captcha captcha) {
		mapper.update(captcha);
	}
	
	public Captcha findByParams(Map<String, Object> params) {
		return mapper.findByParams(params);
	}
	
	public Captcha create(String key, Website website) {
		Captcha captcha = new Captcha();
		captcha.setApiKey(website.getApiKey());
		captcha.setWebKey(website.getWebKey());
		captcha.setKey(key);
		captcha.setSecLevel(website.getSecLevel());
		captcha.setSecMode(website.getSecMode());
		captcha.setVerifyTimes(0);
		captcha.setState(Captcha.State.Unverified.getValue());
		this.add(captcha);
		
		return captcha;
	}
	
	public void verify(String key, Captcha.State state) {
		Captcha captcha = this.findByKey(key);
		if (captcha == null) {
			System.out.println("无效的验证码：" + key);
			return;
		}
		
		captcha.setVerifyTimes(captcha.getVerifyTimes() + 1);
		captcha.setState(state.getValue());
		this.update(captcha);
	}
	
	public Captcha findByKey(String key) {
		if (key == null || "".equals(key)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		
		return this.findByParams(params);
	}

}
