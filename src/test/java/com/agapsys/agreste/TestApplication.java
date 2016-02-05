/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste;

import com.agapsys.agreste.test.MockedWebApplication;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestApplication extends MockedWebApplication implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		stop();
	}
}
