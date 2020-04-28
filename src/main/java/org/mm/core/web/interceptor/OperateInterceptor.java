package org.mm.core.web.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mm.core.support.auth.AuthCenter;
import org.mm.model.Account;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class OperateInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		AuthCenter.keepAlive(request);
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		if (modelAndView != null) {
			ModelMap modelMap = modelAndView.getModelMap();
			
			Account user = AuthCenter.getUser(request);
			Account acc = null;
			if (user != null) {
				acc = new Account();
				acc.setId(user.getId());
				acc.setName(user.getName());
				acc.setEmail(user.getEmail());
				acc.setMobile(user.getMobile());
			}
			
			modelMap.put("globalUserInfo", acc);
		}
	}

}
