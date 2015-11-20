/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.entities;

import javax.persistence.EntityManager;

public abstract class AbstractEntity implements EntityObject {

	@Override
	public void save(EntityManager em) {
		em.persist(this);
	}
}
