/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.servlets.BadRequestException;

public class BadRequestExceptionDto {
	public String message;
	public Integer code;
	
	public BadRequestExceptionDto(BadRequestException ex) {
		this.message = ex.getMessage();
		this.code = ex.getCode();
	}
}
