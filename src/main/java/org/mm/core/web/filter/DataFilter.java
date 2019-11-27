package org.mm.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.mm.core.web.http.RequestWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@WebFilter(urlPatterns = "/*")
@Order(Integer.MIN_VALUE)
public class DataFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}
 
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (servletRequest instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			ServletRequest requestWrapper = new RequestWrapper(httpServletRequest);
			filterChain.doFilter(requestWrapper, servletResponse);
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {

	}

}
