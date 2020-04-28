package org.mm.core.exception;

import org.mm.core.HttpCode;

/**
 * @author LiChenhui
 * @version 2017年9月5日 16点50分
 */
@SuppressWarnings("serial")
public class DataParseException extends BaseException {
	public DataParseException() {
	}

	public DataParseException(Throwable ex) {
		super(ex);
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(String message, Throwable ex) {
		super(message, ex);
	}

	protected HttpCode getHttpCode() {
		return HttpCode.INTERNAL_SERVER_ERROR;
	}
}
