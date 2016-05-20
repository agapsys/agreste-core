/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agapsys.agreste;

import com.agapsys.rcf.User;
import com.agapsys.rcf.exceptions.ForbiddenException;
import com.agapsys.web.toolkit.utils.StringUtils;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Represents a HTTP exchange with Cross-Site Request Forgery protection)
 * @author Leandro Oliveira (leandro@agapsys)
 */
public class CsrfHttpExchange extends HttpExchange {
	// STATIC SCOPE ============================================================
	public static final String SESSION_ATTR_CSRF_TOKEN = CsrfHttpExchange.class.getName() + ".csrfToken";
	private static final int CSRF_TOKEN_LENGTH = 128;
	
	/** Name of the header used to send/retrieve a CSRF token. */
	public static final String CSRF_HEADER  = "X-Csrf-Token";
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	public CsrfHttpExchange(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	@Override
	public User getCurrentUser() {
		User currentUser = super.getCurrentUser();
		
		if (currentUser == null)
			return null;
		
		HttpSession session = getRequest().getSession(false);

		if (session == null)
			throw new ForbiddenException("Missing CSRF header");
		
		String sessionToken = (String) session.getAttribute(SESSION_ATTR_CSRF_TOKEN);
		String requestToken = getRequest().getHeader(CSRF_HEADER);
			
		if (!Objects.equals(sessionToken, requestToken))
			throw new ForbiddenException("Invalid CSRF header");
		
		return currentUser;
	}

	@Override
	public void setCurrentUser(User user) {
		super.setCurrentUser(user);
		if (user != null) {
			HttpSession session = getRequest().getSession(true);
			String token = StringUtils.getRandomString(CSRF_TOKEN_LENGTH);
			session.setAttribute(SESSION_ATTR_CSRF_TOKEN, token);
			getResponse().setHeader(CSRF_HEADER, token);
		} else {
			HttpSession session = getRequest().getSession(false);
			if (session != null) session.removeAttribute(SESSION_ATTR_CSRF_TOKEN);
		}
	}
	// =========================================================================
}
