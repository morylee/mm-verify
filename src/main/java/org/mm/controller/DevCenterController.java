package org.mm.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/dev-center")
public class DevCenterController extends BaseController {

	@RequestMapping(value = "/guide", method = { RequestMethod.GET })
	public Object _new(ModelMap modelMap, HttpServletRequest request) {
		return "dev-center/guide";
	}

}
