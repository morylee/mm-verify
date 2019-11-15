package org.mm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mm.core.HttpCode;
import org.mm.core.exception.BaseException;
import org.mm.core.exception.IllegalParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 控制器基类
 * @author LiChenhui
 * @version 2017年8月23日 11点48分
 */

@ControllerAdvice(annotations = RestController.class)
public abstract class BaseRestController {
	protected final Logger logger = LogManager.getLogger(this.getClass());

	/** 设置成功响应代码 */
	protected ResponseEntity<ModelMap> setSuccessModelMap(ModelMap modelMap) {
		return setSuccessModelMap(modelMap, null);
	}

	/** 设置成功响应代码 */
	protected ResponseEntity<ModelMap> setSuccessModelMap(ModelMap modelMap, Object data) {
		return setModelMap(modelMap, HttpCode.OK, data);
	}

	/** 设置响应代码 */
	protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, HttpCode code) {
		return setModelMap(modelMap, code, null);
	}

	/** 设置响应代码 */
	protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, HttpCode code, Object data) {
		modelMap.remove("org.springframework.validation.BindingResult.map");
//		modelMap.clear();
		if (data != null) {
			modelMap.put("data", data);
		}
		modelMap.put("httpCode", code.value());
		modelMap.put("msg", code.msg());
		modelMap.put("timestamp", System.currentTimeMillis());
		return ResponseEntity.ok(modelMap);
	}

	/** 异常处理 */
	@RequestMapping(headers = "Accept=application/json")
	@ExceptionHandler(Exception.class)
	public void restExceptionHandler(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex)
			throws Exception {
		logger.error("Verify Service Error:", ex);
		
		ModelMap modelMap = new ModelMap();
		if (ex instanceof BaseException) {
			((BaseException) ex).handler(modelMap);
		} else if (ex instanceof IllegalArgumentException) {
			new IllegalParameterException(ex.getMessage()).handler(modelMap);
		} else {
			setModelMap(modelMap, HttpCode.INTERNAL_SERVER_ERROR);
		}
		// TODO 
		request.setAttribute("msg", modelMap.get("msg"));
		response.setContentType("application/json;charset=UTF-8");
		byte[] bytes = JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect);
		response.getOutputStream().write(bytes);
	}

}
