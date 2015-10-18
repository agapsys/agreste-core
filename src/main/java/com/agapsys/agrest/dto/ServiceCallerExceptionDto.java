/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.services.ServiceException;

public class ServiceCallerExceptionDto {
	public int    errorCode;
	public String message;
	
	public ServiceCallerExceptionDto(ServiceException ex) {
		this.errorCode = ex.getErrorCode();
		this.message   = ex.getMessage();
	}
}
