package org.mm.core.captcha;

public enum CaptchaMode {

	Click(0, "点击验证"), Drag(1, "拖拽验证"), Auto(2, "随机验证");
	Integer value;
	String name;
	
	CaptchaMode(Integer value, String name) {
		this.value = value;
		this.name = name;
	}
	public Integer getValue() {
		return this.value;
	}
	public String getName() {
		return this.name;
	}
	public static CaptchaMode valueOf(Integer value) {
		switch (value) {
		case 1:
			return Drag;
		case 2:
			return Auto;
		default:
			return Click;
		}
	}

}
