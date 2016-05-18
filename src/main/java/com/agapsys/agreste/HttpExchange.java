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

import com.agapsys.rcf.LazyInitializer;
import com.agapsys.rcf.exceptions.BadRequestException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com.br)
 */
public class HttpExchange extends com.agapsys.rcf.HttpExchange {
	// STATIC SCOPE ============================================================
	/**
	 * Reads an object passed as request parameters.
	 * @param <T> object type
	 * @param serializer serializer
	 * @param req HTTP request
	 * @param dtoClass object class
	 * @return read object
	 * @throws BadRequestException if it was not possible to read expected object.
	 */
	public static <T> T readParameterObject(ParamMapSerializer serializer, HttpServletRequest req, Class<T> dtoClass) throws BadRequestException {
		try {
			return serializer.getObject(req.getParameterMap(), dtoClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final LazyInitializer<ParamMapSerializer> paramMapSerializer = new LazyInitializer<ParamMapSerializer>() {

		@Override
		protected ParamMapSerializer getLazyInstance() {
			return getParamMapSerializer();
		}
		
	};
	
	public HttpExchange(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}
	
	/**
	 * Returns the parameter map serializer used by this HTTP exchange.
	 * @return the parameter map serializer used by this HTTP exchange.
	 */
	protected ParamMapSerializer getParamMapSerializer() {
		return new ParamMapSerializer();
	}
	
	/**
	 * Returns the managed JPA transaction associated with this HTTP exchange.
	 * @return the managed JPA transaction associated with this HTTP exchange.
	 */
	public JpaTransaction getJpaTransaction() {
		return (JpaTransaction) getRequest().getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
	}
	
	/**
	 * Reads an object passed as request parameters.
	 * @param <T> object type
	 * @param dtoClass object class
	 * @return read object
	 * @throws BadRequestException if it was not possible to read expected object.
	 */
	public <T> T readParameterObject(Class<T> dtoClass) throws BadRequestException {
		return readParameterObject(paramMapSerializer.getInstance(), getRequest(), dtoClass);
	}
	

	public <T> T getOptionalRequestParameter(Class<T> targetClass, String paramName, T defaultValue) throws BadRequestException {
		try {
			String paramValue = getOptionalRequestParameter(paramName, null);
			if (paramValue == null) return defaultValue;
			
			return paramMapSerializer.getInstance().getParameter(paramValue, targetClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}
	
	public final <T> T getMandatoryRequestParameter(Class<T> targetClass, String paramName) throws BadRequestException {
		return getMandatoryRequestParameter(targetClass, paramName, "Missing parameter: %s", paramName);
	}
	
	public <T> T getMandatoryRequestParameter(Class<T> targetClass, String paramName, String errorMessage, Object...errMsgArgs) throws BadRequestException {
		try {
			String paramValue = getMandatoryRequestParameter(paramName, errorMessage, errMsgArgs);
			return paramMapSerializer.getInstance().getParameter(paramValue, targetClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}	
	// =========================================================================
}
