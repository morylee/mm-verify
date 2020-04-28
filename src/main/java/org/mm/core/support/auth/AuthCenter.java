package org.mm.core.support.auth;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthCenter {

	private static final String LOGIN_INTERCEPTOR_REQUEST = "LOGIN_INTERCEPTOR_REQUEST";
	
	private static AuthCenter authCenter;
	
	@Autowired
	private AuthManager authManager;
	
	@PostConstruct
	public void init() {
		authCenter = this;
	}
	
	public static boolean login(HttpServletRequest request, String authType, String account, String password) {
		Entity entity = new Entity(request.getSession());
		entity.setAuthType(authType);
		entity.setAccount(account);
		entity.setPassword(password);
		
		return authCenter.authManager.login(entity);
	}
	
	public static void keepAlive(HttpServletRequest request) {
		Entity entity = new Entity(request.getSession());
		authCenter.authManager.keepAlive(entity);
	}
	
	public static void logout(HttpServletRequest request) {
		Entity entity = new Entity(request.getSession());
		authCenter.authManager.logout(entity);
	}
	
	public static Account getUser(HttpServletRequest request) {
		Entity entity = new Entity(request.getSession());
		return authCenter.authManager.getUser(entity);
	}
	
	public static void saveInterceptorReq(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute(LOGIN_INTERCEPTOR_REQUEST, request.getRequestURI());
	}
	
	public static String getInterceptorReq(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (String) session.getAttribute(LOGIN_INTERCEPTOR_REQUEST);
	}
	
	public static String popInterceptorReq(HttpServletRequest request) {
		String req = getInterceptorReq(request);
		HttpSession session = request.getSession();
		session.removeAttribute(LOGIN_INTERCEPTOR_REQUEST);
		return req;
	}

}
