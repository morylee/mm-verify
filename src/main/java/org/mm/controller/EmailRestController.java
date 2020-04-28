package org.mm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mm.core.Constants;
import org.mm.core.config.Resources;
import org.mm.core.exception.ExistException;
import org.mm.core.exception.IllegalParameterException;
import org.mm.core.security.Md5CoderUtil;
import org.mm.core.util.AssertUtil;
import org.mm.core.util.EmailUtil;
import org.mm.core.util.RandomUtil;
import org.mm.core.util.RedisUtil;
import org.mm.core.util.TypeParseUtil;
import org.mm.model.Account;
import org.mm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailRestController extends BaseRestController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object registerOnBulid(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		throw new IllegalParameterException(Resources.getMessage("CLOSED_BUSINESS", Resources.getMessage("REGISTER_ACTION")));
	}
	
	public Object register(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String email = TypeParseUtil.convertToString(params.get("email"));
		AssertUtil.isBlank(email, "ACCOUNT.EMAIL");
		AssertUtil.email(email, "ACCOUNT.EMAIL");
		
		Account savedAccount = accountService.findByEmail(email);
		if (savedAccount != null) throw new ExistException(Resources.getMessage("ALREADY_USED", Resources.getMessage("ACCOUNT.EMAIL")));
		
		String key = Constants.REGISTER_EMAIL_CAPTCHA + Md5CoderUtil.len16(email);
		Integer captcha = TypeParseUtil.convertToInteger(redisUtil.get(key));
		if (captcha == null) {
			captcha = RandomUtil.randomInt(100000, 999999);
			redisUtil.set(key, captcha, Constants.CAPTCHA_EXPIRED_TIME);
		}
		boolean success = EmailUtil.sendRegisterCaptcha(email, captcha + "", null);
		modelMap.put("success", success);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/registerCheck", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object registerCheckOnBulid(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		throw new IllegalParameterException(Resources.getMessage("CLOSED_BUSINESS", Resources.getMessage("REGISTER_ACTION")));
	}
	
	public Object registerCheck(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String email = TypeParseUtil.convertToString(params.get("email"));
		AssertUtil.isBlank(email, "ACCOUNT.EMAIL");
		AssertUtil.email(email, "ACCOUNT.EMAIL");
		String captcha = TypeParseUtil.convertToString(params.get("captcha"));
		String savedCaptcha = TypeParseUtil.convertToString(redisUtil.get(Constants.REGISTER_EMAIL_CAPTCHA + Md5CoderUtil.len16(email)));
		AssertUtil.isBlank(captcha, "EMAIL.CAPTCHA");
		AssertUtil.match(captcha, savedCaptcha, "EMAIL.CAPTCHA");
		modelMap.put("success", true);
		
		return setSuccessModelMap(modelMap);
	}

}
