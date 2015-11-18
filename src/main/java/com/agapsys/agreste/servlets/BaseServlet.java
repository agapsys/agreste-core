/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.servlets;

import com.agapsys.agreste.entities.AbstractUser;
import com.agapsys.agreste.services.ServiceException;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.action.dispatcher.TransactionalServlet;
import com.agapsys.web.toolkit.AbstractExceptionReporterModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.AbstractWebApplication.LogType;
import com.agapsys.web.toolkit.BadRequestException;
import com.agapsys.web.toolkit.GsonSerializer;
import com.agapsys.web.toolkit.HttpUtils;
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.ObjectSerializer;
import com.agapsys.web.toolkit.Service;
import java.io.IOException;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends TransactionalServlet {

	private final LazyInitializer<ObjectSerializer> objectSerializer = new LazyInitializer<ObjectSerializer>() {

		@Override
		protected ObjectSerializer getLazyInstance(Object... params) {
			return _getSerializer();
		}
	};
	
	protected ObjectSerializer _getSerializer() {
		return new GsonSerializer();
	}
	
	@Override
	public boolean onError(HttpExchange exchange, Throwable t) {
		super.onError(exchange, t); // <-- closes JpaTransaction associated with the request

		if (t instanceof BadRequestException || t instanceof ServiceException) {
			HttpServletResponse resp = exchange.getResponse();
			logRequest(exchange, LogType.WARNING, t.getMessage());
			
			int code;
			
			if (t instanceof BadRequestException) {
				code = ((BadRequestException)t).getCode();
			} else {
				code = ((ServiceException)t).getCode();
			}
			
			try {
				resp.setStatus(code);
				resp.getWriter().print(t.getMessage());
				
				return false; // <-- Error will not propagate.
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			
		} else if(t instanceof OptimisticLockException) {
			HttpServletResponse resp = exchange.getResponse();
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
			return false; // <-- Error will not propagate
			
		} else {
			String stackTrace = AbstractExceptionReporterModule.getStackTrace(t);
			logRequest(exchange, LogType.ERROR, stackTrace);
			return true; // Error will propagate
		}
	}

	@Override
	public void onNotAllowed(HttpExchange exchange) {
		super.onNotAllowed(exchange);
		
		if (exchange.getResponse().getStatus() == HttpServletResponse.SC_FORBIDDEN) {
			logRequest(exchange, LogType.WARNING, "Access denied");
		}
	}

	
	protected <T extends Module> T getModule(Class<T> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}
	
	protected <T extends Service> T getService(Class<T> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	
	
	protected String getOptionalParameter(HttpExchange exchange, String paramName, String defaultValue) {
		return HttpUtils.getOptionalParameter(exchange.getRequest(), paramName, defaultValue);
	}
	
	protected String getMandatoryParamter(HttpExchange exchange, String paramName) throws BadRequestException {
		return HttpUtils.getMandatoryParamter(exchange.getRequest(), paramName);
	}
	
	
	protected <T> T readObject(HttpExchange exchange, Class<T> targetClass) throws BadRequestException {
		return objectSerializer.getInstance().readObject(exchange.getRequest(), targetClass);
	}
	
	protected void writeObject(HttpExchange exchange, Object object) {
		objectSerializer.getInstance().writeObject(exchange.getResponse(), object);
	}
	
	
	protected AbstractUser getSessionUser(HttpExchange exchange) {
		return (AbstractUser) exchange.getSessionUser();
	}
	
	protected void setSessionUser(HttpExchange exchange, AbstractUser user) {
		getUserManager().setSessionUser(exchange, user);
	}
	
	protected void clearSessionUser(HttpExchange exchange) {
		getUserManager().clearSessionUser(exchange);
	}
		
	protected String getLogMessage(HttpExchange exchange, String message) {
		HttpServletRequest req = exchange.getRequest();
		
		AbstractUser sessionUser = getSessionUser(exchange);
		
		StringBuffer requestUrl = req.getRequestURL();
		if (req.getQueryString() != null)
			requestUrl.append("?").append(req.getQueryString());

		String finalMessage =  String.format("%s %s\nIP: %s\nUser-agent: %s\nUser id: %s%s",
			req.getMethod(),
			requestUrl,
			HttpUtils.getOriginIp(req),
			HttpUtils.getOriginUserAgent(req),
			sessionUser == null ? "none" : "" + sessionUser.getId(),
			message != null && !message.trim().isEmpty() ? "\n\n" + message : ""
		);
		
		return finalMessage;
	}

	protected void logRequest(HttpExchange exchange, LogType logType, String message) {
		String consoleLogMessage = String.format("%s\n----\n%s\n----", message, getLogMessage(exchange, null));
		AbstractWebApplication.getRunningInstance().log(logType, consoleLogMessage);
	}
	// =========================================================================
}
