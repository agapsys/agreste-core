/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

public class DtoValidationException extends RuntimeException {
	static String getErrMessage(String fieldName, String message, Object...msgArgs) {
		return String.format("%s: %s", fieldName, msgArgs.length == 0 ? message : String.format(message, msgArgs));
	}
	
	private final String fieldName;
	
	public DtoValidationException(String fieldName, String message, Object...msgArgs) {
		super(getErrMessage(fieldName, message, msgArgs));
		
		if (fieldName == null || fieldName.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty field name");
		
		this.fieldName = fieldName;
	}
	
	public DtoValidationException(String fieldName) {
		this(fieldName, "Required field", fieldName);
	}
	
	public String getFieldName() {
		return fieldName;
	}
}
