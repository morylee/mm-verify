package org.mm.core.util;

import java.util.Map;

import org.mm.model.Pagination;

public class PaginationUtil {

	public static void servicePaging(Map<String, Object> params, Pagination pagination) {
		params.put("from", pagination.getFrom());
		params.put("pageSize", pagination.getPageSize());
	}
	
	public static Pagination controllerPaging(Map<String, Object> params, Integer total, Integer max) {
		if (total != null && total > max) total = max;
		return controllerPaging(params, total);
	}
	
	public static Pagination controllerPaging(Map<String, Object> params, Integer total) {
		Integer page = TypeParseUtil.convertToInteger(params.get("page"));
		Integer pageSize = TypeParseUtil.convertToInteger(params.get("pageSize"));
		
		return controllerPaging(page, pageSize, total);
	}
	
	public static Pagination controllerPaging(Integer page, Integer pageSize, Integer total) {
		return new Pagination(page, pageSize, total);
	}

}
