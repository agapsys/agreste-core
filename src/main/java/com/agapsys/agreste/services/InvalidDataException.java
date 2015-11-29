/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.services;

import com.agapsys.web.toolkit.BadRequestException;
import com.agapsys.web.toolkit.ClientException;

public class InvalidDataException extends ClientException {
	
	// CLASS SCOPE =============================================================
	public static final int CODE = BadRequestException.CODE;
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public InvalidDataException(String message, Object...args) {
		super(CODE, message, args);
	}
	// =========================================================================

}
