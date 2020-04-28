package org.mm.core.support.auth;

import javax.servlet.http.HttpSession;

public class Entity {

	public static final String EMAIL_TYPE = "email";
	public static final String MOBILE_TYPE = "mobile";
	public static final String ACCOUNT_TYPE = "account";
	
	private HttpSession session;
	private String authType;
	private String account;
	private String password;
	
	public Entity() {
		
	}
	
	public Entity(HttpSession session) {
		this.session = session;
	}
	
	public HttpSession getSession() {
		return session;
	}
	public void setSession(HttpSession session) {
		this.session = session;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isNull() {
		return this.authType == null || "".equals(this.authType)
			|| this.account == null || "".equals(this.account)
			|| this.password == null || "".equals(this.password)
			|| this.session == null;
	}

}
