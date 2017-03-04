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
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.modules.PersistenceModule;
import com.agapsys.web.toolkit.utils.Settings;
import java.util.TimeZone;

public abstract class AgresteApplication extends AbstractWebApplication {
    // CLASS SCOPE =============================================================
    public static final String KEY_ABUSE_INTERVAL      = "com.agapsys.agreste.abuseInterval";
    public static final String KEY_ABUSE_COUNT_LIMIT   = "com.agapsys.agreste.abuseCountLimit";
    public static final String KEY_ABUSE_CHECK_ENABLED = "com.agapsys.agreste.abuseCheckEnabled";

    public static final boolean DEFAULT_ABUSE_CHECK_ENABLED = true;
    public static final long    DEFAULT_ABUSE_INTERVAL      = 2000; // 2 seconds
    public static final int     DEFAULT_ABUSE_COUNT_LIMIT   = 10;
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    private long    abuseInterval;
    private int     abuseCountLimit;
    private boolean abuseCheckEnabled;

    /**
     * This method implements required functionality for AGRESTE. Use {@link AgresteApplication#beforeAgresteStart()} instead.
     */
    @Override
    protected final void beforeApplicationStart() {
        TimeZone.setDefault(getDefaultTimeZone());

        super.beforeApplicationStart();
        PersistenceModule persistenceModule = getPersistenceModule();
        if (persistenceModule != null) {
            registerModule(persistenceModule);
        }

        beforeAgresteStart();
    }
    
    protected PersistenceModule getPersistenceModule() {
        return new AgrestePersistenceModule();
    }

    /**
     * Called before application start.
     *
     * This method is equivalent to {@linkplain AbstractApplication#beforeApplicationStart()}.
     * Default implementation does nothing.
     */
    protected void beforeAgresteStart() {}

    /**
     * Return application default time zone.
     *
     * @return application default time zone. Default implementation returns UTC.
     */
    protected TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("UTC");
    }

    /**
     * This method implements required functionality for AGRESTE. Use {@linkplain AgresteApplication#getDefaults()} instead.
     */
    @Override
    protected final Settings getDefaultSettings() {
        Settings defaultSettings = super.getDefaultSettings();

        if (defaultSettings == null)
            defaultSettings = new Settings();

        defaultSettings.setProperty(KEY_ABUSE_CHECK_ENABLED, "" + DEFAULT_ABUSE_CHECK_ENABLED);
        defaultSettings.setProperty(KEY_ABUSE_INTERVAL,      "" + DEFAULT_ABUSE_INTERVAL);
        defaultSettings.setProperty(KEY_ABUSE_COUNT_LIMIT,   "" + DEFAULT_ABUSE_COUNT_LIMIT);

        Settings agresteDefaults = getDefaults();

        if (agresteDefaults != null) {
            defaultSettings.setProperties(agresteDefaults);
        }

        return defaultSettings;
    }

    /**
     * Returns default settings for this application.

     * @return default settings for this application. Default implementation returns null.
     */
    protected Settings getDefaults() {
        return null;
    }

    /**
     * This method implements required functionality for AGRESTE. Use {@link AgresteApplication#afterAgresteStart()} instead.
     */
    @Override
    protected final void afterApplicationStart() {
        super.afterApplicationStart();

        String errFormatStr = "Invalid value for %s: %s";

        Settings settings = getApplicationSettings().getSection(null);

        String abuseCheckEnabledStr  = settings.getMandatoryProperty(KEY_ABUSE_CHECK_ENABLED);
        String abuseCheckIntervalStr = settings.getMandatoryProperty(KEY_ABUSE_INTERVAL);
        String abuseLimitStr         = settings.getMandatoryProperty(KEY_ABUSE_COUNT_LIMIT);

        try {
            abuseCheckEnabled = Boolean.parseBoolean(abuseCheckEnabledStr);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(errFormatStr, KEY_ABUSE_CHECK_ENABLED, abuseCheckEnabledStr));
        }

        try {
            abuseInterval = Long.parseLong(abuseCheckIntervalStr);
        } catch (NumberFormatException ex) {
            throw new RuntimeException(String.format(errFormatStr, KEY_ABUSE_INTERVAL, abuseCheckIntervalStr));
        }

        try {
            abuseCountLimit = Integer.parseInt(abuseLimitStr);
        } catch (NumberFormatException ex) {
            throw new RuntimeException(String.format(errFormatStr, KEY_ABUSE_COUNT_LIMIT, abuseLimitStr));
        }

        if (abuseInterval < 1)
            throw new RuntimeException(String.format(errFormatStr, KEY_ABUSE_INTERVAL, abuseInterval));

        if (abuseCountLimit < 1)
            throw new RuntimeException(String.format(errFormatStr, KEY_ABUSE_COUNT_LIMIT, abuseCountLimit));

        afterAgresteStart();
    }

    /**
     * Called after application start.
     *
     * This method is equivalent to {@linkplain AbstractApplication#afterApplicationStart()}.
     * Default implementation does nothing.
     */
    protected void afterAgresteStart() {}

    @Override
    protected final void beforeApplicationStop() {
        super.beforeApplicationStop();
        beforeAgresteStop();
    }


    protected void beforeAgresteStop() {}

    @Override
    protected final void afterApplicationStop() {
        super.afterApplicationStop();
        afterAgresteStop();
    }

    protected void afterAgresteStop() {}


    protected long getAbuseInterval() {
        return abuseInterval;
    }

    protected int getAbuseCountLimit() {
        return abuseCountLimit;
    }

    protected boolean isAbuseCheckEnabled() {
        return abuseCheckEnabled;
    }

}
