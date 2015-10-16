/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.modules;

import com.agapsys.web.toolkit.AbstractApplication;

public class EmailConfirmSenderModule extends AbstractCodeSenderModule {
	// CLASS SCOPE =============================================================
	// SETTINGS-----------------------------------------------------------------
	public static final String KEY_SUBJECT = "agapsys.webtoolkit.emailConfirmSender.subject";
	public static final String KEY_MESSAGE = "agapsys.webtoolkit.emailConfirmSender.text";
	// -------------------------------------------------------------------------

	// DEFAULT VALUES ----------------------------------------------------------	
	private static final String DEFAULT_SUBJECT = String.format("[%s] Email confirm", APP_NAME_TOKEN);
	private static final String DEFAULT_MESSAGE = String.format("<p>Please confirm you email address clicking on the following link: </p><p><a href=\"http://localhost:8080/app?email=%s&code=%s\">http://localhost:8080/app?email=%s&code=%s</a></p>", EMAIL_TOKEN, CODE_TOKEN, EMAIL_TOKEN, CODE_TOKEN);
	// -------------------------------------------------------------------------
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public EmailConfirmSenderModule(AbstractApplication application) {
		super(application);
	}
	
	@Override
	protected String getDefaultSubject() {
		return DEFAULT_SUBJECT;
	}
	
	@Override
	protected String getDefaultMessage() {
		return DEFAULT_MESSAGE;
	}

	@Override
	protected String getPropertiesSubjectKey() {
		return KEY_SUBJECT;
	}

	@Override
	protected String getPropertiesMessageKey() {
		return KEY_MESSAGE;
	}
	// =========================================================================
}
