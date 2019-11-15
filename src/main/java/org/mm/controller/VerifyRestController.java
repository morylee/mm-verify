package org.mm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mm.core.img.ImageCaptchaUtil;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerifyRestController extends BaseRestController {

	@RequestMapping("/init")
	public Object init(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Map<String, Object> verify = ImageCaptchaUtil.securityGenerate("D:/personal/images/captcha/131.png", 0, 3);
		modelMap.putAll(verify);
		
		return setSuccessModelMap(modelMap);
	}

	@RequestMapping("/verify")
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
			boolean res = ImageCaptchaUtil.securityVerify(key, 0, positions);
			
			modelMap.put("success", res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return setSuccessModelMap(modelMap);
	}
}
