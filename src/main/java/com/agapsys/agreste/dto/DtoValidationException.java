/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
