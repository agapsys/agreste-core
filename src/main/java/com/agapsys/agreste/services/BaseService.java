/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste.services;

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

	protected Object getGlobalAttribute(String name) {
		return attributeService.getAttribute(name);
	}
	
}
