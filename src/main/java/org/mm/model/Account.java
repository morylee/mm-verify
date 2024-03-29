package org.mm.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Account implements Serializable {

	public enum State {
		Enalbed("启用", 0), Disabled("禁用", 1);
		
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
			case 0:
				return Enalbed;
			default:
				return Disabled;
			}
		}
	}

	private Long id;
	private String name;
	private String mobile;
	private String email;
	private String password;
	private Integer roleType;
	private Integer state;
	private Date createdAt;
	private Date updateAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getRoleType() {
		return roleType;
	}
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
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
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

}
