/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.utils.console.ConsoleColor;
import com.agapsys.utils.console.FormatEscapeBuilder;
import java.util.logging.Level;

public abstract class AbstractWebApplication extends com.agapsys.web.toolkit.AbstractWebApplication {
	@Override
	public void log(LogType logType, String message, Object... args) {
		FormatEscapeBuilder feb = new FormatEscapeBuilder().setFgColor(ConsoleColor.YELLOW);
		message = feb.escape(String.format(message, args));
		super.log(logType, message);
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
}
