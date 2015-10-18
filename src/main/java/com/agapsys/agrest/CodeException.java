/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agrest;

/**
 * Represents an exception represented by a code.
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public interface CodeException {
	/** Return exception code. */
	public Integer getCode();
	
	/** Return exception message. */
	public String getMessage();
}
