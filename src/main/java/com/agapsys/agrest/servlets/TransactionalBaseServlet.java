/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.TransactionalServlet;

public abstract class TransactionalBaseServlet extends TransactionalServlet {
	@Override
	public void onError( HttpExchange exchange, Throwable t) {
		BaseServlet.ON_ERROR_CONTROLLER.onError(this, exchange, t);
	}
}
