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
package com.agapsys.agreste.app;

import com.agapsys.agreste.test.MockedWebApplication;
import com.agapsys.web.toolkit.modules.PersistenceModule;
import javax.persistence.EntityManager;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class MyApplication extends MockedWebApplication {

	@Override
	protected void afterApplicationStart() {
		super.afterApplicationStart();
		
		PersistenceModule persistenceModule = getModule(PersistenceModule.class);
		
		EntityManager em = persistenceModule.getEntityManager();
		
		em.getTransaction().begin();
		
		
		new MyUser("user1", "password1").save(em);		
		
		MyUser user = new MyUser("user2", "password2");
		user.addRole(Defs.ACCESS_ROLE);
		user.save(em);
		
		user = (MyUser) new MyUser("user3", "password3").save(em);
		MyGroup group = new MyGroup();
		group.addRole(Defs.ACCESS_ROLE);
		group.addUser(user);
		group.save(em);
		
		em.getTransaction().commit();
		
		em.close();
	}
}