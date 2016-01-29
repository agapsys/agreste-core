/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste;

import com.agapsys.agreste.model.AbstractUser;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class WebSecurity extends com.agapsys.security.web.WebSecurity {
	// STATIC SCOPE ============================================================
	public static AbstractUser getCurrentUser() {
		return (AbstractUser) com.agapsys.security.web.WebSecurity.getCurrentUser();
	}
	
	public static void setCurrentUser(AbstractUser user) {
		com.agapsys.security.web.WebSecurity.setCurrentUser(user);
	}
	// =========================================================================
}
