package org.mm.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mm.core.captcha.CaptchaMode;
import org.mm.core.config.Resources;
import org.mm.core.exception.NotFoundException;
import org.mm.core.support.auth.AuthCenter;
import org.mm.core.util.DateUtil;
import org.mm.core.util.HidingStringUtil;
import org.mm.core.util.PaginationUtil;
import org.mm.core.util.TypeParseUtil;
import org.mm.model.Account;
import org.mm.model.Pagination;
import org.mm.model.Website;
import org.mm.service.ThemeService;
import org.mm.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/website")
public class WebsiteController extends BaseController {

	@Autowired
	private WebsiteService websiteService;
	
	@Autowired
	private ThemeService themeService;
	
	@RequestMapping(value = "/new", method = { RequestMethod.GET })
	public Object _new(ModelMap modelMap, HttpServletRequest request) {
		return "website/new";
	}
	
	@RequestMapping(value = "/setup/{webKey}", method = { RequestMethod.GET })
	public Object setup(ModelMap modelMap, HttpServletRequest request,
			@PathVariable(value = "webKey", required = true) String webKey) {
		getAndCheckWebsite(request, webKey);
		modelMap.addAttribute("webKey", webKey);
		
		return "website/edit";
	}
	
	@RequestMapping(value = {"/info", "/info/{webKey}"}, method = { RequestMethod.POST })
	public Object info(ModelMap modelMap, HttpServletRequest request,
			@PathVariable(value = "webKey", required = false) String webKey) {
		Website website = websiteService.findByWebKey(webKey);
		if (website != null) {
			Account account = AuthCenter.getUser(request);
			if (account.getId().longValue() != website.getAccountId().longValue()) website = null;
		}
		if (website == null) {
			website = new Website();
			website.setThemeNum(Website.THEME_NUM_MAX);
			website.setScalingRatio(Website.SCALING_RATIO_MAX);
		}
		
		List<Map<String, Object>> themes = themeService.search();
		
		DecimalFormat df = new DecimalFormat("#.###");
		List<Double> scalingRatios = new ArrayList<>();
		for (Double d = Website.SCALING_RATIO_MIN; d <= Website.SCALING_RATIO_MAX; d = Double.valueOf(df.format(d + 0.1))) {
			scalingRatios.add(d);
		}
		
		modelMap.addAttribute("website", website);
		modelMap.addAttribute("themes", themes);
		modelMap.addAttribute("scalingRatios", scalingRatios);
		modelMap.addAttribute("secModes", CaptchaMode.values());
		modelMap.addAttribute("secLevels", Website.SecLevel.values());
		
		return "website/form::websiteForm";
	}
	
	@RequestMapping(value = "/index", method = { RequestMethod.GET })
	public Object index(ModelMap modelMap, HttpServletRequest request) {
		return "website/index";
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public Object search(ModelMap modelMap, HttpServletRequest request,
			@RequestParam(value = "page", required = false) String _page,
			@RequestParam(value = "pageSize", required = false) String _pageSize) {
		Integer page = TypeParseUtil.convertToInteger(_page);
		Integer pageSize = TypeParseUtil.convertToInteger(_pageSize);
		
		Account account = AuthCenter.getUser(request);
		
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", account.getId());
		params.put("state", Website.State.Default.getValue());
		Integer total = websiteService.count(params);
		Pagination pagination = PaginationUtil.controllerPaging(page, pageSize, total);
		List<Map<String, Object>> websites = websiteService.selectToMap(params, pagination);
		
		Map<String, Object> result = new HashMap<>();
		result.put("websites", websites);
		pagination.mergeTo(result);
		modelMap.addAllAttributes(result);
		
		return "website/index::websites";
	}

	@RequestMapping(value = "/monitor/{webKey}", method = RequestMethod.GET)
	public Object monitor(ModelMap modelMap, HttpServletRequest request,
			@PathVariable(name = "webKey", required = true) String webKey){
		Website website = getAndCheckWebsite(request, webKey);
		Map<String, Object> map = new HashMap<>();
		map.put("webKey", website.getWebKey());
		map.put("name", website.getName());
		map.put("url", website.getUrl());
		
		modelMap.addAttribute("website", map);
		
		return "website/monitor";
	}
	
	@RequestMapping(value = "/{webKey}", method = { RequestMethod.GET })
	public Object item(ModelMap modelMap, HttpServletRequest request,
			@PathVariable(name = "webKey", required = true) String webKey) {
		Website website = getAndCheckWebsite(request, webKey);
		
		Map<String, Object> map = new HashMap<>();
		map.put("webKey", website.getWebKey());
		map.put("apiKey", HidingStringUtil.defaultHidingString(website.getApiKey(), 3, 3));
		map.put("name", website.getName());
		map.put("url", website.getUrl());
		map.put("themeNum", website.getThemeNum());
		map.put("themeName", website.getThemeNum() == null ? "随机" : ThemeService.themeMap.get(website.getThemeNum()));
		map.put("scalingRatio", website.getScalingRatio());
		map.put("secMode", website.getSecMode());
		map.put("secModeName", CaptchaMode.valueOf(website.getSecMode()).getName());
		map.put("secLevel", website.getSecLevel());
		map.put("secLevelName", Website.SecLevel.valueOf(website.getSecLevel()).getName());
		map.put("createdAt", DateUtil.getDateTimeFormat(website.getCreatedAt()));
		map.put("updatedAt", DateUtil.getDateTimeFormat(website.getUpdatedAt()));
		
		modelMap.addAttribute("website", map);
		
		return "website/item";
	}
	
	private Website getAndCheckWebsite(HttpServletRequest request, String webKey) {
		Website website = websiteService.findByWebKey(webKey);
		if (website == null) throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		Account account = AuthCenter.getUser(request);
		if (account.getId().longValue() != website.getAccountId().longValue())
			throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		return website;
	}

}
