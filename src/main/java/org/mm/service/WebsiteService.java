package org.mm.service;

import java.util.HashMap;
import java.util.Map;

import org.mm.mapper.WebsiteMapper;
import org.mm.model.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebsiteService {

	@Autowired
	private WebsiteMapper mapper;
	
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

}
