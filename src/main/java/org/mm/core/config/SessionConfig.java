package org.mm.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionListener;

import org.mm.core.Constants;
import org.mm.core.web.listener.DomainHttpSessionListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
public class SessionConfig extends RedisHttpSessionConfiguration {

	public SessionConfig() {
		List<HttpSessionListener> list = new ArrayList<>();
		list.add(new DomainHttpSessionListener());
		this.setHttpSessionListeners(list);
		this.setMaxInactiveIntervalInSeconds(Constants.SESSION_EXPIRED_TIME);
	}
	
	// 添加session 监听
	@Override
	public void setHttpSessionListeners(List<HttpSessionListener> listeners) {
		super.setHttpSessionListeners(listeners);
	}
	
	//设置session过期时间
	@Override
	public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
		super.setMaxInactiveIntervalInSeconds(maxInactiveIntervalInSeconds);
	}
}

