/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.action.dispatcher.TransactionalServlet;
import com.agapsys.web.toolkit.utils.BadRequestException;
import com.agapsys.web.toolkit.utils.ObjectSerializer;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public abstract class TransactionalBaseServlet extends TransactionalServlet {
	private final LazyInitializer<ObjectSerializer> serializer = new LazyInitializer<ObjectSerializer>() {

		@Override
		protected ObjectSerializer getLazyInstance(Object... params) {
			return _getSerializer();
		}
		
	};
	
	protected abstract ObjectSerializer _getSerializer();
	
	@Override
	public void onError( HttpExchange exchange, Throwable t) {
		if (t instanceof BadRequestException) {
			HttpServletResponse resp = exchange.getResponse();
			
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			try {
				resp.getWriter().print(t.getMessage());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		} else {
			super.onError(exchange, t);
		}
	}
	
	public <T> T readObject(HttpExchange exchange, Class<T> targetClass) throws BadRequestException {
		return serializer.getInstance().readObject(exchange.getRequest(), targetClass);
	}
	
	public void sendObject(HttpExchange exchange, Object obj) {
		serializer.getInstance().writeObject(exchange.getResponse(), obj);
	}
	
}
