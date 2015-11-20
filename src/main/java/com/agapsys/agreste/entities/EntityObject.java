/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.entities;

import java.io.Serializable;
import javax.persistence.EntityManager;

public interface EntityObject extends Serializable {
	public Long getId();
	
	public void save(EntityManager em);
}
