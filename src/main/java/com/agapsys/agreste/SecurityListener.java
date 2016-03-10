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
	private final WebSecurityManager securityManager;
	private final String[] securedClasses;
	
	public SecurityListener() {
		this(null);
	}
	
	public SecurityListener(WebSecurityManager securityManager, String...securedClasses) {
		this.securityManager = securityManager;
		this.securedClasses = securedClasses;
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebSecurityManager webSecurityManager;
		
		if (securityManager == null) {
			String webSecurityManagerClassName = sce.getServletContext().getInitParameter(WEB_SECURITY_MANAGER_CLASS_PARAM);

			try {
				Class<? extends WebSecurityManager> webSecurityManagerClass = (Class<? extends WebSecurityManager>) Class.forName(webSecurityManagerClassName);
				webSecurityManager = webSecurityManagerClass.newInstance();
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException(String.format("Web security manager class not found (check your web.xml): %s", webSecurityManagerClassName), ex);
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new RuntimeException(String.format("Could not instantiate %s", webSecurityManagerClassName), ex);
			}
		} else {
			webSecurityManager = securityManager;
		}
		
		
		System.out.print(String.format("Initializing Web Security Framework (security manager: %s)...", webSecurityManager.getClass().getName()));
		
		if (securedClasses.length == 0) {
			WebSecurity.init(this.getClass().getClassLoader(), webSecurityManager);
		} else {
			WebSecurity.init(this.getClass().getClassLoader(), webSecurityManager, securedClasses);
		}
		
		System.out.print(" Done!\n");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
	// =========================================================================
}
