/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.servlets;

import com.agapsys.agreste.AbstractAgrestApplication;
import com.agapsys.agreste.services.RateLimitingException;
import com.agapsys.web.toolkit.AbstractWebApplication;
import java.io.IOException;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AbuseCheckFilter implements Filter{

	// CLASS SCOPE =============================================================
	private static final String SESSION_ATTR_LAST_CHECK  = "lastCheck";
	private static final String SESSION_ATTR_ABUSE_COUNT = "abuseCount";
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		
		if (session == null) {
			session = req.getSession(true);
			session.setAttribute(SESSION_ATTR_LAST_CHECK, null);
			session.setAttribute(SESSION_ATTR_ABUSE_COUNT, 0);
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			AbstractAgrestApplication app = (AbstractAgrestApplication) AbstractWebApplication.getRunningInstance();
			
			long appAbuseInterval = app.getAbuseInterval();
			int appAbuseCountLimit = app.getAbuseCountLimit();
			
			Date lastCheck = (Date) session.getAttribute(SESSION_ATTR_LAST_CHECK);
			int abuseCount = (int) session.getAttribute(SESSION_ATTR_ABUSE_COUNT);
			
			Date now = new Date();
			
			session.setAttribute(SESSION_ATTR_LAST_CHECK, now);
			boolean abuseDetected = false;
			
			if (lastCheck != null) {
				long ellapsed = now.getTime() - lastCheck.getTime();
				
				if (ellapsed < appAbuseInterval) {
					abuseCount++;
					abuseDetected = true;
				} else {
					abuseCount--;
					if (abuseCount < 0) abuseCount = 0;
				}
				
				session.setAttribute(SESSION_ATTR_ABUSE_COUNT, abuseCount);
		
				if (abuseDetected && abuseCount > appAbuseCountLimit) {
					resp.setStatus(RateLimitingException.CODE);
					resp.getWriter().print("Too many requests");
				}
			}
		}
	}

	@Override
	public void destroy() {}
	// =========================================================================
}
