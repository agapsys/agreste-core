/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

public class DtoValidationException extends RuntimeException {
	private final String fieldName;
	private final String message;
	
	public DtoValidationException(String fieldName, String message, Object...msgArgs) {
		if (fieldName == null || fieldName.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty field name");
		
		this.fieldName = fieldName;
		
		if (message == null || message.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty message");
		
		this.message = msgArgs.length == 0 ? message : String.format(message, msgArgs);
	}
	
	public DtoValidationException(String fieldName) {
		this(fieldName, "Required field");
	}

	String getUnformattedMessage() {
		return message;
	}
	
	@Override
	public String getMessage() {
		return String.format("%s: %s", message, fieldName);
	}
	
	public String getFieldName() {
		return fieldName;
	}
}
