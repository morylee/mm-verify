package org.mm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mm.core.util.DateUtil;
import org.mm.mapper.CaptchaMapper;
import org.mm.model.Captcha;
import org.mm.model.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

	@Autowired
	private CaptchaMapper mapper;
	
	public void add(Captcha captcha) {
		mapper.add(captcha);
	}
	
	public void update(Captcha captcha) {
		mapper.update(captcha);
	}
	
	public Captcha findByParams(Map<String, Object> params) {
		return mapper.findByParams(params);
	}
	public List<Map<String, Object>> countDate(Map<String, Object> params) {
		return mapper.countDate(params);
	}
	public List<Map<String, Object>> countState(Map<String, Object> params) {
		return mapper.countState(params);
	}
	
	public Captcha create(String key, Website website) {
		Captcha captcha = new Captcha();
		captcha.setApiKey(website.getApiKey());
		captcha.setWebKey(website.getWebKey());
		captcha.setKey(key);
		captcha.setSecLevel(website.getSecLevel());
		captcha.setSecMode(website.getSecMode());
		captcha.setVerifyTimes(0);
		captcha.setState(Captcha.State.Unverified.getValue());
		this.add(captcha);
		
		return captcha;
	}
	
	public void verify(String key, Captcha.State state) {
		Captcha captcha = this.findByKey(key);
		if (captcha == null) {
			System.out.println("无效的验证码：" + key);
			return;
		}
		
		captcha.setVerifyTimes(captcha.getVerifyTimes() + 1);
		captcha.setState(state.getValue());
		this.update(captcha);
	}
	
	public Captcha findByKey(String key) {
		if (key == null || "".equals(key)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		
		return this.findByParams(params);
	}
	
	public List<Map<String, Object>> countPeriod(String webKey, Date timeFrom, Date timeTo) {
		Date currTime = new Date();
		if (timeFrom == null) timeFrom = DateUtil.getDayBefore(currTime);
		if (timeTo == null) timeTo = currTime;
		
		Map<String, Object> params = new HashMap<>();
		params.put("webKey", webKey);
		params.put("timeFrom", DateUtil.getDateTimeFormat(timeFrom));
		params.put("timeTo", DateUtil.getDateTimeFormat(timeTo));
		
		long periodTime = timeTo.getTime() - timeFrom.getTime();
		long oneDayTime = 24 * 60 * 60 * 1000;
		Integer sqlDateType;
		String dateFormat = null;
		String dateNameFormat = null;
		Long interval = null;
		if (periodTime <= oneDayTime / 4) {
			sqlDateType = 0;
			timeFrom = DateUtil.getFirstTimeOfHourQuarter(timeFrom);
			timeTo = DateUtil.getFirstTimeOfHourQuarter(timeTo);
			dateFormat = "yyyyMMddHH";
			dateNameFormat = "H时";
			interval = 15 * 60 * 1000l;
		} else if (periodTime <= oneDayTime) {
			sqlDateType = 1;
			timeFrom = DateUtil.getFirstTimeOfHour(timeFrom);
			timeTo = DateUtil.getFirstTimeOfHour(timeTo);
			dateFormat = "yyyyMMddHH";
			dateNameFormat = "d日H时";
			interval = 60 * 60 * 1000l;
		} else if (periodTime <= 15 * oneDayTime) {
			sqlDateType = 2;
			timeFrom = DateUtil.getFirstTimeOfDayHalf(timeFrom);
			timeTo = DateUtil.getFirstTimeOfDayHalf(timeTo);
			dateFormat = "yyyyMMdda";
			dateNameFormat = "d日a";
			interval = 12 * 60 * 60 * 1000l;
		} else if (periodTime <= 30 * oneDayTime) {
			sqlDateType = 3;
			timeFrom = DateUtil.getFirstTimeOfDay(timeFrom);
			timeTo = DateUtil.getFirstTimeOfDay(timeTo);
			dateFormat = "yyyyMMdd";
			dateNameFormat = "M月d日";
			interval = 24 * 60 * 60 * 1000l;
		} else if (periodTime <= 2 * 365 * oneDayTime) {
			sqlDateType = 4;
			dateFormat = "yyyyMM";
			dateNameFormat = "yy年M月";
		} else if (periodTime <= 6 * 365 * oneDayTime) {
			sqlDateType = 5;
			dateFormat = "yyyy";
			dateNameFormat = "yy年";
		} else {
			sqlDateType = 6;
			dateFormat = "yyyy";
			dateNameFormat = "yy年";
		}
		params.put("dateType", sqlDateType);
		List<Map<String, Object>> counts = this.countDate(params);
		
		Map<String, Object> countMap = new HashMap<>();
		for (Map<String, Object> count: counts) countMap.put((String) count.get("dateNum"), count.get("count"));
		
		List<Map<String, Object>> allCounts = new ArrayList<>();
		if (sqlDateType == 0) {
			for (long i = timeFrom.getTime(); i <= timeTo.getTime(); i += interval) {
				Date curr = new Date(i);
				Integer hourQuarter = (DateUtil.getMinute(curr) + 15) / 15;
				String key = DateUtil.getDateFormat(curr, dateFormat) + hourQuarter;
				String dateName =  DateUtil.getDateFormat(curr, dateNameFormat) + hourQuarter + "刻";
				appendToAllCounts(countMap, allCounts, key, dateName);
			}
		} else if (sqlDateType == 1 || sqlDateType == 2 || sqlDateType == 3) {
			for (long i = timeFrom.getTime(); i <= timeTo.getTime(); i += interval) {
				Date curr = new Date(i);
				String key = DateUtil.getDateFormat(curr, dateFormat).replace("上午", "AM").replace("下午", "PM");
				appendToAllCounts(countMap, allCounts,
					key, DateUtil.getDateFormat(curr, dateNameFormat));
			}
		} else if (sqlDateType == 4) {
			Integer fromYear = DateUtil.getYear(timeFrom);
			Integer fromMonth = DateUtil.getMonth(timeFrom);
			Integer toYear = DateUtil.getYear(timeTo);
			Integer toMonth = DateUtil.getMonth(timeTo);
			for (int i = 0; i <= (toYear - fromYear) * 12 + toMonth - fromMonth; i++) {
				Date curr = DateUtil.getMonthAfter(timeFrom, i);
				appendToAllCounts(countMap, allCounts,
					DateUtil.getDateFormat(curr, dateFormat), DateUtil.getDateFormat(curr, dateNameFormat));
			}
		} else if (sqlDateType == 5) {
			Integer fromYear = DateUtil.getYear(timeFrom);
			Integer fromMonthQuarter = (DateUtil.getMonth(timeFrom) + 2) / 3;
			Integer toYear = DateUtil.getYear(timeTo);
			Integer toMonthQuarter = (DateUtil.getMonth(timeTo) + 2) / 3;
			for (int i = 0; i <= (toYear - fromYear) * 4 + toMonthQuarter - fromMonthQuarter; i++) {
				Date curr = DateUtil.getMonthAfter(timeFrom, i * 3);
				Integer monthQuarter = (DateUtil.getMonth(curr) + 2) / 3;
				String key = DateUtil.getDateFormat(curr, dateFormat) + monthQuarter;
				String dateName =  DateUtil.getDateFormat(curr, dateNameFormat) + monthQuarter + "季度";
				appendToAllCounts(countMap, allCounts, key, dateName);
			}
		} else {
			Integer fromYear = DateUtil.getYear(timeFrom);
			Integer toYear = DateUtil.getYear(timeTo);
			for (int i = 0; i <= toYear - fromYear; i++) {
				Date curr = DateUtil.getYearAfter(timeFrom, i);
				appendToAllCounts(countMap, allCounts,
					DateUtil.getDateFormat(curr, dateFormat), DateUtil.getDateFormat(curr, dateNameFormat));
			}
		}
		
		return allCounts;
	}
	
	public List<Map<String, Object>> countSameHour(String webKey, Date timeFrom, Date timeTo) {
		Date currTime = new Date();
		if (timeFrom == null) timeFrom = DateUtil.getDayBefore(currTime);
		if (timeTo == null) timeTo = currTime;
		
		Map<String, Object> params = new HashMap<>();
		params.put("webKey", webKey);
		params.put("timeFrom", DateUtil.getDateTimeFormat(timeFrom));
		params.put("timeTo", DateUtil.getDateTimeFormat(timeTo));
		params.put("dateFormat", "%k");
		List<Map<String, Object>> counts = this.countDate(params);
		Map<String, Object> countMap = new HashMap<>();
		for (Map<String, Object> count: counts) countMap.put((String) count.get("dateNum"), count.get("count"));
		List<Map<String, Object>> allCounts = new ArrayList<>();
		Map<String, Object> allCount;
		for (int i = 0; i < 24; i++) {
			allCount = new HashMap<>();
			allCount.put("date", i + "时");
			if (countMap.containsKey("" + i)) {
				allCount.put("count", countMap.get("" + i));
			} else {
				allCount.put("count", 0);
			}
			allCounts.add(allCount);
		}
		return allCounts;
	}
	
	public List<Map<String, Object>> countState(String webKey, Date timeFrom, Date timeTo) {
		Date currTime = new Date();
		if (timeFrom == null) timeFrom = DateUtil.getDayBefore(currTime);
		if (timeTo == null) timeTo = currTime;
		
		Map<String, Object> params = new HashMap<>();
		params.put("webKey", webKey);
		params.put("timeFrom", DateUtil.getDateTimeFormat(timeFrom));
		params.put("timeTo", DateUtil.getDateTimeFormat(timeTo));
		
		return this.countState(params);
	}
	
	private void appendToAllCounts(Map<String, Object> countMap, List<Map<String, Object>> allCounts, String key, String dateName) {
		Map<String, Object> count = new HashMap<>();
		count.put("date", dateName);
		if (countMap.containsKey(key)) {
			count.put("count", countMap.get(key));
		} else {
			count.put("count", 0);
		}
		allCounts.add(count);
	}

}
