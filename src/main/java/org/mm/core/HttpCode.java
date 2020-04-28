package org.mm.core;

import org.mm.core.config.Resources;

/**
 * Ajax 请求时的自定义查询状态码，主要参考Http状态码，但并不完全对应
 * 
 * @author LiChenhui
 * @version 2017年9月5日 16点48分
 */
public enum HttpCode {
	/** 200请求成功 */
	OK(200),
	/** 207频繁操作 */
	MULTI_STATUS(207),
	/** 302已经存在*/
	ALREADY_EXIST(302),
	/** 303登录失败 */
	LOGIN_FAIL(303),
	/** 400请求参数出错 */
	BAD_REQUEST(400),
	/** 401没有登录 */
	UNAUTHORIZED(401),
	/** 403没有权限 */
	FORBIDDEN(403),
	/** 404找不到页面 */
	NOT_FOUND(404),
	/** 408请求超时 */
	REQUEST_TIMEOUT(408),
	/** 409发生冲突 */
	CONFLICT(409),
	/** 410已被删除 */
	GONE(410),
	/** 417执行失败*/
	FAILED(417),
	/** 423已被锁定 */
	LOCKED(423),
	/** 500服务器出错 */
	INTERNAL_SERVER_ERROR(500);

	private final Integer value;

	private HttpCode(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("HTTPCODE_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}
}
