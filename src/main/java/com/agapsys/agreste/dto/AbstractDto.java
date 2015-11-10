/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

import java.lang.reflect.Field;

public abstract class AbstractDto {
	// CLASS SCOPE =============================================================
	public static class DtoValidationException extends RuntimeException {

		public DtoValidationException(String message) {
			super(message);
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public void validate() throws DtoValidationException {
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			DtoRequired[] annotations = field.getAnnotationsByType(DtoRequired.class);
			DtoRequired annotation;
			
			if (annotations.length != 0)
				annotation = annotations[0];
			else
				annotation = null;
			
			if (annotation != null) {
				try {
					if (field.get(this) == null)
						throw new DtoValidationException("Required field: " + field.getName());

					if (field.getType() == String.class) {
						String str = (String) field.get(this);

						if (str.trim().isEmpty() && !annotation.acceptEmpty())
							throw new DtoValidationException("Required field: " + field.getName());
					}
				} catch (IllegalAccessException | IllegalArgumentException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
	// =========================================================================
}
