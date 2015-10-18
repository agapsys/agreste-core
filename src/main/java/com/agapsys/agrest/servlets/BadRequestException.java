/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

public class BadRequestException extends RuntimeException {
	private final Integer errorCode;
	
	public BadRequestException(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public BadRequestException(Integer errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	BadRequestException() {
		errorCode = null;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}
