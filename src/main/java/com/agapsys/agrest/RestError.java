/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agrest;

/**
 * Represents an error in REST application
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public interface RestError {
	/** @return error code. */
	public Integer getCode();
	
	/** @return error message. */
	public String getMessage();
}
