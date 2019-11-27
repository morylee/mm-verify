package org.mm.core.captcha;

public enum CaptchaMode {

	Auto(2), Drag(1), Click(0);
	Integer value;
	
	CaptchaMode(Integer value) {
		this.value = value;
	}
	public Integer getValue() {
		return this.value;
	}

}
