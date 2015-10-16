/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.services.ServiceCallerException;

public class ServiceCallerExceptionDto {
	public int    errorCode;
	public String message;
	
	public ServiceCallerExceptionDto(ServiceCallerException ex) {
		this.errorCode = ex.getErrorCode();
		this.message   = ex.getMessage();
	}
}
