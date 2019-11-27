package org.mm.service;

import org.mm.mapper.AccountMapper;
import org.mm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

	@Autowired
	private AccountMapper mapper;
	
	public Account find(Long id) {
		return mapper.find(id);
	}

}
