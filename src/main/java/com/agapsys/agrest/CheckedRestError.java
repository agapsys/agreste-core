/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

public class CheckedRestError extends Exception implements RestError {
	private final RestError wrappedError;
	
	public CheckedRestError(Integer code, String message) {
		wrappedError = new InternalRestError(code, message);
	}

	public CheckedRestError(String message) {
		this(null, message);
	}
	
	public CheckedRestError() {
		this (null, null);
	}
	
	@Override
	public Integer getCode() {
		return wrappedError.getCode();
	}

	@Override
	public String getMessage() {
		return wrappedError.getMessage();
	}
}
