/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste;

import com.agapsys.security.web.WebSecurityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class SecurityListener implements ServletContextListener {
	// STATIC SCOPE ============================================================
	private static final String WEB_SECURITY_MANAGER_CLASS_PARAM = "com.agapsys.agreste.webSecurityManager";
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String webSecurityManagerClassName = sce.getServletContext().getInitParameter(WEB_SECURITY_MANAGER_CLASS_PARAM);
		WebSecurityManager webSecurityManager;
		
		try {
			Class<? extends WebSecurityManager> webSecurityManagerClass = (Class<? extends WebSecurityManager>) Class.forName(webSecurityManagerClassName);
			webSecurityManager = webSecurityManagerClass.newInstance();
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(String.format("Web security manager class not found (check your web.xml): %s", webSecurityManagerClassName), ex);
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(String.format("Could not instantiate %s", webSecurityManagerClassName), ex);
		}
		
		WebSecurity.init(this.getClass().getClassLoader(), webSecurityManager);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
	// =========================================================================
}
