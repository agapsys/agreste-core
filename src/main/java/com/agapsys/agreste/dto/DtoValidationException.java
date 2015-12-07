/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

public class DtoValidationException extends RuntimeException {
	private final String fieldName;

	public DtoValidationException(String fieldName) {
		this(fieldName, "Required field: %s", fieldName);
	}
	
	public DtoValidationException(String fieldName, String message, Object...args) {
		super(args.length > 0 ? String.format(message, args) : message);
		
		if (fieldName == null || fieldName.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty field name");
		
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
