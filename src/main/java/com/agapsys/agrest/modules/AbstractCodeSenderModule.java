/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.modules;

import com.agapsys.mail.Message;
import com.agapsys.mail.MessageBuilder;
import com.agapsys.web.toolkit.AbstractModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import com.agapsys.web.toolkit.SmtpModule;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.internet.InternetAddress;

public abstract class AbstractCodeSenderModule extends AbstractModule {
	// CLASS SCOPE =============================================================
	// TOKENS ------------------------------------------------------------------
	public static final String CODE_TOKEN     = "${code}";
	public static final String APP_NAME_TOKEN = "${appName}";
	public static final String EMAIL_TOKEN    = "${email}";
	// -------------------------------------------------------------------------
	
	// DEFAULT VALUES ----------------------------------------------------------	
	private static final String DEFAULT_SUBJECT = String.format("[%s] Code sender", APP_NAME_TOKEN);
	private static final String DEFAULT_MESSAGE = String.format("<p>In order to complete your request, please click on the following link: </p><p><a href=\"http://localhost:8080/app?email=%s&code=%s\">http://localhost:8080/app?email=%s&code=%s</a></p>", EMAIL_TOKEN, CODE_TOKEN, EMAIL_TOKEN, CODE_TOKEN);
	// -------------------------------------------------------------------------
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private String subject;
	private String message;
		
	protected String getDefaultSubject() {
		return DEFAULT_SUBJECT;
	}
	
	protected String getDefaultMessage() {
		return DEFAULT_MESSAGE;
	}

	protected abstract String getPropertiesSubjectKey();
	protected abstract String getPropertiesMessageKey();
	
	@Override
	public Properties getDefaultSettings() {
		Properties properties = new Properties();
		
		String defaultSubject = getDefaultSubject();
		if (defaultSubject == null || defaultSubject.trim().isEmpty())
			throw new RuntimeException("Null/Empty default subject");
				
		String defaultMessage = getDefaultMessage();
		if (defaultMessage == null || defaultMessage.trim().isEmpty())
			throw new RuntimeException("Null/Empty default message");
		
		properties.setProperty(getPropertiesSubjectKey(), defaultSubject);
		properties.setProperty(getPropertiesMessageKey(), defaultMessage);
		
		return properties;
	}
	
	private SmtpModule getSmtpModule() {
		// Since SMTP module is a mandatory dependency there is no need to check if it is null
		return (SmtpModule) getApplication().getModule(SmtpModule.DEFAULT_MODULE_ID);
	}

	@Override
	protected void onStart(AbstractWebApplication webApp) {
		Properties appProperties = webApp.getProperties();
		
		subject = appProperties.getProperty(getPropertiesSubjectKey());
		if (subject == null || subject.trim().isEmpty())
			subject = getDefaultSubject();
		
		message = appProperties.getProperty(getPropertiesMessageKey());
		if (message == null || message.trim().isEmpty())
			message = getDefaultMessage();
	}

	@Override
	protected void onStop() {
		subject = null;
		message = null;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}
	
	private String replaceTokens(String str, String code, String email) {
		return str
			.replaceAll(Pattern.quote(APP_NAME_TOKEN), getApplication().getName())
			.replaceAll(Pattern.quote(CODE_TOKEN), code)
			.replaceAll(Pattern.quote(EMAIL_TOKEN), email);
	}
	
	protected Message getMessage(String code, InternetAddress recipient) {
		InternetAddress sender = getSmtpModule().getSender();
		
		String finalSubject = replaceTokens(getSubject(), code, recipient.getAddress());
		String finalMessage = replaceTokens(getMessage(), code, recipient.getAddress());
		
		return new MessageBuilder(sender, recipient).setMimeSubtype("html").setSubject(finalSubject).setText(finalMessage).build();
	}
	
	public void sendCode(String code, InternetAddress recipient) {
		if (code == null || code.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty code");
		
		if (recipient == null)
			throw new IllegalArgumentException("Null recipient");
		
		if (isRunning()) {
			getSmtpModule().sendMessage(getMessage(code, recipient));
		} else {
			throw new RuntimeException("Module is not running");
		}
	}
}
