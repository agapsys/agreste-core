/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.servlets;

import com.agapsys.agreste.WebSecurity;
import com.agapsys.agreste.dto.MapSerializer;
import com.agapsys.agreste.exceptions.BadRequestException;
import com.agapsys.agreste.exceptions.ClientException;
import com.agapsys.agreste.model.AbstractUser;
import com.agapsys.agreste.utils.GsonSerializer;
import com.agapsys.agreste.utils.ObjectSerializer;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.Service;
import com.agapsys.web.toolkit.modules.AbstractExceptionReporterModule;
import com.agapsys.web.toolkit.services.AttributeService;
import com.agapsys.web.toolkit.utils.HttpUtils;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends ActionServlet {
	
	// CLASS SCOPE =============================================================
	public static final ObjectSerializer DEFAULT_SERIALIZER = new GsonSerializer();
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final LazyInitializer<ObjectSerializer> objectSerializer = new LazyInitializer<ObjectSerializer>() {

		@Override
		protected ObjectSerializer getLazyInstance() {
			return getObjectSerializer();
		}
	};
	private final LazyInitializer<MapSerializer> mapSerializer = new LazyInitializer<MapSerializer>() {

		@Override
		protected MapSerializer getLazyInstance() {
			return getMapSerializer();
		}
		
	};
	
	private AttributeService attributeService;

	@Override
	protected void onInit() {
		attributeService = getService(AttributeService.class);
	}
	
	
	protected MapSerializer getMapSerializer() {
		return new MapSerializer();
	}
	
	protected ObjectSerializer getObjectSerializer() {
		return DEFAULT_SERIALIZER;
	}
	
	private ObjectSerializer _getObjectSerializer() {
		return objectSerializer.getInstance();
	}
	
	private MapSerializer _getMapSerializer() {
		return mapSerializer.getInstance();
	}
	
	protected Object getGlobalAttribute(String name) {
		return attributeService.getAttribute(name);
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
	
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}
	
	public <T extends Service> T getService(Class<T> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	
	public String getOptionalParameter(HttpExchange exchange, String paramName, String defaultValue) {
		return HttpUtils.getOptionalParameter(exchange.getRequest(), paramName, defaultValue);
	}
	
	public String getMandatoryParameter(HttpExchange exchange, String paramName) throws BadRequestException {
		return HttpUtils.getMandatoryParameter(exchange.getRequest(), paramName);
	}
	
	public String getMandatoryParameter(HttpExchange exchange, String paramName, String errorMessage, Object...errMsgArgs) throws BadRequestException {
		return HttpUtils.getMandatoryParameter(exchange.getRequest(), paramName, errorMessage, errMsgArgs);
	}

	public <T> T getParameterDto(HttpExchange exchange, Class<T> dtoClass) throws BadRequestException {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		
		for (Map.Entry<String, String[]> entry : exchange.getRequest().getParameterMap().entrySet()) {
			fieldMap.put(entry.getKey(), entry.getValue()[0]);
		}
		
		try {
			return _getMapSerializer().getObject(fieldMap, dtoClass);
		} catch (MapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}
	
	public <T> T readObject(HttpExchange exchange, Class<T> targetClass) throws BadRequestException {
		return _getObjectSerializer().readObject(exchange.getRequest(), targetClass);
	}
	
	public void writeObject(HttpExchange exchange, Object object) {
		_getObjectSerializer().writeObject(exchange.getResponse(), object);
	}
	
	protected String getLogMessage(HttpExchange exchange, String message) {
		HttpServletRequest req = exchange.getRequest();
		
		AbstractUser loggedUser = WebSecurity.isRunning() ? WebSecurity.getCurrentUser() : null;
		
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
	// =========================================================================
}
