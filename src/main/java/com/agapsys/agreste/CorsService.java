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
import com.agapsys.web.toolkit.Service;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;

/**
 * Cross-Origin resource sharing module
 */
public class CorsService extends Service {
    // CLASS SCOPE =============================================================
    private static String PROPERTY_PREFIX = CorsService.class.getName();

    public static final String KEY_ALLOWED_ORIGINS = PROPERTY_PREFIX + ".allowedOrigins";
    public static final String KEY_ALLOWED_METHODS = PROPERTY_PREFIX + ".allowedMethods";
    public static final String KEY_ALLOWED_HEADERS = PROPERTY_PREFIX + ".allowedHeaders";

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

    public CorsService() {
        __reset();
    }

    @Override
    protected void onStart() {
        super.onStart();

        synchronized(this) {
            __reset();

            AbstractApplication app = getApplication();
            String val = app.getProperty(KEY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS);

            if (val != null) {
                allowedOrigins = val.split(Pattern.quote(ORIGIN_DELIMITER));

                for (int i = 0; i < allowedOrigins.length; i++)
                    allowedOrigins[i] = allowedOrigins[i].trim();

                allowedMethods = app.getProperty(KEY_ALLOWED_METHODS, DEFAULT_ALLOWED_METHODS);
                allowedHeaders = app.getProperty(KEY_ALLOWED_HEADERS, DEFAULT_ALLOWED_HEADERS);
            } else {
                allowedOrigins = null;
                allowedMethods = null;
                allowedHeaders = null;
            }
        }
    }

    public String[] getAllowedOrigins() {
        synchronized(this) {
            return allowedOrigins;
        }
    }

    public String getAllowedMethods() {
        synchronized(this) {
            return allowedMethods;
        }
    }

    public String getAllowedHeaders() {
        synchronized(this) {
            return allowedHeaders;
        }
    }

    public void putCorsHeaders(HttpServletResponse resp) {
        synchronized(this) {
            if (!isRunning())
                throw new RuntimeException("Module is not running");

            String mAllowedMethods   = getAllowedMethods();
            String mAllowedHeaders   = getAllowedHeaders();
            String[] mAllowedOrigins = getAllowedOrigins();

            if (mAllowedMethods != null && !mAllowedMethods.isEmpty())
                resp.setHeader(HEADER_ALLOWED_METHODS, getAllowedMethods());

            if (mAllowedHeaders != null && !mAllowedHeaders.isEmpty())
                resp.setHeader(HEADER_ALLOWED_HEADERS, getAllowedHeaders());

            if (mAllowedOrigins != null) {
                for (String allowedOrigin : getAllowedOrigins())
                    resp.addHeader(HEADER_ALLOWED_ORIGINS, allowedOrigin);
            }
        }
    }
    // =========================================================================
}
