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

import com.agapsys.rcf.User;
import com.agapsys.rcf.exceptions.ClientException;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.Service;
import com.agapsys.web.toolkit.modules.ExceptionReporterModule;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.OptimisticLockException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Controller<HE extends HttpExchange> extends com.agapsys.rcf.Controller<HE> {
	// STATIC SCOPE ============================================================
	/**
	 * Returns an application module.
	 * @param <M> module type
	 * @param moduleClass module class.
	 * @return application module associated with given class.
	 */
	public static <M extends Module> M getModule(Class<M> moduleClass) {
		return AbstractWebApplication.getRunningInstance().getModule(moduleClass);
	}

	/**
	 * Returns an application service.
	 * @param <S> service type
	 * @param serviceClass service class.
	 * @return application service associated with given class.
	 */
	public static <S extends Service> S getService(Class<S> serviceClass) {
		return AbstractWebApplication.getRunningInstance().getService(serviceClass);
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	@Override
	protected void onClientError(HE exchange, ClientException error) {
		super.onClientError(exchange, error);
		logRequest(exchange, LogType.WARNING, error.getMessage());
	}

	@Override
	protected boolean onControllerError(HE exchange, Throwable t) throws ServletException, IOException {
		super.onControllerError(exchange, t);

		if (!(t instanceof OptimisticLockException)) { // <-- OptimisticLockException will be handled by JpaFilter!
			String stackTrace = ExceptionReporterModule.getStackTrace(t);
			logRequest(exchange, LogType.ERROR, stackTrace);
		}

		return false;
	}

	@Override
	protected HE getHttpExchange(HttpServletRequest req, HttpServletResponse resp) {
		return (HE) new HttpExchange(req, resp);
	}

	protected String getLogMessage(HE exchange, String message) {
		User loggedUser;

		try {
			loggedUser = exchange.getCurrentUser();
		} catch (ClientException ex) {
			loggedUser = null;
		}

		HttpServletRequest req = exchange.getRequest();

		StringBuffer requestUrl = req.getRequestURL();
		if (req.getQueryString() != null)
			requestUrl.append("?").append(req.getQueryString());

		String finalMessage =  String.format("%s %s\nIP: %s\nUser-agent: %s\nUser id: %s%s",
			req.getMethod(),
			requestUrl,
			exchange.getRequestOriginIp(),
			exchange.getRequestUserAgent(),
			loggedUser != null ? "" + loggedUser.toString(): "none",
			message != null && !message.trim().isEmpty() ? "\n\n" + message : ""
		);

		return finalMessage;
	}

	public void logRequest(HE exchange, LogType logType, String message) {
		String consoleLogMessage = String.format("%s\n----\n%s\n----", message, getLogMessage(exchange, null));
		AbstractWebApplication.getRunningInstance().log(logType, consoleLogMessage);
	}

	private Object getSingleDto(Object obj) {
		if (obj == null)
			return null;

		Dto dtoAnnotation = obj.getClass().getAnnotation(Dto.class);

		if (dtoAnnotation == null)
			return super.getDtoObject(obj);

		Class<?> dtoClass = dtoAnnotation.value();

		try {
			Constructor constructor = dtoClass.getConstructor(obj.getClass());
			return constructor.newInstance(obj);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(String.format("DTO class (%s) does not have an accessible constructor accepting an instance of '%s'", dtoClass.getName(), obj.getClass().getName()), ex);
		}

	}

	private List getDtoList(List objList) {
		List dto = new LinkedList();

		for (Object obj : objList) {
			dto.add(getSingleDto(obj));
		}

		return dto;
	}

	private Map getDtoMap(Map<Object, Object> objMap) {
		Map dto = new LinkedHashMap();

		for (Map.Entry entry : objMap.entrySet()) {
			dto.put(getSingleDto(entry.getKey()), getSingleDto(entry.getValue()));
		}

		return dto;
	}

	private Set getDtoSet(Set objSet) {
		Set dto = new LinkedHashSet();

		for (Object obj : objSet) {
			dto.add(getSingleDto(obj));
		}

		return dto;
	}

	@Override
	protected final Object getDtoObject(Object src) {
		Object dto;

		if (src instanceof List) {
			dto = getDtoList((List) src);
		} else if (src instanceof Set) {
			dto = getDtoSet((Set) src);
		} else if (src instanceof Map) {
			dto = getDtoMap((Map<Object, Object>) src);
		} else {
			dto = getSingleDto(src);
		}

		return dto;
	}
	// =========================================================================


}
