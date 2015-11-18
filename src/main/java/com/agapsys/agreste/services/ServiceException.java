/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.services;

import com.agapsys.web.toolkit.BadRequestException;

public class ServiceException extends Exception {
	
	private final int code;

	public ServiceException(String message) {
		this(BadRequestException.CODE, message);
	}

	public ServiceException(int code, String message) {
		super(message);

		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}

