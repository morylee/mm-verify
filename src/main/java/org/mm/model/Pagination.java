package org.mm.model;

import java.util.Map;

import org.mm.core.Constants;

public class Pagination {

	private Integer page;
	private Integer pageSize;
	private Integer total;
	private Integer totalPage;
	private Integer realPage;
	private Boolean overRange;
	
	public Pagination(Integer page, Integer pageSize, Integer total) {
		if (page == null) page = 1;
		if (pageSize == null) pageSize = Constants.DEFAULT_PAGE_SIZE;
		if (total == null) total = 0;
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
		this.calculate();
	}
	
	private void calculate() {
		this.totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
		this.overRange = this.totalPage == 0 || this.page > this.totalPage;
		this.realPage = this.page;
		if (this.overRange) this.realPage = this.totalPage;
	}
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		this.calculate();
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
		this.calculate();
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public Integer getRealPage() {
		return realPage;
	}
	public Boolean getOverRange() {
		return overRange;
	}
	public Integer getFrom() {
		return pageSize * (page - 1);
	}
	public void mergeTo(Map<String, Object> params) {
		params.put("page", this.page);
		params.put("pageSize", this.pageSize);
		params.put("total", this.total);
		params.put("totalPage", this.totalPage);
	}

}
