package org.mm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mm.core.captcha.CaptchaMode;

@SuppressWarnings("serial")
public class Website implements Serializable {

	public static final Integer USER_MAX_WEBSITE_COUNT = 3;
	
	public static final List<Integer> SEC_MODE_LIST = new ArrayList<>();
	public static final Integer THEME_NUM_MIN = 0;
	public static final Integer THEME_NUM_MAX = 6;
	public static final Double SCALING_RATIO_MIN = 0.8;
	public static final Double SCALING_RATIO_MAX = 1.5;
	
	static {
		SEC_MODE_LIST.add(CaptchaMode.Auto.getValue());
		SEC_MODE_LIST.add(CaptchaMode.Click.getValue());
		SEC_MODE_LIST.add(CaptchaMode.Drag.getValue());
	}
	
	public enum State {
		Default("正常", 0), Deleted("已删除", 1);
		
		private final String name;
		private final Integer value;
		
		private State(String name, Integer value) {
			this.name = name;
			this.value = value;
		}
		public Integer getValue() {
			return this.value;
		}
		public String getName() {
			return this.name;
		}
		
		public static State valueOf(Integer value) {
			switch (value) {
			case 1:
				return Deleted;
			default:
				return Default;
			}
		}
	}
	
	public enum SecLevel {
		Low(1, "低级"), Middle(2, "中级"), High(3, "高级");
		
		private Integer value;
		private String name;
		
		private SecLevel(Integer value, String name) {
			this.value = value;
			this.name = name;
		}
		
		public Integer getValue() {
			return this.value;
		}
		public String getName() {
			return this.name;
		}
		public static SecLevel valueOf(Integer value) {
			switch (value) {
			case 2:
				return Middle;
			case 3:
				return High;
			default:
				return Low;
			}
		}
	}

	private Long id;
	private Long accountId;
	private String name;
	private String url;
	private String apiKey;
	private String webKey;
	private Integer secLevel;
	private Integer secMode;
	private Integer iconType;
	private Integer themeNum;
	private Double scalingRatio;
	private Integer state;
	private Date createdAt;
	private Date updatedAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getWebKey() {
		return webKey;
	}
	public void setWebKey(String webKey) {
		this.webKey = webKey;
	}
	public Integer getSecLevel() {
		return secLevel;
	}
	public void setSecLevel(Integer secLevel) {
		this.secLevel = secLevel;
	}
	public Integer getSecMode() {
		return secMode;
	}
	public void setSecMode(Integer secMode) {
		this.secMode = secMode;
	}
	public Integer getIconType() {
		return iconType;
	}
	public void setIconType(Integer iconType) {
		this.iconType = iconType;
	}
	public Integer getThemeNum() {
		return themeNum;
	}
	public void setThemeNum(Integer themeNum) {
		this.themeNum = themeNum;
	}
	public Double getScalingRatio() {
		return scalingRatio;
	}
	public void setScalingRatio(Double scalingRatio) {
		this.scalingRatio = scalingRatio;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getStateName() {
		return State.valueOf(this.state).getName();
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
