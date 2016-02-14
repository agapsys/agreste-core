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

package com.agapsys.agreste.servlets;

import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.JpaTransactionFilter;
import com.agapsys.agreste.WebSecurity;
import com.agapsys.agreste.dto.MapSerializer;
import com.agapsys.agreste.exceptions.BadRequestException;
import com.agapsys.agreste.exceptions.ClientException;
import com.agapsys.agreste.model.AbstractUser;
import com.agapsys.agreste.utils.GsonSerializer;
import com.agapsys.agreste.utils.ObjectSerializer;
import com.agapsys.security.NotAllowedException;
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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;

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
	
	protected JpaTransaction getJpaTransaction() {
		return (JpaTransaction) attributeService.getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
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
	protected void onError(HttpExchange exchange, Throwable t) {
		super.onError(exchange, t);

		if (t instanceof NotAllowedException) {
			logRequest(exchange, LogType.WARNING, "Blocked request (not allowed)");
		} else if (t instanceof ClientException) {
			logRequest(exchange, LogType.WARNING, t.getMessage());
		} else if (!(t instanceof OptimisticLockException)){
			String stackTrace = AbstractExceptionReporterModule.getStackTrace(t);
			logRequest(exchange, LogType.ERROR, stackTrace);
		}
	}
	
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}
	
	public <T extends Service> T getService(Class<T> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	
	public AbstractUser getCurrentUser() {
		return WebSecurity.getCurrentUser();
	}
	
	public void setCurrentUser(AbstractUser user) {
		WebSecurity.setCurrentUser(user);
	}
	
	public String getOptionalParameter(HttpExchange exchange, String paramName, String defaultValue) {
		return HttpUtils.getOptionalParameter(exchange.getRequest(), paramName, defaultValue);
	}
	
	public String getMandatoryParameter(HttpExchange exchange, String paramName) throws BadRequestException {
		try {
			return HttpUtils.getMandatoryParameter(exchange.getRequest(), paramName);
		} catch (HttpUtils.BadRequestException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}
	
	public String getMandatoryParameter(HttpExchange exchange, String paramName, String errorMessage, Object...errMsgArgs) throws BadRequestException {
		try {
			return HttpUtils.getMandatoryParameter(exchange.getRequest(), paramName, errorMessage, errMsgArgs);
		} catch (HttpUtils.BadRequestException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}

	public <T> T readParameterObject(HttpExchange exchange, Class<T> dtoClass) throws BadRequestException {
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
		
		AbstractUser loggedUser = WebSecurity.getCurrentUser();
		
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
