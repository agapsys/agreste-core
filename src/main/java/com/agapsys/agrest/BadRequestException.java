/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

public class BadRequestException extends UncheckedRestError {
	public BadRequestException(Integer code, String message) {
		super(code, message);
	}
	
	public BadRequestException(String message) {
		this(null, message);
	}
	
	public BadRequestException() {
		this(null, null);
	}
}
