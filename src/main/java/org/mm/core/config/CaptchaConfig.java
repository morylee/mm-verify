package org.mm.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaConfig {

	@Value("${mm.captcha.dir:D:/captcha_lib}")
	public String imgBaseDir;

	@Value("${mm.captcha.theme:theme}")
	public String baseTheme;

	@Value("${mm.captcha.background:background}")
	public String baseBackground;

	@Value("${mm.captcha.imgType:png}")
	public String baseImgType;

	@Value("${mm.captcha.expire:3600}")
	public Integer expireSeconds;

	@Value("${mm.captcha.tokenExpire:300}")
	public Integer tokenExpireSeconds;

	@Value("${mm.captcha.maxFailTimes:3}")
	public Integer maxFailTimes;

}
