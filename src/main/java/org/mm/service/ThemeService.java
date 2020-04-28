package org.mm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ThemeService {

	public static final Map<Integer, String> themeMap = new HashMap<>();
	
	static {
		themeMap.put(0, "风景");
		themeMap.put(1, "动物");
		themeMap.put(2, "动漫");
		themeMap.put(3, "科技");
		themeMap.put(4, "桥梁");
		themeMap.put(5, "车辆");
		themeMap.put(6, "综合");
	}
	
	public List<Map<String, Object>> search() {
		List<Map<String, Object>> themes = new ArrayList<>();
		Map<String, Object> theme;
		for (Map.Entry<Integer, String> entry: themeMap.entrySet()) {
			theme = new HashMap<>();
			theme.put("value", entry.getKey());
			theme.put("name", entry.getValue());
			themes.add(theme);
		}
		
		return themes;
	}

}
