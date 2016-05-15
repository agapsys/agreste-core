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
package com.agapsys.agreste.app.controllers;

import com.agapsys.agreste.Controller;
import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.app.entities.User;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author Leandro Oliveira (leandro@agapsys)
 */
public class BaseController extends Controller {

	@Override
	protected User getUser(HttpServletRequest req) {
		User user = (User) super.getUser(req);
		
		JpaTransaction jpa = getJpaTransaction(req);
		
		if (user != null && jpa != null) {
			EntityManager em = jpa.getEntityManager();
			
			if (!em.contains(user)) {
				user = em.find(User.class, user.getId());
			}
		}
		
		return user;
	}
	
}
