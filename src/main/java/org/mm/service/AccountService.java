package org.mm.service;

import java.util.HashMap;
import java.util.Map;

import org.mm.core.config.Resources;
import org.mm.core.exception.ExistException;
import org.mm.core.util.PasswordUtil;
import org.mm.mapper.AccountMapper;
import org.mm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

	@Autowired
	private AccountMapper mapper;
	
	public void add(Account account) {
		mapper.add(account);
	}
	
	public void create(Account account) {
		Account savedAccount = this.findByEmail(account.getEmail());
		if (savedAccount != null) throw new ExistException(Resources.getMessage("ALREADY_USED", Resources.getMessage("ACCOUNT.EMAIL")));
		
		account.setPassword(PasswordUtil.securityPwd(account.getPassword()));
		account.setState(Account.State.Enalbed.getValue());
		account.setRoleType(1);
		this.add(account);
	}
	
	public void update(Account account) {
		mapper.update(account);
	}
	
	public Account findByParams(Map<String, Object> params) {
		return mapper.findByParams(params);
	}
	
	public Account findById(Long id) {
		if (id == null) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		return this.findByParams(params);
	}
	
	public Account findByEmail(String email) {
		if (email == null || "".equals(email)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		
		return this.findByParams(params);
	}
	
	public Account findByMobile(String mobile) {
		if (mobile == null || "".equals(mobile)) return null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("mobile", mobile);
		
		return this.findByParams(params);
	}

}
