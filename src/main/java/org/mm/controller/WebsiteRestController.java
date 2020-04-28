package org.mm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mm.core.config.Resources;
import org.mm.core.exception.NotFoundException;
import org.mm.core.support.auth.AuthCenter;
import org.mm.model.Account;
import org.mm.model.Captcha;
import org.mm.model.Website;
import org.mm.service.CaptchaService;
import org.mm.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.mm.core.util.AssertUtil;
import org.mm.core.util.DateUtil;
import org.mm.core.util.Map2ModelUtil;
import org.mm.core.util.PasswordUtil;
import org.mm.core.util.TypeParseUtil;

@RestController
@RequestMapping("/website")
public class WebsiteRestController extends BaseRestController {

	@Autowired
	private WebsiteService websiteService;
	
	@Autowired
	private CaptchaService captchaService;
	
	@RequestMapping(value = "/create", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object create(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Website website = Map2ModelUtil.convert(Website.class, params);
		valid(website);
		
		Account account = AuthCenter.getUser(request);
		website.setAccountId(account.getId());
		
		websiteService.create(website);
		
		return setSuccessModelMap(modelMap, website.getWebKey());
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object update(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Website website = Map2ModelUtil.convert(Website.class, params);
		AssertUtil.isBlank(website.getWebKey(), "WEBSITE.WEB_KEY");
		valid(website);
		
		Account account = AuthCenter.getUser(request);
		website.setAccountId(account.getId());
		
		websiteService.modify(website);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Account account = AuthCenter.getUser(request);
		String webKey = TypeParseUtil.convertToString(params.get("webKey"));
		websiteService.delete(account.getId(), webKey);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/apiKey", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object apiKey(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = TypeParseUtil.convertToString(params.get("webKey"));
		AssertUtil.isBlank(webKey, "WEBSITE.WEB_KEY");
		String password = TypeParseUtil.convertToString(params.get("password"));
		AssertUtil.isBlank(password, "ACCOUNT.PASSWORD");
		String secPwd = PasswordUtil.securityPwd(password);
		
		Account account = AuthCenter.getUser(request);
		AssertUtil.match(account.getPassword(), secPwd, "ACCOUNT.PASSWORD");
		
		Website website = websiteService.findByWebKey(webKey);
		if (website == null) throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		if (website.getAccountId().longValue() != account.getId().longValue())
			throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		
		return setSuccessModelMap(modelMap, website.getApiKey());
	}
	
	@RequestMapping(value = "/monitor/time", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorTime(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = TypeParseUtil.convertToString(params.get("webKey"));
		AssertUtil.isBlank(webKey, "WEBSITE.WEB_KEY");
		Website website = getAndCheckWebsite(request, webKey);
		
		Date[] dateRange = getDateRange(params, website.getCreatedAt());
		List<Map<String, Object>> counts = captchaService.countPeriod(webKey, dateRange[0], dateRange[1]);
		
		List<String> names = new ArrayList<>();
		List<Integer> values = new ArrayList<>();
		for (Map<String, Object> count: counts) {
			names.add(TypeParseUtil.convertToString(count.get("date")));
			values.add(TypeParseUtil.convertToInteger(count.get("count")));
		}
		modelMap.addAttribute("websiteName", website.getName());
		modelMap.addAttribute("names", names);
		modelMap.addAttribute("values", values);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/monitor/hour", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorHour(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = TypeParseUtil.convertToString(params.get("webKey"));
		AssertUtil.isBlank(webKey, "WEBSITE.WEB_KEY");
		Website website = getAndCheckWebsite(request, webKey);
		
		Date[] dateRange = getDateRange(params, website.getCreatedAt());
		List<Map<String, Object>> counts = captchaService.countSameHour(webKey, dateRange[0], dateRange[1]);
		
		List<String> names = new ArrayList<>();
		List<Integer> values = new ArrayList<>();
		for (Map<String, Object> count: counts) {
			names.add(TypeParseUtil.convertToString(count.get("date")));
			values.add(TypeParseUtil.convertToInteger(count.get("count")));
		}
		modelMap.addAttribute("websiteName", website.getName());
		modelMap.addAttribute("names", names);
		modelMap.addAttribute("values", values);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/monitor/state", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorState(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		String webKey = TypeParseUtil.convertToString(params.get("webKey"));
		AssertUtil.isBlank(webKey, "WEBSITE.WEB_KEY");
		Website website = getAndCheckWebsite(request, webKey);
		
		Date[] dateRange = getDateRange(params, website.getCreatedAt());
		List<Map<String, Object>> counts = captchaService.countState(webKey, dateRange[0], dateRange[1]);
		
		Map<Object, Object> countMap = new HashMap<>();
		for (Map<String, Object> count: counts) countMap.put(count.get("state"), count.get("count"));
		
		List<Map<Object, Object>> countList = new ArrayList<>();
		Map<Object, Object> count;
		for (Captcha.State state: Captcha.State.values()) {
			count = new HashMap<>();
			count.put("name", state.getName());
			if (countMap.containsKey(state.getValue())) {
				count.put("value", countMap.get(state.getValue()));
			} else {
				count.put("value", 0);
			}
			countList.add(count);
		}
		
		return setSuccessModelMap(modelMap, countList);
	}
	
	private void valid(Website website) {
		AssertUtil.isBlank(website.getName(), "WEBSITE.NAME");
		AssertUtil.length(website.getName(), 2, 32, "WEBSITE.NAME");
		AssertUtil.isBlank(website.getUrl(), "WEBSITE.URL");
		AssertUtil.length(website.getUrl(), 6, 100, "WEBSITE.URL");
		AssertUtil.isNull(website.getSecMode(), "WEBSITE.SEC_MODE");
		AssertUtil.contains(website.getSecMode(), Website.SEC_MODE_LIST, "WEBSITE.SEC_MODE");
		AssertUtil.isNull(website.getSecLevel(), "WEBSITE.SEC_LEVEL");
		AssertUtil.range(website.getSecLevel(), Website.SecLevel.Low.getValue(), Website.SecLevel.High.getValue(), "WEBSITE.SEC_LEVEL");
		if (website.getThemeNum() != null)
			AssertUtil.range(website.getThemeNum(), Website.THEME_NUM_MIN, Website.THEME_NUM_MAX, "WEBSITE.THEME_NUM");
		AssertUtil.isNull(website.getScalingRatio(), "WEBSITE.SCALING_RATIO");
		AssertUtil.range(website.getScalingRatio(), Website.SCALING_RATIO_MIN, Website.SCALING_RATIO_MAX, "WEBSITE.SCALING_RATIO");
	}
	
	private Website getAndCheckWebsite(HttpServletRequest request, String webKey) {
		Website website = websiteService.findByWebKey(webKey);
		if (website == null) throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		Account account = AuthCenter.getUser(request);
		if (account.getId().longValue() != website.getAccountId().longValue())
			throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		return website;
	}
	
	private Date[] getDateRange(Map<String, Object> params, Date minDate) {
		Date timeFrom = null;
		Date timeTo = null;
		try {
			String timeFromStr = TypeParseUtil.convertToString(params.get("timeFrom"));
			String timeToStr = TypeParseUtil.convertToString(params.get("timeTo"));
			if (StringUtils.isNotBlank(timeFromStr)) timeFrom = DateUtil.getDateTimeFormat(timeFromStr);
			if (StringUtils.isNotBlank(timeToStr)) timeTo = DateUtil.getDateTimeFormat(timeToStr);
			AssertUtil.moreThan(timeFrom, timeTo, "DATE_FROM", "DATE_TO");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Date currTime = new Date();
		if (timeFrom == null) timeFrom = DateUtil.getDayBefore(currTime);
		if (timeFrom.getTime() < minDate.getTime()) timeFrom = minDate;
		if (timeTo == null) timeTo = currTime;
		
		return new Date[]{timeFrom, timeTo};
	}

}
