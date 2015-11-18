/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

public class DtoValidationException extends RuntimeException {
	private final String fieldName;

	DtoValidationException(String fieldName) {
		super("Required field: " + fieldName);
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
