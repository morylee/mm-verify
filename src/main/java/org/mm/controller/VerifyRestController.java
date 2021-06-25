package org.mm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mm.core.OriginCheck;
import org.mm.core.captcha.CaptchaUtil;
import org.mm.core.exception.NotFoundException;
import org.mm.model.Website;
import org.mm.service.VerifyService;
import org.mm.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerifyRestController extends BaseRestController {

	@Autowired
	private WebsiteService websiteService;
	
	@Autowired
	private VerifyService verifyService;
	
	/**
	 * 验证码基础信息
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(key = "webKey", init = true)
	@RequestMapping(value = "/param", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object param(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = (String) params.get("webKey");
		Website website = websiteService.findByWebKey(webKey);
		if (website != null) {
			modelMap.put("width", CaptchaUtil.DEFAULT_WIDTH * website.getScalingRatio() / CaptchaUtil.BACKGROUND_RESIZE);
			modelMap.put("height", CaptchaUtil.DEFAULT_HEIGHT * website.getScalingRatio() / CaptchaUtil.BACKGROUND_RESIZE);
		} else {
			throw new NotFoundException("无效的WebKey");
		}
		
		return setSuccessModelMap(modelMap);
	}
	
	/**
	 * 初始化验证码
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(key = "webKey", init = true)
	@RequestMapping(value = "/init", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object init(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = (String) params.get("webKey");
		Website website = websiteService.findByWebKey(webKey);
		if (website != null) {
			String requestOrigin = request.getHeader(HttpHeaders.ORIGIN);
			website.setUrl(requestOrigin); // 用于传参记录初始化的源地址
			Map<String, Object> verify = verifyService.init(website);
			modelMap.putAll(verify);
		} else {
			throw new NotFoundException("无效的WebKey");
		}
		
		return setSuccessModelMap(modelMap);
	}

	/**
	 * 校验验证码
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(key = "key")
	@RequestMapping(value = "/verify", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object verify(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		JSONObject json = new JSONObject(params);
		try {
			String key = json.getString("key");
			JSONArray array = json.getJSONArray("clientPositions");
			String[][] positions = new String[array.length()][];
			for (int i = 0; i < array.length(); i++) {
				JSONArray xy = array.getJSONArray(i);
				String[] position = new String[xy.length()];
				for (int j = 0; j < xy.length(); j++) {
					position[j] = xy.getString(j);
				}
				positions[i] = position;
			}
			Map<String, Object> res = verifyService.verify(key, positions);
			
			modelMap.putAll(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return setSuccessModelMap(modelMap);
	}
	
	/**
	 * 删除验证码
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(key = "webKey", init = true)
	@RequestMapping(value = "/del", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = (String) params.get("webKey");
		String key = (String) params.get("key");
		
		boolean success = verifyService.delete(webKey, key);
		modelMap.put("success", success);
		
		return setSuccessModelMap(modelMap);
	}
	
	/**
	 * 校验验证码token
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(ignore = true)
	@RequestMapping(value = "/verifyToken", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object verifyToken(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String apiKey = (String) params.get("apiKey");
		String token = (String) params.get("token");

		boolean success = CaptchaUtil.verifyToken(apiKey, token);
		modelMap.put("success", success);
		
		return setSuccessModelMap(modelMap);
	}
	
	/**
	 * 删除验证码token
	 * @param modelMap
	 * @param params
	 * @param request
	 * @return
	 */
	@OriginCheck(key = "webKey", init = true)
	@RequestMapping(value = "/delToken", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object delToken(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = (String) params.get("webKey");
		String token = (String) params.get("token");
		
		boolean success = false;
		Website website = websiteService.findByWebKey(webKey);
		if (website == null) {
			throw new NotFoundException("无效的WebKey");
		} else {
			success = CaptchaUtil.delToken(website.getApiKey(), token);
		}
		modelMap.put("success", success);
		
		return setSuccessModelMap(modelMap);
	}

}
