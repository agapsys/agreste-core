/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.entities.EntityObject;
import java.util.Collection;

public abstract class AbstractDtoEntity {
	// CLASS SCOPE =============================================================
	public static <T extends AbstractDtoEntity> Collection<T> getDtoCollection(Class<T> dtoClass, Collection<? extends EntityObject> entityCollection) {
		try {
			Collection<T> destCollection = entityCollection.getClass().newInstance();
			
			for (EntityObject entityObject : entityCollection) {
				destCollection.add(dtoClass.getConstructor(entityObject.getClass()).newInstance(entityObject));
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
	
	public static Collection<Long> getIdCollection(Collection<? extends EntityObject> entityCollection) {
		try {
			Collection<Long> destCollection = entityCollection.getClass().newInstance();
			
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
	public Long id = null;
	
	public AbstractDtoEntity() {}
	
	public AbstractDtoEntity(EntityObject obj) {
		if (obj != null) {
			this.id = obj.getId();
		}
	}
	// =========================================================================
}
