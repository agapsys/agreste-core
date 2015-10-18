/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

import com.agapsys.agrest.CodeException;

public class BadRequestException extends RuntimeException implements CodeException {
	public final Integer code;
	
	public BadRequestException(Integer code, String message) {
		super(message);
		this.code = code;
	}
	
	public BadRequestException() {
		this(null, null);
	}
	
	public BadRequestException(String message) {
		this(null, message);
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
}
