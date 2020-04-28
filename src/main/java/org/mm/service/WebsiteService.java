package org.mm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mm.core.config.Resources;
import org.mm.core.exception.IllegalParameterException;
import org.mm.core.exception.NotFoundException;
import org.mm.core.util.ClientKeyUtil;
import org.mm.core.util.DateUtil;
import org.mm.core.util.HidingStringUtil;
import org.mm.core.util.RedisUtil;
import org.mm.mapper.WebsiteMapper;
import org.mm.model.Account;
import org.mm.model.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mm.core.util.PaginationUtil;
import org.mm.model.Pagination;

@Service
public class WebsiteService {

	@Autowired
	private WebsiteMapper mapper;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	public void add(Website website) {
		mapper.add(website);
	}
	
	public void create(Website website) {
		Account account = accountService.findById(website.getAccountId());
		if (!Account.RoleType.SuperUser.is(account.getRoleType())) {
			Map<String, Object> params = new HashMap<>();
			params.put("accountId", website.getAccountId());
			params.put("state", Website.State.Default.getValue());
			Integer total = count(params);
			if (total >= Website.USER_MAX_WEBSITE_COUNT) {
				throw new IllegalParameterException(Resources.getMessage("WEBSITE_COUNT_LIMIT"));
			}
		}
		
		website.setApiKey(ClientKeyUtil.apiKey());
		website.setWebKey(ClientKeyUtil.webKey());
		website.setIconType(0);
		website.setState(Website.State.Default.getValue());
		
		this.add(website);
	}
	
	public void update(Website website) {
		mapper.update(website);
	}
	
	public void modify(Website website) {
		Website savedWebsite = this.findByWebKey(website.getWebKey());
		if (savedWebsite == null || website.getAccountId() == null || savedWebsite.getAccountId().longValue() != website.getAccountId().longValue()) {
			throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		}
		savedWebsite.setName(website.getName());
		savedWebsite.setUrl(website.getUrl());
		savedWebsite.setSecLevel(website.getSecLevel());
		savedWebsite.setSecMode(website.getSecMode());
		savedWebsite.setThemeNum(website.getThemeNum());
		savedWebsite.setScalingRatio(website.getScalingRatio());
		this.update(savedWebsite);
		
		redisUtil.del(savedWebsite.getWebKey());
	}
	
	public void delete(Long accountId, String webKey) {
		Website website = this.findByWebKey(webKey);
		if (website == null || accountId == null || website.getAccountId().longValue() != accountId.longValue()) {
			throw new NotFoundException(Resources.getMessage("NOT_FOUND", Resources.getMessage("WEBSITE")));
		}
		website.setState(Website.State.Deleted.getValue());
		this.update(website);
		
		redisUtil.del(website.getWebKey());
	}
	
	public Website findByParams(Map<String, Object> params) {
		return mapper.findByParams(params);
	}
	
	public Website findByApiKey(String apiKey) {
		if (apiKey == null || "".equals(apiKey)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("apiKey", apiKey);
		params.put("state", Website.State.Default.getValue());
		
		return this.findByParams(params);
	}
	
	public Website findByWebKey(String webKey) {
		if (webKey == null || "".equals(webKey)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("webKey", webKey);
		params.put("state", Website.State.Default.getValue());
		
		return this.findByParams(params);
	}
	
	public List<Website> select(Map<String, Object> params) {
		return mapper.select(params);
	}
	
	public List<Website> select(Map<String, Object> params, Pagination pagination) {
		PaginationUtil.servicePaging(params, pagination);
		return this.select(params);
	}
	
	public List<Map<String, Object>> selectToMap(Map<String, Object> params, Pagination pagination) {
		List<Website> websites = this.select(params, pagination);
		
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map;
		for (Website website: websites) {
			map = new HashMap<>();
			map.put("id", website.getId());
			map.put("accountId", website.getAccountId());
			map.put("name", website.getName());
			map.put("url", website.getUrl());
			map.put("apiKey", HidingStringUtil.defaultHidingString(website.getApiKey(), 3, 3));
			map.put("webKey", website.getWebKey());
			map.put("createdAt", DateUtil.getDateTimeFormat(website.getCreatedAt()));
			map.put("createdDate", DateUtil.getDateFormat(website.getCreatedAt()));
			
			list.add(map);
		}
		
		return list;
	}
	
	public Integer count(Map<String, Object> params) {
		return mapper.count(params);
	}

}
