/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.services;

import com.agapsys.web.toolkit.ClientException;

public class RateLimitingException extends ClientException {
	
	// CLASS SCOPE =============================================================
	public static final int CODE = 429;
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public RateLimitingException(String message) {
		super(CODE, message);
	}
	// =========================================================================
	
}
