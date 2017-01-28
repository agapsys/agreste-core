/*
 * Copyright 2016-2017 Agapsys Tecnologia Ltda-ME.
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

import com.agapsys.rcf.exceptions.RateLimitingException;
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

public class AbuseCheckFilter implements Filter {
    // CLASS SCOPE =============================================================
    private static final String SESSION_ATTR_LAST_CHECK  = "com.agaosys.agreste.lastCheck";
    private static final String SESSION_ATTR_ABUSE_COUNT = "com.agaosys.agreste.abuseCount";
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AgresteApplication app = (AgresteApplication) AgresteApplication.getRunningInstance();

        if (!app.isAbuseCheckEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null) {
            session = req.getSession(true);
            session.setAttribute(SESSION_ATTR_LAST_CHECK, null);
            session.setAttribute(SESSION_ATTR_ABUSE_COUNT, 0);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

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
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
    // =========================================================================
}
