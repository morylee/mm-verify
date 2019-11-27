package org.mm.core.config;

import org.mm.core.web.interceptor.CorsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

	@Autowired
	private CorsInterceptor corsInterceptor;
	
	public void addInterceptors(InterceptorRegistry registry) {
		// 配置CORS拦截器
		registry.addInterceptor(corsInterceptor).addPathPatterns("/verify/**");
	}

}
