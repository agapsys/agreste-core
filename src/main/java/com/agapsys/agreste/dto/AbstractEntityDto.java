/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

import com.agapsys.jpa.EntityObject;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractEntityDto extends AbstractDto {
	// CLASS SCOPE =============================================================
	public static interface EntityCollectionFilter<T extends EntityObject> {
		public boolean isAccepted(T entityObject);
	}
	
	public static <T extends AbstractEntityDto> Collection<T> getDtoCollection(Class<T> dtoClass, Collection<? extends EntityObject> entityCollection) {
		return getDtoCollection(dtoClass, entityCollection, null);
	}
	
	public static <T extends AbstractEntityDto> Collection<T> getDtoCollection(Class<T> dtoClass, Collection<? extends EntityObject> entityCollection, EntityCollectionFilter filter) {
		try {
			Collection<T> destCollection;
			
			if (entityCollection instanceof Set) {
				destCollection = new LinkedHashSet<>();
			} else if (entityCollection instanceof List) {
				destCollection = new LinkedList<>();
			} else {
				throw new UnsupportedOperationException("Unsupported collection: " + entityCollection.getClass().getName());
			} 
						
			for (EntityObject entityObject : entityCollection) {
				if (filter == null || filter.isAccepted(entityObject)) {
					destCollection.add(dtoClass.getConstructor(entityObject.getClass()).newInstance(entityObject));
				}
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
	
	public static Collection<Object> getIdCollection(Collection<? extends EntityObject> entityCollection) {
		try {
			Collection<Object> destCollection = entityCollection.getClass().newInstance();
			
			for (EntityObject entityObject : entityCollection) {
				destCollection.add(entityObject.getId());
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
	private Object id = null;
	
	public AbstractEntityDto() {}
	
	public AbstractEntityDto(EntityObject obj) {
		if (obj != null) {
			this.id = obj.getId();
		}
	}
	
	public Object getId() {
		return id;
	}
	public void setObject(Object id) {
		this.id = id;
	}
	// =========================================================================
}
