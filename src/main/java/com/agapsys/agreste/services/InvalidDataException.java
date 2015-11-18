/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.services;

import com.agapsys.web.toolkit.BadRequestException;

public class InvalidDataException extends ServiceException {
	
	// CLASS SCOPE =============================================================
	public static final int CODE = BadRequestException.CODE;
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public InvalidDataException(String message) {
		super(CODE, message);
	}
	// =========================================================================

}
