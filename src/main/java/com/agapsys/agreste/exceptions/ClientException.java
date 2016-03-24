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

package com.agapsys.agreste.exceptions;

public class ClientException extends RuntimeException {
	private final int httpsStatus;
	private final Integer appStatus;
	
	public ClientException(int httpStatus) {
		this(httpStatus, null);
	}
	
	public ClientException(int httpStatus, Integer appStatus) {
		this(httpStatus, appStatus, "");
	}
	
	public ClientException(int httpStatus, String message, Object...messageArgs) {
		this(httpStatus, null, message, messageArgs);
	}
	
	public ClientException(int httpStatus, Integer appStatus, String message, Object...args) {
		super(args.length > 0 ? String.format(message, args) : message);
		this.httpsStatus = httpStatus;
		this.appStatus = appStatus;
	}
	
	public int getHttpsStatus() {
		return httpsStatus;
	}

	public Integer getAppStatus() {
		return appStatus;
	}
}
