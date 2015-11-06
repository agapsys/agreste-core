/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.modules;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

public class PersistenceModule extends com.agapsys.web.toolkit.PersistenceModule {

	@Override
	protected EntityManager _getEntityManager() {
		EntityManager em = super._getEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		return em;
	}
}
