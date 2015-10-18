/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

public class ServiceException extends CheckedRestError {
	public ServiceException(Integer code, String message) {
		super(code, message);
	}
	
	public ServiceException(String message) {
		this(null, message);
	}
	
	public ServiceException() {
		this(null, null);
	}
}
