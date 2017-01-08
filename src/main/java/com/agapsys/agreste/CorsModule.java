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

import com.agapsys.web.toolkit.AbstractApplication;
import com.agapsys.web.toolkit.ApplicationSettings;
import com.agapsys.web.toolkit.modules.WebModule;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;

/**
 * Cross-Origin resource sharing module
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class CorsModule extends WebModule {
    // CLASS SCOPE =============================================================
    private static final String[] EMPTY_STRING_ARRAY = new String[] {};

    public static String SETTINGS_GROUP_NAME = CorsModule.class.getName();

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

    private void reset() {
        allowedOrigins = null;
        allowedMethods = null;
        allowedHeaders = null;
    }

    public CorsModule() {
        reset();
    }

    @Override
    protected final String getSettingsGroupName() {
        return SETTINGS_GROUP_NAME;
    }

    @Override
    public Properties getDefaultProperties() {
        Properties defaultProperties = super.getDefaultProperties();

        defaultProperties.setProperty(KEY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS);
        defaultProperties.setProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
        defaultProperties.setProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);

        return defaultProperties;
    }

    @Override
    protected void onInit(AbstractApplication webApp) {
        super.onInit(webApp);

        reset();

        Properties props = getProperties();

        String val = ApplicationSettings.getProperty(props, KEY_ALLOWED_ORIGINS);

        if (val != null) {
            allowedOrigins = val.split(Pattern.quote(ORIGIN_DELIMITER));

            for (int i = 0; i < allowedOrigins.length; i++)
                allowedOrigins[i] = allowedOrigins[i].trim();
        } else {
            allowedOrigins = EMPTY_STRING_ARRAY;
        }

        allowedMethods = ApplicationSettings.getProperty(props, KEY_ALLOWED_METHODS);
        allowedHeaders = ApplicationSettings.getProperty(props, KEY_ALLOWED_HEADERS);
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

    public final void putCorsHeaders(HttpServletResponse resp) {
        if (!isActive()) throw new RuntimeException("Module is not running");

        String _allowedMethod = getAllowedMethods();
        String _allowedHeaders = getAllowedHeaders();
        String[] _allowedOrigins = getAllowedOrigins();

        if (_allowedMethod != null && !_allowedMethod.isEmpty())
            resp.setHeader(HEADER_ALLOWED_METHODS, getAllowedMethods());

        if (_allowedHeaders != null && !_allowedHeaders.isEmpty())
            resp.setHeader(HEADER_ALLOWED_HEADERS, getAllowedHeaders());

        if (_allowedOrigins != null) {
            for (String allowedOrigin : getAllowedOrigins())
                resp.addHeader(HEADER_ALLOWED_ORIGINS, allowedOrigin);
        }
    }
    // =========================================================================
}
