package org.mm.core.exception;

import org.mm.core.HttpCode;

/**
 * 
 * @author LiChenhui
 * @version 2017-09-18
 */
@SuppressWarnings("serial")
public class ExistException extends BaseException {
	public ExistException() {
	}

	public ExistException(String message) {
		super(message);
	}

	public ExistException(String message, Exception e) {
		super(message, e);
	}

	@Override
	protected HttpCode getHttpCode() {
		return HttpCode.ALREADY_EXIST;
	}
}
