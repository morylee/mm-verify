package org.mm.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Website implements Serializable {

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
	
	public static final Integer LOWEST_SECURITY_LEVEL = 1;
	public static final Integer HIGHEST_SECURITY_LEVEL = 3;
	public static final Double LOWEST_SCALING_RATIO = 0.8;
	public static final Double HIGHEST_SCALING_RATIO = 1.5;

	private Long id;
	private Long accountId;
	private String url;
	private String apiKey;
	private String webKey;
	private Integer secLevel;
	private Integer secMode;
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
