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
public class ServiceException extends RuntimeException {
	private final int errorCode;

	public ServiceException(int errorCode) {
		this.errorCode = errorCode;
	}

	public ServiceException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
	
