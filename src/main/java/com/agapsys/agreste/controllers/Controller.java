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

package com.agapsys.agreste.controllers;

import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.JpaTransactionFilter;
import com.agapsys.agreste.WebSecurity;
import com.agapsys.agreste.dto.MapSerializer;
import com.agapsys.agreste.model.AbstractUser;
import com.agapsys.rcf.HttpExchange;
import com.agapsys.rcf.LazyInitializer;
import com.agapsys.rcf.exceptions.BadRequestException;
import com.agapsys.rcf.exceptions.ClientException;
import com.agapsys.security.NotAllowedException;
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
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public abstract class Controller extends com.agapsys.rcf.Controller {
	
	private final LazyInitializer<MapSerializer> mapSerializer = new LazyInitializer<MapSerializer>() {

		@Override
		protected MapSerializer getLazyInstance() {
			return getCustomMapSerializer();
		}
		
	};
	
	private AttributeService attributeService;

	@Override
	protected void onInit() {
		super.onInit();
		attributeService = getService(AttributeService.class);
	}
	
	
	protected MapSerializer getCustomMapSerializer() {
		return new MapSerializer();
	}
	
	
	protected JpaTransaction getJpaTransaction() {
		return (JpaTransaction) attributeService.getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
	}
	
	protected Object getGlobalAttribute(String name) {
		return attributeService.getAttribute(name);
	}

	
	@Override
	protected void onClientError(HttpServletRequest req, ClientException error) {
		super.onClientError(req, error);
		logRequest(req, LogType.WARNING, error.getMessage());
	}
	
	@Override
	protected boolean onControllerError(HttpExchange exchange, Throwable t) throws ServletException, IOException {

		if (t instanceof NotAllowedException) { // <-- will be handled by WebSecurityFilter!
			logRequest(exchange.getRequest(), LogType.WARNING, "Blocked request (not allowed)");
		} else if (!(t instanceof OptimisticLockException)) { // <-- will be handled by JpaFilter!
			String stackTrace = AbstractExceptionReporterModule.getStackTrace(t);
			logRequest(exchange.getRequest(), LogType.ERROR, stackTrace);
		}
		
		return false;
	}
	
	protected <T extends Module> T getModule(Class<T> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}
	
	protected <T extends Service> T getService(Class<T> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	
	
	protected AbstractUser getCurrentUser() {
		AbstractUser loggedUser = WebSecurity.getCurrentUser();
		JpaTransaction jpaTransaction = getJpaTransaction();
		
		if (loggedUser != null && jpaTransaction != null) {
			EntityManager em = jpaTransaction.getEntityManager();
			
			if (!em.contains(loggedUser)) {
				loggedUser = em.find(AbstractUser.class, loggedUser.getId());
			}
		}
		
		return loggedUser;
	}
	
	protected void setCurrentUser(AbstractUser user) {
		WebSecurity.setCurrentUser(user);
	}
	
	protected void unregisterCurrentUser() {
		WebSecurity.unregisterCurrentUser();
	}
	
	
	protected String getOptionalParameter(HttpServletRequest req, String paramName, String defaultValue) {
		return HttpUtils.getOptionalParameter(req, paramName, defaultValue);
	}
	
	protected String getMandatoryParameter(HttpServletRequest req, String paramName) throws BadRequestException {
		try {
			return HttpUtils.getMandatoryParameter(req, paramName);
		} catch (HttpUtils.BadRequestException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}
	
	protected String getMandatoryParameter(HttpServletRequest req, String paramName, String errorMessage, Object...errMsgArgs) throws BadRequestException {
		try {
			return HttpUtils.getMandatoryParameter(req, paramName, errorMessage, errMsgArgs);
		} catch (HttpUtils.BadRequestException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}

	protected <T> T readParameterObject(HttpServletRequest req, Class<T> dtoClass) throws BadRequestException {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		
		for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
			fieldMap.put(entry.getKey(), entry.getValue()[0]);
		}
		
		try {
			return mapSerializer.getInstance().getObject(fieldMap, dtoClass);
		} catch (MapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}
	
	
	protected String getLogMessage(HttpServletRequest req, String message) {
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

	protected void logRequest(HttpServletRequest req, LogType logType, String message) {
		String consoleLogMessage = String.format("%s\n----\n%s\n----", message, getLogMessage(req, null));
		AbstractWebApplication.getRunningInstance().log(logType, consoleLogMessage);
	}
	// =========================================================================
}
