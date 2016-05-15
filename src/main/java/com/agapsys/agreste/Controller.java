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

package com.agapsys.agreste;

import com.agapsys.rcf.HttpExchange;
import com.agapsys.rcf.LazyInitializer;
import com.agapsys.rcf.User;
import com.agapsys.rcf.exceptions.BadRequestException;
import com.agapsys.rcf.exceptions.ClientException;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.Service;
import com.agapsys.web.toolkit.modules.AbstractExceptionReporterModule;
import com.agapsys.web.toolkit.utils.HttpUtils;
import java.io.IOException;
import javax.persistence.OptimisticLockException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAllowedException;

public abstract class Controller extends com.agapsys.rcf.Controller {
	
	private final LazyInitializer<ParamMapSerializer> paramMapSerializer = new LazyInitializer<ParamMapSerializer>() {

		@Override
		protected ParamMapSerializer getLazyInstance() {
			return getCustomParamMapSerializer();
		}
		
	};
		
	protected ParamMapSerializer getCustomParamMapSerializer() {
		return new ParamMapSerializer();
	}
	
	protected JpaTransaction getJpaTransaction(HttpServletRequest req) {
		return (JpaTransaction) req.getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
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
		try {
			return paramMapSerializer.getInstance().getObject(req.getParameterMap(), dtoClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}
	
	protected String getLogMessage(HttpServletRequest req, String message) {
		User loggedUser = getUser(req);
		
		StringBuffer requestUrl = req.getRequestURL();
		if (req.getQueryString() != null)
			requestUrl.append("?").append(req.getQueryString());

		String finalMessage =  String.format("%s %s\nIP: %s\nUser-agent: %s\nUser id: %s%s",
			req.getMethod(),
			requestUrl,
			HttpUtils.getOriginIp(req),
			HttpUtils.getOriginUserAgent(req),
			loggedUser != null ? "" + loggedUser.toString(): "none",
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
