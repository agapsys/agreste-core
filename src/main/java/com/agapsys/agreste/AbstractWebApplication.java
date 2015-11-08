/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste;

import com.agapsys.utils.console.Console;
import com.agapsys.utils.console.ConsoleColor;
import com.agapsys.utils.console.FormatEscapeBuilder;
import com.agapsys.web.toolkit.utils.DateUtils;
import java.util.logging.Level;

public abstract class AbstractWebApplication extends com.agapsys.web.toolkit.AbstractWebApplication {
	// CLASS SCOPE =============================================================
	public static AbstractWebApplication getInstance() {
		return (AbstractWebApplication) com.agapsys.web.toolkit.AbstractWebApplication.getRunningInstance();
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
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
		String logTypeStr = feb.escape(String.format("[%s]", logType.name()));		
		
		Console.printlnf("%s %s %s", DateUtils.getLocalTimestamp(), logTypeStr, String.format(message, args));
	}
	
	@Override
	protected void beforeApplicationStart() {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(getHibernateLogLevel());
		
		super.beforeApplicationStart();
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
