/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste;

import com.agapsys.agreste.exceptions.ClientException;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class ClientExceptionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (ClientException ex) {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.setStatus(ex.getCode());
			resp.getWriter().print(ex.getMessage());
		}
	}

	@Override
	public void destroy() {}
}
