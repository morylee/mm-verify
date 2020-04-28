package org.mm.core.support.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.mm.core.Constants;
import org.mm.core.util.RedisUtil;
import org.mm.core.util.TypeParseUtil;
import org.mm.model.Account;
import org.mm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthManager {

	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private AccountService accountService;
	
	public boolean login(Entity entity) {
		if (entity.isNull()) return false;
		
		Map<String, Object> params = new HashMap<>();
		params.put(entity.getAuthType(), entity.getAccount());
		Account acc = accountService.findByParams(params);
		if (acc != null && acc.getPassword().equals(entity.getPassword())) {
			HttpSession session = entity.getSession();
			session.setAttribute(Constants.CURRENT_USER, acc);
			
			String sessionId = session.getId();
			redisUtil.hset(sessionId, "authType", entity.getAuthType(), Constants.SESSION_EXPIRED_TIME);
			redisUtil.hset(sessionId, "account", entity.getAccount(), Constants.SESSION_EXPIRED_TIME);
			redisUtil.hset(sessionId, "password", entity.getPassword(), Constants.SESSION_EXPIRED_TIME);
			
			return true;
		} else {
			return false;
		}
	}
	
	public void keepAlive(Entity entity) {
		HttpSession session = entity.getSession();
		if (session == null) return;
		String sessionId = session.getId();
		if (redisUtil.hasKey(sessionId)) redisUtil.expire(sessionId, Constants.SESSION_EXPIRED_TIME);
	}
	
	public void logout(Entity entity) {
		HttpSession session = entity.getSession();
		if (session == null) return;
		session.removeAttribute(Constants.CURRENT_USER);
		
		redisUtil.del(session.getId());
	}
	
	public Account getUser(Entity entity) {
		HttpSession session = entity.getSession();
		if (session == null) return null;
		Account acc = (Account) session.getAttribute(Constants.CURRENT_USER);
		if (acc == null) {
			String sessionId = session.getId();
			if (redisUtil.hasKey(sessionId)) {
				String authType = TypeParseUtil.convertToString(redisUtil.hget(sessionId, "authType"));
				String account = TypeParseUtil.convertToString(redisUtil.hget(sessionId, "account"));
				
				if (authType != null && !"".equals(authType) && account != null && !"".equals(account)) {
					Map<String, Object> params = new HashMap<>();
					params.put(authType, account);
					acc = accountService.findByParams(params);
					
					session.setAttribute(Constants.CURRENT_USER, acc);
				}
			}
		}
		
		return acc;
	}

}
