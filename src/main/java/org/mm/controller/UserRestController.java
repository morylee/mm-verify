package org.mm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mm.core.Constants;
import org.mm.core.captcha.CaptchaUtil;
import org.mm.core.config.Resources;
import org.mm.core.exception.IllegalParameterException;
import org.mm.core.security.Md5CoderUtil;
import org.mm.core.support.auth.AuthCenter;
import org.mm.core.support.auth.Entity;
import org.mm.core.util.AssertUtil;
import org.mm.core.util.Map2ModelUtil;
import org.mm.core.util.PasswordUtil;
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
@RequestMapping("/user")
public class UserRestController extends BaseRestController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object registerOnBuild(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		throw new IllegalParameterException(Resources.getMessage("CLOSED_BUSINESS", Resources.getMessage("REGISTER_ACTION")));
	}
	
	public Object register(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Account account = Map2ModelUtil.convert(Account.class, params);
		
		AssertUtil.isBlank(account.getEmail(), "ACCOUNT.EMAIL");
		AssertUtil.email(account.getEmail(), "ACCOUNT.EMAIL");
		String redisKey = Constants.REGISTER_EMAIL_CAPTCHA + Md5CoderUtil.len16(account.getEmail());
		String captcha = TypeParseUtil.convertToString(params.get("captcha"));
		String savedCaptcha = TypeParseUtil.convertToString(redisUtil.get(redisKey));
		AssertUtil.isBlank(captcha, "EMAIL.CAPTCHA");
		AssertUtil.match(captcha, savedCaptcha, "EMAIL.CAPTCHA");
		AssertUtil.isBlank(account.getName(), "ACCOUNT.NAME");
		AssertUtil.length(account.getName(), 2, 16, "ACCOUNT.NAME");
		AssertUtil.name(account.getName(), "ACCOUNT.NAME");
		AssertUtil.isBlank(account.getPassword(), "ACCOUNT.PASSWORD");
		AssertUtil.length(account.getPassword(), 8, 64, "ACCOUNT.PASSWORD");
		AssertUtil.isBlank(account.getConfirmPassword(), "ACCOUNT.CONFIRM_PASSWORD");
		AssertUtil.equal(account.getPassword(), account.getConfirmPassword(), "ACCOUNT.PASSWORD");
		
		accountService.create(account);
		redisUtil.del(redisKey);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object login(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String account = TypeParseUtil.convertToString(params.get("account"));
		AssertUtil.isBlank(account, "ACCOUNT.ACCOUNT");
		AssertUtil.email(account, "ACCOUNT.ACCOUNT");
		String password = TypeParseUtil.convertToString(params.get("password"));
		AssertUtil.isBlank(password, "ACCOUNT.PASSWORD");
		AssertUtil.length(password, 8, 64, "ACCOUNT.PASSWORD");
		String captcha = TypeParseUtil.convertToString(params.get("captcha"));
		if (!CaptchaUtil.verifyToken(Constants.API_KEY, captcha)) {
			throw new IllegalParameterException(Resources.getMessage("GRAPHIC_CAPTCHA_ERROR"));
		}
		
		String secPwd = PasswordUtil.securityPwd(password);
		if (!AuthCenter.login(request, Entity.EMAIL_TYPE, account, secPwd)) {
			throw new IllegalParameterException(Resources.getMessage("TWO_VALUE_NOT_MATCH", Resources.getMessage("ACCOUNT.ACCOUNT_OR_PWD")));
		}
		
		String savedReq = AuthCenter.popInterceptorReq(request);
		if (savedReq == null) savedReq = "/";
		
		return setSuccessModelMap(modelMap, savedReq);
	}

}
