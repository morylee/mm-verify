package org.mm.core.web.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.mm.core.exception.UnauthException;
import org.mm.core.support.auth.AuthCenter;
import org.mm.model.Account;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	private List<String> fragmentUrls = new ArrayList<>();
	
	public List<String> getFragmentUrls() {
		return fragmentUrls;
	}

	public void setFragmentUrls(List<String> fragmentUrls) {
		this.fragmentUrls = fragmentUrls;
	}
	
	public void appendFragmentUrl(String fragmentUrl) {
		this.fragmentUrls.add(fragmentUrl);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		try {
			Account user = AuthCenter.getUser(request);
			if(user == null) {
				if (checkFragmentUrl(request)) {
					response.sendRedirect("/flogin");
				} else {
					if (isAjax(request)) {
						throw new UnauthException();
					} else {
						AuthCenter.saveInterceptorReq(request);
						response.sendRedirect("/login");
					}
				}
				
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
 
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	}
	
	private boolean checkFragmentUrl(HttpServletRequest request) {
		String reqUrl = request.getRequestURI();
		for (String fragmentUrl: this.fragmentUrls) {
			if (fragmentUrl.endsWith("*")) {
				if (reqUrl.startsWith(fragmentUrl.substring(0, fragmentUrl.length() - 1))) return true;
			} else {
				if (reqUrl.equals(fragmentUrl)) return true;
			}
		}
		return false;
	}
	
	private boolean isAjax(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (accept != null && accept.indexOf("application/json") != -1) return true;
		
		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) return true;
		
		String uri = request.getRequestURI();
		if (StringUtils.containsAny(uri, ".json", ".xml")) return true;
		
		String ajax = request.getParameter("__ajax");
		if (StringUtils.containsAny(ajax, "json", "xml")) return true;
		
		return false;
	}

}
