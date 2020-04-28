package org.mm.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.mm.model.Account;

@Mapper
public interface AccountMapper {

	public void add(Account account);
	public void update(Account account);
	public Account findByParams(Map<String, Object> params);

}
