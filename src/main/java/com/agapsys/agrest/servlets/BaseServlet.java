/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.servlets;

import com.agapsys.agrest.BadRequestException;
import com.agapsys.agrest.dto.RestErrorDto;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.toolkit.utils.ObjectSerializer;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends ActionServlet {
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
			exchange.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendObject(exchange, new RestErrorDto((BadRequestException) t));
		} else {
			super.onError(exchange, t);
		}
	}
	
	public <T> T readObject(HttpExchange exchange, Class<T> targetClass) throws BadRequestException {
		try {
			return serializer.getInstance().readObject(exchange.getRequest(), targetClass);
		} catch (ObjectSerializer.BadRequestException ex) {
			throw new BadRequestException();
		}
	}
	
	public void sendObject(HttpExchange exchange, Object obj) {
		serializer.getInstance().writeObject(exchange.getResponse(), obj);
	}
}
