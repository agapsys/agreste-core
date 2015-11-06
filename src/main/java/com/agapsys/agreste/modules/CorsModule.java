/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.modules;

import com.agapsys.web.toolkit.AbstractModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import java.util.Properties;
import javax.servlet.http.HttpServletResponse;

/**
 * Cross-Origin resource sharing module
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class CorsModule extends AbstractModule {
	// CLASS SCOPE =============================================================
	public static final String KEY_ALLOWED_ORIGINS = "agapsys.agrest.cors.allowedOrigins";
	public static final String KEY_ALLOWED_METHODS = "agapsys.agrest.cors.allowedMethods";
	public static final String KEY_ALLOWED_HEADERS = "agapsys.agrest.cors.allowedHeaders";
	
	private static final String HEADER_ALLOWED_ORIGINS = "Access-Control-Allow-Origin";
	private static final String HEADER_ALLOWED_METHODS = "Access-Control-Allow-Methods"; 
	private static final String HEADER_ALLOWED_HEADERS = "Access-Control-Allow-Headers"; 
	
	private static final String DEFAULT_ALLOWED_ORIGINS = "";
	private static final String DEFAULT_ALLOWED_METHODS = "";
	private static final String DEFAULT_ALLOWED_HEADERS = "";
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private String allowedOrigins;
	private String allowedMethods;
	private String allowedHeaders;
	
	protected String getDefaultAllowedOrigin() {
		return DEFAULT_ALLOWED_ORIGINS;
	}
	
	protected String getDefaultAllowedMethods() {
		return DEFAULT_ALLOWED_METHODS;
	}
	
	protected String getDefaultAllowedHeaders() {
		return DEFAULT_ALLOWED_HEADERS;
	}
	
	@Override
	public Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		
		String defaultAllowedOrigins = getDefaultAllowedOrigin();
		if (defaultAllowedOrigins == null)
			defaultAllowedOrigins = DEFAULT_ALLOWED_ORIGINS;
		
		String defaultAllowedMethods = getDefaultAllowedMethods();
		if (defaultAllowedMethods == null)
			defaultAllowedMethods = DEFAULT_ALLOWED_METHODS;
		
		String defaultAllowedHeaders = getDefaultAllowedHeaders();
		if (defaultAllowedHeaders == null)
			defaultAllowedHeaders = DEFAULT_ALLOWED_HEADERS;
		
		defaultProperties.setProperty(KEY_ALLOWED_ORIGINS, defaultAllowedOrigins.trim());
		defaultProperties.setProperty(KEY_ALLOWED_METHODS, defaultAllowedMethods.trim());
		defaultProperties.setProperty(KEY_ALLOWED_HEADERS, defaultAllowedHeaders.trim());
		
		return defaultProperties;
	}
	
	@Override
	protected void onStart(AbstractWebApplication webApp) {
		Properties appProperties = webApp.getProperties();
		allowedOrigins = appProperties.getProperty(KEY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS);
		allowedMethods = appProperties.getProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
		allowedHeaders = appProperties.getProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);
	}
	
	@Override
	protected void onStop() {
		allowedOrigins = null;
		allowedMethods = null;
		allowedHeaders = null;
	}

	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public String getAllowedMethods() {
		return allowedMethods;
	}

	public String getAllowedHeaders() {
		return allowedHeaders;
	}

	public void putCorsHeaders(HttpServletResponse resp) {
		if (!isRunning()) throw new RuntimeException("Module is not running");
		
		if (allowedOrigins != null && !allowedOrigins.trim().isEmpty())
			resp.setHeader(HEADER_ALLOWED_ORIGINS, allowedOrigins);
		
		if (allowedMethods != null && !allowedMethods.trim().isEmpty())
			resp.setHeader(HEADER_ALLOWED_METHODS, allowedMethods);
		
		if (allowedHeaders != null && !allowedHeaders.trim().isEmpty())
			resp.setHeader(HEADER_ALLOWED_HEADERS, allowedHeaders);
	}
	// =========================================================================
}
