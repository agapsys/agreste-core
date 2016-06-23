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

import com.agapsys.rcf.exceptions.BadRequestException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class HttpRequest extends com.agapsys.rcf.HttpRequest {

	public HttpRequest(HttpExchange exchange, HttpServletRequest coreRequest) {
		super(exchange, coreRequest);
	}

	@Override
	public HttpExchange getExchange() {
		return (HttpExchange) super.getExchange();
	}

	/**
	 * Reads an object passed as request parameters.
	 *
	 * @param <T> object type
	 * @param serializer serializer
	 * @param dtoClass object class
	 * @return read object
	 * @throws BadRequestException if it was not possible to read expected object.
	 */
	public <T> T readParameterObject(ParamMapSerializer serializer, Class<T> dtoClass) throws BadRequestException {
		try {
			return serializer.getObject(getCoreRequest().getParameterMap(), dtoClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException("Cannot read parameters");
		}
	}

	/**
	 * Reads an object passed as request parameters using exchange default parameter map serializer.
	 *
	 * @param <T> object type
	 * @param dtoClass object class
	 * @return read object
	 * @throws BadRequestException if it was not possible to read expected object.
	 */
	public final <T> T readParameterObject(Class<T> dtoClass) throws BadRequestException {
		return readParameterObject(getExchange().getParamMapSerializer(), dtoClass);
	}

	public <T> T getOptionalParameter(Class<T> targetClass, String paramName, T defaultValue) throws BadRequestException {
		try {
			String paramValue = getOptionalParameter(paramName, null);

			if (paramValue == null) return defaultValue;

			return getExchange().getParamMapSerializer().getParameter(paramValue, targetClass);

		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}

	public <T> T getMandatoryParameter(Class<T> targetClass, String paramName, String errorMessage, Object...errMsgArgs) throws BadRequestException {
		try {
			String paramValue = getMandatoryParameter(paramName, errorMessage, errMsgArgs);
			return getExchange().getParamMapSerializer().getParameter(paramValue, targetClass);
		} catch (ParamMapSerializer.SerializerException ex) {
			throw new BadRequestException(ex.getMessage());
		}
	}

	public final <T> T getMandatoryParameter(Class<T> targetClass, String paramName) throws BadRequestException {
		return getMandatoryParameter(targetClass, paramName, "Missing parameter: %s", paramName);
	}

}
