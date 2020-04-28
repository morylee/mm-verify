package org.mm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.mm.model.Website;

@Mapper
public interface WebsiteMapper {

	public void add(Website website);
	public void update(Website website);
	public Website findByParams(Map<String, Object> params);
	public List<Website> select(Map<String, Object> params);
	public Integer count(Map<String, Object> params);

}
