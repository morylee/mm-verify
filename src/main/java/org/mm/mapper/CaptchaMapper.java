package org.mm.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.mm.model.Captcha;

@Mapper
public interface CaptchaMapper {

	public void add(Captcha captcha);
	public void update(Captcha captcha);
	public Captcha findByParams(Map<String, Object> params);

}
