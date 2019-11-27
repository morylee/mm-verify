package org.mm.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.mm.model.Website;

@Mapper
public interface WebsiteMapper {

	public Website findByParams(Map<String, Object> params);

}
