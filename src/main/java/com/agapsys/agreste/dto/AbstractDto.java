/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

import java.lang.reflect.Field;

public abstract class AbstractDto {
	
	public void validate() throws DtoValidationException {
		Field[] fields = getClass().getDeclaredFields();
		
		for (Field field : fields) {
			
			field.setAccessible(true);
			
			try {
				
				if (AbstractDto.class.isAssignableFrom(field.getType())) {
					AbstractDto dtoField = (AbstractDto) field.get(this);
					if (dtoField != null) {
						try {
							dtoField.validate();
						} catch (DtoValidationException ex) {
							String fullPathFieldName = String.format("%s.%s", field.getName(), ex.getFieldName());
							throw new DtoValidationException(fullPathFieldName);
						}
					}
				}

				DtoRequired[] annotations = field.getAnnotationsByType(DtoRequired.class);
				DtoRequired annotation;

				if (annotations.length != 0)
					annotation = annotations[0];
				else
					annotation = null;

				if (annotation != null) {
					if (field.get(this) == null)
						throw new DtoValidationException(field.getName());

					if (field.getType() == String.class) {
						String str = (String) field.get(this);

						if (str.trim().isEmpty() && !annotation.acceptEmpty())
							throw new DtoValidationException(field.getName());
					}

				}

			} catch (IllegalAccessException | IllegalArgumentException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
