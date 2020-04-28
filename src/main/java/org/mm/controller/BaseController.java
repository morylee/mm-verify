package org.mm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mm.core.exception.NotFoundException;
import org.mm.core.exception.UnauthException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice(annotations = Controller.class)
public class BaseController {

protected final Logger logger = LogManager.getLogger(this.getClass());
	
	/** 异常处理 */
	@RequestMapping(headers = "Accept=text/html")
	@ExceptionHandler(Exception.class)
	public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex)
			throws Exception {
		logger.error("Verify Service Error:", ex);
		if (ex instanceof NotFoundException) {
			return "redirect:/404";
		} else if (ex instanceof UnauthException) {
			return "redirect:/login";
		} else {
			return "redirect:/500";
		}
	}

}
