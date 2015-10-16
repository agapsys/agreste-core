/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.services;

/** 
 * An exception thrown due to an invalid service call.
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class ServiceCallerException extends RuntimeException {
	private final int errorCode;

	public ServiceCallerException(int errorCode) {
		this.errorCode = errorCode;
	}

	public ServiceCallerException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
	
