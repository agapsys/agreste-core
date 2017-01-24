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

import com.agapsys.web.toolkit.AbstractApplication;
import com.agapsys.web.toolkit.WebModule;
import com.agapsys.web.toolkit.utils.Settings;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;

/**
 * Cross-Origin resource sharing module
 */
public class CorsModule extends WebModule {
    // CLASS SCOPE =============================================================
    public static String SETTINGS_SECTION_NAME = CorsModule.class.getName();

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

    private void __reset() {
        allowedOrigins = null;
        allowedMethods = null;
        allowedHeaders = null;
    }

    public CorsModule() {
        __reset();
    }

    @Override
    protected final String getSettingsSection() {
        return SETTINGS_SECTION_NAME;
    }

    @Override
    protected Settings getDefaultSettings() {
        Settings defaults = super.getDefaultSettings();

        if (defaults == null)
            defaults = new Settings();

        defaults.setProperty(KEY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS);
        defaults.setProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
        defaults.setProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);

        return defaults;
    }

    @Override
    protected void onInit(AbstractApplication webApp) {
        super.onInit(webApp);

        __reset();

        Settings props = getSettings();

        String val = props.getProperty(KEY_ALLOWED_ORIGINS, null);

        if (val != null) {
            allowedOrigins = val.split(Pattern.quote(ORIGIN_DELIMITER));

            for (int i = 0; i < allowedOrigins.length; i++)
                allowedOrigins[i] = allowedOrigins[i].trim();

            allowedMethods = props.getMandatoryProperty(KEY_ALLOWED_METHODS);
            allowedHeaders = props.getMandatoryProperty(KEY_ALLOWED_HEADERS);
        } else {
            allowedOrigins = null;
            allowedMethods = null;
            allowedHeaders = null;
        }
    }

    private String[] __getAllowedOrigins() {
        return allowedOrigins;
    }

    private String __getAllowedMethods() {
        return allowedMethods;
    }

    private String __getAllowedHeaders() {
        return allowedHeaders;
    }

    public final void putCorsHeaders(HttpServletResponse resp) {
        if (!isActive()) throw new RuntimeException("Module is not running");

        String mAllowedMethods   = __getAllowedMethods();
        String mAllowedHeaders   = __getAllowedHeaders();
        String[] mAllowedOrigins = __getAllowedOrigins();

        if (mAllowedMethods != null && !mAllowedMethods.isEmpty())
            resp.setHeader(HEADER_ALLOWED_METHODS, __getAllowedMethods());

        if (mAllowedHeaders != null && !mAllowedHeaders.isEmpty())
            resp.setHeader(HEADER_ALLOWED_HEADERS, __getAllowedHeaders());

        if (mAllowedOrigins != null) {
            for (String allowedOrigin : __getAllowedOrigins())
                resp.addHeader(HEADER_ALLOWED_ORIGINS, allowedOrigin);
        }
    }
    // =========================================================================
}
