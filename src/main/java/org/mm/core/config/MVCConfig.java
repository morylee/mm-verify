package org.mm.core.config;

import org.mm.core.web.interceptor.CorsInterceptor;
import org.mm.core.web.interceptor.LoginInterceptor;
import org.mm.core.web.interceptor.OperateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

	@Autowired
	private CorsInterceptor corsInterceptor;
	
	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Autowired
	private OperateInterceptor operateInterceptor;
	
	public void addInterceptors(InterceptorRegistry registry) {
		// 配置CORS拦截器
		registry.addInterceptor(corsInterceptor).addPathPatterns("/verify/**");
		
		loginInterceptor.appendFragmentUrl("/website/search");
		loginInterceptor.appendFragmentUrl("/website/info");
		loginInterceptor.appendFragmentUrl("/website/info/*");
		loginInterceptor.appendFragmentUrl("/theme/display");
		registry.addInterceptor(loginInterceptor)
		.addPathPatterns("/**")
//		.excludePathPatterns("/")
		.excludePathPatterns("/login")
		.excludePathPatterns("/flogin")
		.excludePathPatterns("/error")
		.excludePathPatterns("/404")
		.excludePathPatterns("/500")
		.excludePathPatterns("/email/register")
		.excludePathPatterns("/email/registerCheck")
		.excludePathPatterns("/mobile/register")
		.excludePathPatterns("/mobile/login")
		.excludePathPatterns("/user/register")
		.excludePathPatterns("/user/login")
		.excludePathPatterns("/verify/param")
		.excludePathPatterns("/verify/init")
		.excludePathPatterns("/verify/verify")
		.excludePathPatterns("/verify/del")
		.excludePathPatterns("/verify/verifyToken")
		.excludePathPatterns("/verify/delToken")
		.excludePathPatterns("/css/**")
		.excludePathPatterns("/js/**")
		.excludePathPatterns("/images/**")
		.excludePathPatterns("/fonts/**")
		.excludePathPatterns("/static/**")
		.excludePathPatterns("/**.map")
		.excludePathPatterns("/**.ico");
		
		registry.addInterceptor(operateInterceptor)
		.addPathPatterns("/**");
	}

}
