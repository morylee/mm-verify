package org.mm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mm.core.img.ImageCaptchaUtil;
import org.mm.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerifyRestController extends BaseRestController {

	@Autowired
	private VerifyService verifyService;
	
	@RequestMapping(value = "/param", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object param(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		modelMap.put("width", ImageCaptchaUtil.DEFAULT_WIDTH * VerifyService.account.getSize());
		modelMap.put("height", ImageCaptchaUtil.DEFAULT_HEIGHT * VerifyService.account.getSize());
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/init", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object init(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Map<String, Object> verify = verifyService.init(VerifyService.account);
		modelMap.putAll(verify);
		
		return setSuccessModelMap(modelMap);
	}

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
	
	@RequestMapping(value = "/verifyToken", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object tokenVerify(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String apiKey = (String) params.get("apiKey");
		String token = (String) params.get("token");

		boolean success = ImageCaptchaUtil.verifyToken(apiKey, token);
		modelMap.put("success", success);
		
		return setSuccessModelMap(modelMap);
	}

}
