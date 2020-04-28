package org.mm.controller;

import javax.servlet.http.HttpServletRequest;

import org.mm.core.support.auth.AuthCenter;
import org.mm.model.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController extends BaseController {

	@RequestMapping(value = "/", method = { RequestMethod.GET })
	public Object index(ModelMap modelMap) {
		return "index";
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET })
	public Object login(ModelMap modelMap, HttpServletRequest request) {
		Account user = AuthCenter.getUser(request);
		if (user != null) {
			return "redirect:/";
		}
		
		return "login";
	}
	
	@RequestMapping(value = "/flogin", method = { RequestMethod.GET })
	public Object flogin(ModelMap modelMap, HttpServletRequest request) {
		return "flogin";
	}
	
	@RequestMapping(value = "/logout", method = { RequestMethod.GET })
	public Object logout(ModelMap modelMap, HttpServletRequest request) {
		AuthCenter.logout(request);
		
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/profile", method = { RequestMethod.GET })
	public Object profile(ModelMap modelMap, HttpServletRequest request) {
		return "building";
	}
	
	@RequestMapping(value = "/404", method = { RequestMethod.GET })
	public Object page404(ModelMap modelMap, HttpServletRequest request) {
		return "404";
	}
	
	@RequestMapping(value = "/500", method = { RequestMethod.GET })
	public Object page500(ModelMap modelMap, HttpServletRequest request) {
		return "500";
	}

}
