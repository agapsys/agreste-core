/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.servlets;

import com.agapsys.agreste.dto.MapSerializer;
import com.agapsys.agreste.model.LoggedUser;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.action.dispatcher.TransactionalServlet;
import com.agapsys.web.toolkit.AbstractExceptionReporterModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.BadRequestException;
import com.agapsys.web.toolkit.ClientException;
import com.agapsys.web.toolkit.GsonSerializer;
import com.agapsys.web.toolkit.HttpUtils;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.ObjectSerializer;
import com.agapsys.web.toolkit.Service;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends TransactionalServlet {
	
	private final LazyInitializer<ObjectSerializer> objectSerializer = new LazyInitializer<ObjectSerializer>() {

		@Override
		protected ObjectSerializer getLazyInstance(Object... params) {
			return _getObjectSerializer();
		}
	};
	private final LazyInitializer<MapSerializer> mapSerializer = new LazyInitializer<MapSerializer>() {

		@Override
		protected MapSerializer getLazyInstance(Object... params) {
			return _getMapSerializer();
		}
		
	};
	
	
	protected MapSerializer _getMapSerializer() {
		return new MapSerializer();
	}
	
	protected final MapSerializer getMapSerializer() {
		return mapSerializer.getInstance();
	}
	
	protected ObjectSerializer _getObjectSerializer() {
		return new GsonSerializer();
	}
	
	protected final ObjectSerializer getObjectSerializer() {
		return objectSerializer.getInstance();
	}
	
	
	@Override
	protected boolean onError(HttpExchange exchange, Throwable t) {
		super.onError(exchange, t); // <-- closes JpaTransaction associated with the request

		if (t instanceof ClientException) {
			HttpServletResponse resp = exchange.getResponse();
			logRequest(exchange, LogType.WARNING, t.getMessage());
			
			int code = ((ClientException)t).getCode();
			
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
	protected void onNotAllowed(HttpExchange exchange) {
		super.onNotAllowed(exchange);
		
		if (exchange.getResponse().getStatus() == HttpServletResponse.SC_FORBIDDEN) {
			logRequest(exchange, LogType.WARNING, "Access denied");
		}
	}

	
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}
	
	public <T extends Service> T getService(Class<T> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	
	
	public String getOptionalParameter(HttpExchange exchange, String paramName, String defaultValue) {
		return HttpUtils.getOptionalParameter(exchange.getRequest(), paramName, defaultValue);
	}
	
	public String getMandatoryParamter(HttpExchange exchange, String paramName) throws BadRequestException {
		return HttpUtils.getMandatoryParamter(exchange.getRequest(), paramName);
	}
	
	public <T> T getParameterDto(HttpExchange exchange, Class<T> dtoClass) throws BadRequestException {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		
		for (Map.Entry<String, String[]> entry : exchange.getRequest().getParameterMap().entrySet()) {
			fieldMap.put(entry.getKey(), entry.getValue()[0]);
		}
		
		try {
			return mapSerializer.getInstance().getObject(fieldMap, dtoClass);
		} catch (MapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}
	
	
	public <T> T readObject(HttpExchange exchange, Class<T> targetClass) throws BadRequestException {
		return objectSerializer.getInstance().readObject(exchange.getRequest(), targetClass);
	}
	
	public void writeObject(HttpExchange exchange, Object object) {
		objectSerializer.getInstance().writeObject(exchange.getResponse(), object);
	}
	
	
	public LoggedUser getLoggedUser(HttpExchange exchange) {
		return (LoggedUser) getUserManager().getUser(exchange);
	}
	
	public void registerLoggedUser(HttpExchange exchange, LoggedUser user) {
		getUserManager().login(exchange, user);
	}
	
	public void unregisterLoggedUser(HttpExchange exchange) {
		getUserManager().logout(exchange);
	}
	
	
	public String getLogMessage(HttpExchange exchange, String message) {
		HttpServletRequest req = exchange.getRequest();
		
		LoggedUser loggedUser = getLoggedUser(exchange);
		
		StringBuffer requestUrl = req.getRequestURL();
		if (req.getQueryString() != null)
			requestUrl.append("?").append(req.getQueryString());

		String finalMessage =  String.format("%s %s\nIP: %s\nUser-agent: %s\nUser id: %s%s",
			req.getMethod(),
			requestUrl,
			HttpUtils.getOriginIp(req),
			HttpUtils.getOriginUserAgent(req),
			loggedUser != null ? "" + loggedUser.getId() : "none",
			message != null && !message.trim().isEmpty() ? "\n\n" + message : ""
		);
		
		return finalMessage;
	}

	public void logRequest(HttpExchange exchange, LogType logType, String message) {
		String consoleLogMessage = String.format("%s\n----\n%s\n----", message, getLogMessage(exchange, null));
		AbstractWebApplication.getRunningInstance().log(logType, consoleLogMessage);
	}
}
