/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agrest.RestError;

public class RestErrorDto {
	public String message;
	public Integer code;
	
	public RestErrorDto(RestError error) {
		this.message = error.getMessage();
		this.code = error.getCode();
	}
}
