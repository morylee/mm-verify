package org.mm.core.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DomainHttpSessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent event) {
		System.out.println("创建Session: " + event.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("销毁Session: " + event.getSession().getId());
	}

}
