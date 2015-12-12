/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.model;

import com.agapsys.web.action.dispatcher.SessionUser;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UnloggedUser implements SessionUser {
	// CLASS SCOPE =============================================================
	public static final String ROLE_UNLOGGED_USER = "com.agapsys.agreste.roles.UNLOGGED_USER";
	
	private static final List<String> ROLE_SET;
	
	static {
		List<String> roleList = new LinkedList<>();
		roleList.add(ROLE_UNLOGGED_USER);
		ROLE_SET = Collections.unmodifiableList(roleList);
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private Date lastRequest;
	
	public UnloggedUser() {
		this.lastRequest = new Date();
	}
	
	public Date getLastRequest() {
		return lastRequest;
	}
	
	private void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}
	
	public void registerRequest() {
		setLastRequest(new Date());
	}
	
	@Override
	public boolean isAdmin() {
		return false;
	}
	
	@Override
	public Collection<String> getRoles() {
		return ROLE_SET;
	}
	// =========================================================================
}
