package org.mm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class Captcha implements Serializable {

	public enum State {
		Unverified("未验证", 0), Verified("已验证", 1), VerifyFailed("验证失败", 2), VerifySuccess("验证成功", 3);
		
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
				return Verified;
			case 2:
				return VerifyFailed;
			case 3:
				return VerifySuccess;
			default:
				return Unverified;
			}
		}
		
		public static final List<Integer> canVerify = new ArrayList<>();
		static {
			canVerify.add(Unverified.value);
			canVerify.add(Verified.value);
		}
	}
	
	private Long id;
	private String apiKey;
	private String webKey;
	private String key;
	private Integer secLevel;
	private Integer secMode;
	private Integer verifyTimes;
	private Integer state;
	private Date createdAt;
	private Date updatedAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
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
	public Integer getVerifyTimes() {
		return verifyTimes;
	}
	public void setVerifyTimes(Integer verifyTimes) {
		this.verifyTimes = verifyTimes;
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
