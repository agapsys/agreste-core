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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractDtoObject {
	// CLASS SCOPE =============================================================
	public static <T extends AbstractDtoObject> Collection<T> getDtoCollection(Class<T> dtoClass, Collection collection) {
		try {
			Collection<T> destCollection;
			
			if (collection instanceof Set) {
				destCollection = new LinkedHashSet<>();
			} else if (collection instanceof List) {
				destCollection = new LinkedList<>();
			} else {
				throw new UnsupportedOperationException("Unsupported collection: " + collection.getClass().getName());
			} 
						
			for (Object object : collection) {
				destCollection.add(dtoClass.getConstructor(object.getClass()).newInstance(object));
			}
			
			return destCollection;
			
		} catch (Throwable t) {
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t);
			}
		}
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	public void validate() throws DtoValidationException {
		Field[] fields = getClass().getDeclaredFields();
		
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			field.setAccessible(true);
			
			try {
				
				if (AbstractDtoObject.class.isAssignableFrom(field.getType())) {
					AbstractDtoObject dtoField = (AbstractDtoObject) field.get(this);
					if (dtoField != null) {
						try {
							dtoField.validate();
						} catch (DtoValidationException ex) {
							String fullPathFieldName = String.format("%s.%s", field.getName(), ex.getFieldName());
							
							throw new DtoValidationException(fullPathFieldName, ex.getUnformattedMessage());
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
	// =========================================================================
}
