package org.mm.core;

/**
 * Ajax 请求时的自定义查询状态码，主要参考Http状态码，但并不完全对应
 */
public enum HttpCode {
	/** 200请求成功 */
	OK(200, "操作成功"),
	/** 207频繁操作 */
	MULTI_STATUS(207, "操作频繁"),
	/** 302已经存在*/
	ALREADY_EXIST(302, "已经存在"),
	/** 303登录失败 */
	LOGIN_FAIL(303, "登录失败"),
	/** 400请求参数出错 */
	BAD_REQUEST(400, "参数错误"),
	/** 401没有登录 */
	UNAUTHORIZED(401, "没有登录"),
	/** 403没有权限 */
	FORBIDDEN(403, "没有权限"),
	/** 404找不到页面 */
	NOT_FOUND(404, "未找到资源"),
	/** 408请求超时 */
	REQUEST_TIMEOUT(408, "操作超时"),
	/** 409发生冲突 */
	CONFLICT(409, "资源冲突"),
	/** 410已被删除 */
	GONE(410, "资源已删除"),
	/** 417执行失败*/
	FAILED(417, "操作失败"),
	/** 423已被锁定 */
	LOCKED(423, "已被锁定"),
	/** 500服务器出错 */
	INTERNAL_SERVER_ERROR(500, "服务器出错");

	private final Integer value;
	private final String msg;

	private HttpCode(Integer value, String msg) {
		this.value = value;
		this.msg = msg;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return this.msg;
	}

	public String toString() {
		return this.value.toString();
	}
}
