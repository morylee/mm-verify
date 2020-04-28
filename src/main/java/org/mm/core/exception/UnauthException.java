package org.mm.core.exception;

import org.mm.core.HttpCode;

@SuppressWarnings("serial")
public class UnauthException extends BaseException {

	public UnauthException() {
	}

	public UnauthException(Throwable ex) {
		super(ex);
	}

	public UnauthException(String message) {
		super(message);
	}

	public UnauthException(String message, Throwable ex) {
		super(message, ex);
	}
	
	@Override
	protected HttpCode getHttpCode() {
		return HttpCode.UNAUTHORIZED;
	}

}
