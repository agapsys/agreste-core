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

package com.agapsys.agreste.modules;

import com.agapsys.web.toolkit.AbstractModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import java.util.Properties;
import java.util.regex.Pattern;
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
	
	private static final String ORIGIN_DELIMITER = ",";
	
	private static final String HEADER_ALLOWED_ORIGINS = "Access-Control-Allow-Origin";
	private static final String HEADER_ALLOWED_METHODS = "Access-Control-Allow-Methods"; 
	private static final String HEADER_ALLOWED_HEADERS = "Access-Control-Allow-Headers"; 
	
	private static final String DEFAULT_ALLOWED_ORIGINS = "";
	private static final String DEFAULT_ALLOWED_METHODS = "";
	private static final String DEFAULT_ALLOWED_HEADERS = "";
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private String[] allowedOrigins;
	private String allowedMethods;
	private String allowedHeaders;
	
	@Override
	public Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		
		defaultProperties.setProperty(KEY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS);
		defaultProperties.setProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
		defaultProperties.setProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);
		
		return defaultProperties;
	}
	
	@Override
	protected void onStart(AbstractWebApplication webApp) {
		Properties appProperties = webApp.getProperties();
		String val = appProperties.getProperty(KEY_ALLOWED_ORIGINS);
		
		if (val == null)
			val = "";
		
		val = val.trim();
		
		if (!val.isEmpty()) {
			allowedOrigins = val.split(Pattern.quote(ORIGIN_DELIMITER));
			for (int i = 0; i < allowedOrigins.length; i++)
				allowedOrigins[i] = allowedOrigins[i].trim();
		}
		
		allowedMethods = appProperties.getProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
		allowedHeaders = appProperties.getProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);
	}
	
	@Override
	protected void onStop() {
		allowedOrigins = null;
		allowedMethods = null;
		allowedHeaders = null;
	}

	public String[] getAllowedOrigins() {
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
		
		String _allowedMethod = getAllowedMethods();
		String _allowedHeaders = getAllowedHeaders();
		
		if (_allowedMethod != null && !_allowedMethod.trim().isEmpty())
			resp.setHeader(HEADER_ALLOWED_METHODS, getAllowedMethods());
		
		if (_allowedHeaders != null && !_allowedHeaders.trim().isEmpty())
			resp.setHeader(HEADER_ALLOWED_HEADERS, getAllowedHeaders());
		
		for (String allowedOrigin : getAllowedOrigins())
			resp.addHeader(HEADER_ALLOWED_ORIGINS, allowedOrigin);
	}
	// =========================================================================
}
