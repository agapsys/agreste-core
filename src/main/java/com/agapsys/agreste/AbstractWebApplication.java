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

import com.agapsys.utils.console.printer.ConsoleColor;
import com.agapsys.utils.console.printer.ConsolePrinter;
import com.agapsys.utils.console.printer.FormatEscapeBuilder;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.modules.PersistenceModule;
import com.agapsys.web.toolkit.utils.DateUtils;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

public abstract class AbstractWebApplication extends com.agapsys.web.toolkit.AbstractWebApplication {
	// CLASS SCOPE =============================================================
	public static final String KEY_ABUSE_INTERVAL      = "com.agapsys.agreste.abuseInterval";
	public static final String KEY_ABUSE_COUNT_LIMIT   = "com.agapsys.agreste.abuseCountLimit";
	public static final String KEY_ABUSE_CHECK_ENABLED = "com.agapsys.agreste.abuseCheckEnabled";
	
	public static final boolean DEFAULT_ABUSE_CHECK_ENABLED = true;
	public static final long    DEFAULT_ABUSE_INTERVAL      = 2000; // 2 seconds
	public static final int     DEFAULT_ABUSE_COUNT_LIMIT   = 10;
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private long abuseInterval;
	private int abuseCountLimit;
	private boolean abuseCheckEnabled;
	
	@Override
	public void log(LogType logType, String message, Object... args) {
		ConsoleColor fgColor;
		switch (logType) {
			case INFO:
				fgColor = ConsoleColor.GREEN;
				break;
				
			case WARNING:
				fgColor = ConsoleColor.YELLOW;
				break;
				
			case ERROR:
				fgColor = ConsoleColor.RED;
				break;
				
			default:
				throw new UnsupportedOperationException("Unsupported type: " + logType.name());
				
		}
		FormatEscapeBuilder feb = new FormatEscapeBuilder().setFgColor(fgColor);
		String logTypeStr = feb.toString(String.format("[%s]", logType.name()));		
		
		if (args.length > 0)
			message = String.format(message, args);
		
		ConsolePrinter.println("%s %s %s", DateUtils.getLocalTimestamp(), logTypeStr, message);
	}
	
	@Override
	protected void beforeApplicationStart() {
		TimeZone.setDefault(getDefaultTimeZone());		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(getHibernateLogLevel());
		
		super.beforeApplicationStart();
		registerModule(PersistenceModule.class);
	}
	
	protected TimeZone getDefaultTimeZone() {
		return TimeZone.getTimeZone("UTC");
	}

	@Override
	protected void afterApplicationStart() {
		super.afterApplicationStart();
		
		String errFormatStr = "Invalid value for %s: %s";
		
		Properties properties = getProperties();
		
		String abuseCheckEnabledStr  = properties.getProperty(KEY_ABUSE_CHECK_ENABLED, "" + DEFAULT_ABUSE_CHECK_ENABLED);
		String abuseCheckIntervalStr = properties.getProperty(KEY_ABUSE_INTERVAL,      "" + DEFAULT_ABUSE_INTERVAL);
		String abuseLimitStr         = properties.getProperty(KEY_ABUSE_COUNT_LIMIT,   "" + DEFAULT_ABUSE_COUNT_LIMIT);
		
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
	}

	@Override
	protected Properties getDefaultProperties() {
		Properties properties = super.getDefaultProperties();
		if (properties == null)
			properties = new Properties();
		
		properties.setProperty(KEY_ABUSE_CHECK_ENABLED, "" + DEFAULT_ABUSE_CHECK_ENABLED);
		properties.setProperty(KEY_ABUSE_INTERVAL,      "" + DEFAULT_ABUSE_INTERVAL);
		properties.setProperty(KEY_ABUSE_COUNT_LIMIT,   "" + DEFAULT_ABUSE_COUNT_LIMIT);
		
		return properties;
	}

	public long getAbuseInterval() {
		return abuseInterval;
	}
	
	public int getAbuseCountLimit() {
		return abuseCountLimit;
	}
	
	public boolean isAbuseCheckEnabled() {
		return abuseCheckEnabled;
	}
	
	/**
	 * Return hibernate logging level.
	 * @return hibernate logging level. Default implementation disables log.
	 */
	protected Level getHibernateLogLevel() {
		return Level.OFF;
	}
	// =========================================================================
}
