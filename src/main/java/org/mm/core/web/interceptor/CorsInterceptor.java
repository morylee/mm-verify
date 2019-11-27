package org.mm.core.web.interceptor;

import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.mm.core.Constants;
import org.mm.core.OriginCheck;
import org.mm.core.util.RedisUtil;
import org.mm.core.web.http.RequestWrapper;
import org.mm.model.Website;
import org.mm.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(Integer.MIN_VALUE)
public class CorsInterceptor implements HandlerInterceptor {

	private static final String PARAMS_SEPARATE = ", ";
	private static final String ALLOWED_ALL = "*";

	@Value("${cors.access.control.maxAge:7200}")
	String corsAccessControlMaxAge;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private WebsiteService websiteService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			
			OriginCheck annotation = method.getAnnotation(OriginCheck.class);
			if (annotation != null) {
				response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, getHeaders(request));
				response.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, corsAccessControlMaxAge);
				if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
					response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
					response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
					response.setStatus(HttpStatus.NO_CONTENT.value());
					
					return false;
				} else {
					String requestOrigin = requestOrigin(request.getHeader(HttpHeaders.ORIGIN));
					if (!annotation.ignore() && requestOrigin != null) {
						JSONObject body = requestBody(request);
						String key = body.has(annotation.key()) ? body.getString(annotation.key()) : null;
						requestOrigin = checkOrigin(key, annotation.init(), request.getHeader(HttpHeaders.ORIGIN));
					}
					response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestOrigin);
					response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, request.getMethod());
				}
			}
		}
		
		return true;
	}
	
	private JSONObject requestBody(HttpServletRequest request) throws JSONException {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		String body = requestWrapper.getBody();
		JSONObject bodyJson = null;
		if (StringUtils.hasText(body) && body.startsWith("{") && body.endsWith("}")) {
			bodyJson = new JSONObject(body);
		} else {
			bodyJson = new JSONObject();
		}
		
		return bodyJson;
	}
	
	private String requestOrigin(String requestOrigin) {
		if (!StringUtils.hasText(requestOrigin)) return null;
		return requestOrigin;
	}
	
	private String domain(String url) {
		int index = url.indexOf("://");
		if (index == -1) return null;
		String subUrl = url.substring(index + 3);
		index = subUrl.indexOf(":");
		if (index > -1) subUrl = subUrl.substring(0, index);
		
		return subUrl;
	}
	
	private String checkOrigin(String key, boolean init, String requestOrigin) {
		if (!StringUtils.hasText(key)) {
			return null;
		}
		
		String reqOriginDomain = domain(requestOrigin); // maybe null
		String allowedOrigin = (String) redisUtil.hget(key, Constants.REQUEST_ORIGIN_KEY);
		if (init && !StringUtils.hasText(allowedOrigin)) {
			Website website = websiteService.findByWebKey(key);
			allowedOrigin = website == null ? null : website.getUrl();
			redisUtil.hset(key, Constants.REQUEST_ORIGIN_KEY, allowedOrigin);
		}
		if (!StringUtils.hasText(allowedOrigin)) {
			return null;
		}
		if (ALLOWED_ALL.equals(allowedOrigin)
			|| allowedOrigin.equalsIgnoreCase(reqOriginDomain)
			|| allowedOrigin.equalsIgnoreCase(requestOrigin)) {
			return requestOrigin;
		}
		if (allowedOrigin.startsWith(ALLOWED_ALL) && reqOriginDomain != null) {
			if (reqOriginDomain.endsWith(allowedOrigin.substring(1))) {
				return requestOrigin;
			}
		}
		
		return null;
	}

	private String getHeaders(HttpServletRequest httpServletRequest) {
		StringBuilder params = new StringBuilder();
		String accessControlRequestHeaders = httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
		if (!(accessControlRequestHeaders == null || accessControlRequestHeaders.isEmpty())) {
			params.append(accessControlRequestHeaders).append(PARAMS_SEPARATE);
		}

		Enumeration<String> names = httpServletRequest.getHeaderNames();
		while (names.hasMoreElements()) {
			params.append(names.nextElement()).append(PARAMS_SEPARATE);
		}
		params.setLength(params.length() - PARAMS_SEPARATE.length());
		return params.toString();
	}

}
