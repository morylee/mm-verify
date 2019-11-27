package org.mm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.mm.model.Account;

@Mapper
public interface AccountMapper {

	public Account find(Long id);

}
