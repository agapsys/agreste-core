/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.servlets.BadRequestException;

public class BadRequestExceptionDto {
	public int    errorCode;
	public String message;
	
	public BadRequestExceptionDto(BadRequestException ex) {
		this.errorCode = ex.getErrorCode();
		this.message   = ex.getMessage();
	}
}
