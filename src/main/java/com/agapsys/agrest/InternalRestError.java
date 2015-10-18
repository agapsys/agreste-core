/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agrest;

/**
 * Basic implementation of an {@linkplain RestError}
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
class InternalRestError implements RestError {
	private final Integer code;
	private final String message;
	
	public InternalRestError(Integer code, String message) {
		this.message = message;
		this.code = code;
	}
	
	public InternalRestError(String message) {
		this(null, message);
	}
	
	public InternalRestError() {
		this (null, null);
	}
	
	@Override
	public final Integer getCode() {
		return code;
	}
	
	@Override
	public final String getMessage() {
		return message;
	}
}
