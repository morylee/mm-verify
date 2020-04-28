package org.mm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mm.core.support.auth.AuthCenter;
import org.mm.core.util.AssertUtil;
import org.mm.core.util.DateUtil;
import org.mm.core.util.TypeParseUtil;
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

@RestController
@RequestMapping("/index")
public class IndexRestController extends BaseRestController {

	@Autowired
	private WebsiteService websiteService;
	
	@Autowired
	private CaptchaService captchaService;
	
	@RequestMapping(value = "/monitor/time", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorTime(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Date[] dateRange = getDateRange(params);
		
		List<Website> websites = getWebsites(request);
		List<String> websiteNames = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<List<Integer>> valuesList = new ArrayList<>();
		List<Integer> values;
		for (Website website: websites) {
			websiteNames.add(website.getId() + "." + website.getName());
			List<Map<String, Object>> counts = captchaService.countPeriod(website.getWebKey(), dateRange[0], dateRange[1]);
			
			names.clear();
			values = new ArrayList<>();
			for (Map<String, Object> count: counts) {
				names.add(TypeParseUtil.convertToString(count.get("date")));
				values.add(TypeParseUtil.convertToInteger(count.get("count")));
			}
			
			valuesList.add(values);
		}
		
		modelMap.addAttribute("websiteNames", websiteNames);
		modelMap.addAttribute("names", names);
		modelMap.addAttribute("valuesList", valuesList);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/monitor/hour", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorHour(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Date[] dateRange = getDateRange(params);
		
		List<Website> websites = getWebsites(request);
		List<String> websiteNames = new ArrayList<>();
		List<List<Integer>> valuesList = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<Integer> values;
		for (Website website: websites) {
			websiteNames.add(website.getId() + "." + website.getName());
			List<Map<String, Object>> counts = captchaService.countSameHour(website.getWebKey(), dateRange[0], dateRange[1]);
			
			names.clear();
			values = new ArrayList<>();
			for (Map<String, Object> count: counts) {
				names.add(TypeParseUtil.convertToString(count.get("date")));
				values.add(TypeParseUtil.convertToInteger(count.get("count")));
			}
			valuesList.add(values);
		}
		modelMap.addAttribute("websiteNames", websiteNames);
		modelMap.addAttribute("names", names);
		modelMap.addAttribute("valuesList", valuesList);
		
		return setSuccessModelMap(modelMap);
	}
	
	@RequestMapping(value = "/monitor/state", method = RequestMethod.POST, headers = "Accept=application/json")
	public Object monitorState(ModelMap modelMap, @RequestBody Map<String, Object> params, HttpServletRequest request) {
		Date[] dateRange = getDateRange(params);
		
		params.put("timeFrom", DateUtil.getDateTimeFormat(dateRange[0]));
		params.put("timeTo", DateUtil.getDateTimeFormat(dateRange[1]));
		List<Map<String, Object>> counts = captchaService.countState(params);
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
	
	private List<Website> getWebsites(HttpServletRequest request) {
		Account account = AuthCenter.getUser(request);
		
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", account.getId());
		params.put("state", Website.State.Default.getValue());
		
		return websiteService.select(params);
	}
	
	private Date[] getDateRange(Map<String, Object> params) {
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
		if (timeTo == null) timeTo = currTime;
		
		return new Date[]{timeFrom, timeTo};
	}

}
