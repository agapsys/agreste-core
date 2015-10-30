/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.toolkit.utils.BadRequestException;
import com.agapsys.web.toolkit.utils.ObjectSerializer;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends ActionServlet {
	// CLASS SCOPE =============================================================
	static class OnErrorController {
		public void onError(ActionServlet actionServlet, HttpExchange exchange, Throwable t) {
			if (t instanceof BadRequestException) {
				HttpServletResponse resp = exchange.getResponse();

				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				try {
					resp.getWriter().print(t.getMessage());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} else {
				actionServlet.onError(exchange, t);
			}
		}
	}
	
	static final OnErrorController ON_ERROR_CONTROLLER = new OnErrorController();
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	@Override
	public void onError(HttpExchange exchange, Throwable t) {
		ON_ERROR_CONTROLLER.onError(this, exchange, t);
	}
	// =========================================================================
}
