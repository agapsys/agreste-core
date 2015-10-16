/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.agrest.modules.CorsModule;
import com.agapsys.agrest.modules.EmailConfirmSenderModule;
import com.agapsys.agrest.modules.PasswordResetSenderModule;
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
		
		Class emailConfirmSenderModuleClass  = getEmailConfirmSenderModuleClass();
		Class passwordResetSenderModuleClass = getPasswordResetSenderModuleClass();
		Class corsModuleClass                = getCorsModuleClass();
		
		if (emailConfirmSenderModuleClass != null)
			registerModule(emailConfirmSenderModuleClass);
		
		if (passwordResetSenderModuleClass != null)
			registerModule(passwordResetSenderModuleClass);
		
		if (corsModuleClass != null)
			registerModule(corsModuleClass);
	}
	
	/**
	 * Return hibernate logging level.
	 * @return hibernate logging level. Default implementation disables log.
	 */
	protected Level getHibernateLogLevel() {
		return Level.OFF;
	}
	
	protected Class<? extends EmailConfirmSenderModule> getEmailConfirmSenderModuleClass() {
		return EmailConfirmSenderModule.class;
	}
	
	protected Class<? extends PasswordResetSenderModule> getPasswordResetSenderModuleClass() {
		return PasswordResetSenderModule.class;
	}
	
	protected Class<? extends CorsModule> getCorsModuleClass() {
		return CorsModule.class;
	}
	
	/**
	 * Return The email confirm sender module used by this application
	 * @return The email confirm sender module used by this application. If there is no such module, returns null. 
	 */
	public final EmailConfirmSenderModule getEmailConfirmSenderModule() {
		return (EmailConfirmSenderModule) getModuleInstance(getEmailConfirmSenderModuleClass());
	}

	/**
	 * Return The password reset sender module used by this application 
	 * @return The password reset sender module used by this application. If there is no such module, returns null.
	 */
	public final PasswordResetSenderModule getPasswordResetSenderModule() {
		return (PasswordResetSenderModule) getModuleInstance(getPasswordResetSenderModuleClass());
	}
	
	/**
	 * Return The CORS module used by this application
	 * @return The CORS module used by this application. If there is no such module, returns null.
	 */
	public final CorsModule getCorsModule() {
		return (CorsModule) getModuleInstance(getCorsModuleClass());
	}
}
