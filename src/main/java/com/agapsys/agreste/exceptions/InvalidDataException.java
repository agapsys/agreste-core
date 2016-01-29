/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.exceptions;

public class InvalidDataException extends BadRequestException {

	public InvalidDataException(String message, Object... args) {
		super(message, args);
	}
	
}
