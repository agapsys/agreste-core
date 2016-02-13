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

package com.agapsys.agreste.services;

import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.JpaTransactionFilter;
import com.agapsys.web.toolkit.AbstractService;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.services.AttributeService;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class BaseService  extends AbstractService {

	private AttributeService attributeService;
	
	@Override
	protected void onInit(AbstractWebApplication webApp) {
		super.onInit(webApp);
		attributeService = getService(AttributeService.class);
	}

	protected JpaTransaction getJpaTransaction() {
		return (JpaTransaction) attributeService.getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
	}
	
	protected Object getGlobalAttribute(String name) {
		return attributeService.getAttribute(name);
	}
}
