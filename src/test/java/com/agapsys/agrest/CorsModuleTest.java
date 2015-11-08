/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.agreste.modules.CorsModule;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpHeader;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.sevlet.test.ApplicationContext;
import com.agapsys.sevlet.test.ServletContainer;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.WebAction;
import com.agapsys.web.toolkit.AbstractWebApplication;
import java.util.Properties;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CorsModuleTest {
	// CLASS SCOPE =============================================================
	@WebListener
	public static class TestApplication extends TestWebApplication {
		public static final String VAL_ALLOWED_HEADERS = "testHeaders";
		public static final String VAL_ALLOWED_ORIGINS = "testOrigins";
		public static final String VAL_ALLOWED_METHODS = "testMethods";
		
		@Override
		protected Properties getDefaultProperties() {
			Properties props = new Properties();
			props.setProperty(CorsModule.KEY_ALLOWED_HEADERS, VAL_ALLOWED_HEADERS);
			props.setProperty(CorsModule.KEY_ALLOWED_ORIGINS, VAL_ALLOWED_ORIGINS);
			props.setProperty(CorsModule.KEY_ALLOWED_METHODS, VAL_ALLOWED_METHODS);
			return props;
		}

		@Override
		protected void beforeApplicationStart() {
			super.beforeApplicationStart();
			registerModule(CorsModule.class);
		}
	}
	
	@WebServlet("/*")
	public static class TestServlet extends ActionServlet {

		@Override
		public void beforeAction(HttpExchange exchange) {
			CorsModule corsModule = (CorsModule) AbstractWebApplication.getRunningInstance().getModule(CorsModule.class);
			corsModule.putCorsHeaders(exchange.getResponse());
		}
		
		@WebAction
		public void get(HttpExchange exchange) {}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private ServletContainer sc;
	
	@Before
	public void before() {
		ApplicationContext context = new ApplicationContext();
		context.registerEventListener(new TestApplication());
		context.registerServlet(TestServlet.class);
		
		sc = new ServletContainer();
		sc.registerContext(context);
		sc.startServer();
	}
	
	@After
	public void after() {
		sc.stopServer();
	}
	
	@Test
	public void testCorsHeaders() {
		StringResponse resp = sc.doRequest(new HttpGet("/get"));
		Assert.assertEquals(HttpServletResponse.SC_OK, resp.getStatusCode());
		
		HttpHeader allowOriginHeader = resp.getFirstHeader("Access-Control-Allow-Origin");
		HttpHeader allowMethodsHeader = resp.getFirstHeader("Access-Control-Allow-Methods");
		HttpHeader allowHeadersHeader = resp.getFirstHeader("Access-Control-Allow-Headers");
		
		Assert.assertNotNull(allowOriginHeader);
		Assert.assertNotNull(allowMethodsHeader);
		Assert.assertNotNull(allowHeadersHeader);
		
		Assert.assertEquals(TestApplication.VAL_ALLOWED_ORIGINS, allowOriginHeader.getValue());
		Assert.assertEquals(TestApplication.VAL_ALLOWED_METHODS, allowMethodsHeader.getValue());
		Assert.assertEquals(TestApplication.VAL_ALLOWED_HEADERS, allowHeadersHeader.getValue());
	}
	// =========================================================================
}
