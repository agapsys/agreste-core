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
package com.agapsys.agreste.test.app;

import com.agapsys.agreste.ServletContainerBuilder;
import com.agapsys.agreste.TestUtils;
import com.agapsys.agreste.TestUtils.RestEndpoint;
import com.agapsys.agreste.dto.AbstractDtoTest;
import com.agapsys.agreste.exceptions.ForbiddenException;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.security.web.SessionCsrfSecurityManager;
import com.agapsys.security.web.WebSecurityManager;
import com.agapsys.sevlet.container.ServletContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class AppTest {
	// STATIC SCOPE ============================================================
	@BeforeClass
	public static void beforeClass() {
		System.out.println(String.format("=== %s ===", AbstractDtoTest.class.getSimpleName()));
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println();
	}
	
	private static final WebSecurityManager DEFAULT_SECURITY_MANAGER = new SessionCsrfSecurityManager();
	
	public static HttpClient doLogin(ServletContainer sc, String username, String password) {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/controller/login", "username", "password");
		HttpClient client = new HttpClient();
		StringResponse resp = sc.doRequest(client, endpoint.getRequest(username, password));
		client.addDefaultHeaders(resp.getFirstHeader(SessionCsrfSecurityManager.CSRF_HEADER));
		return client;
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final TestUtils testUtils = TestUtils.getInstance();
	
	private ServletContainer sc;
	
	@Before
	public void before() {
		sc = new ServletContainerBuilder(
				MyApplication.class, 
				DEFAULT_SECURITY_MANAGER, 
				"com.agapsys.agreste.test.app.MyController", 
				"com.agapsys.agreste.test.app.MyService"
			).registerController(MyController.class)
			.build();
		sc.startServer();
	}
	
	@After
	public void after() {
		sc.stopServer();
	}
	
	@Test
	public void testLogin() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/controller/login", "username", "password");
		testUtils.println(endpoint.toString());
		
		HttpClient client = new HttpClient();
		StringResponse resp;

		// Invalid username...
		resp = sc.doRequest(client, endpoint.getRequest("invalid_user", "invalid_password"));
		testUtils.assertErrorStatus(ForbiddenException.CODE, "Invalid credentials", resp);
		
		// Invalid password...
		resp = sc.doRequest(client, endpoint.getRequest("user1", "invalid-password"));
		testUtils.assertStatus(ForbiddenException.CODE, resp);
		
		// Valid credentials...
		resp = sc.doRequest(client, endpoint.getRequest("user1", "password1"));
		testUtils.assertStatus(200, resp);
	}
	
	@Test
	public void testPublicGet() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/controller/publicGet");
		testUtils.println(endpoint.toString());
		
		StringResponse resp;

		resp = sc.doRequest(endpoint.getRequest());
		testUtils.assertStatus(200, resp);
		Assert.assertEquals("OK", resp.getContentString());
	}
	
	@Test
	public void testSecuredGet() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/controller/securedGet");
		testUtils.println(endpoint.toString());
		
		StringResponse resp;

		// Unlogged access...	
		resp = sc.doRequest(endpoint.getRequest());
		testUtils.assertStatus(401, resp);
		
		// Logged access...
		HttpClient client = doLogin(sc, "user1", "password1");
		resp = sc.doRequest(client, endpoint.getRequest());
		testUtils.assertStatus(200, resp);
		Assert.assertEquals("OK", resp.getContentString());
		
	}
	
	@Test
	public void testImplicitSecuredGet() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/controller/implicitSecuredGet");
		testUtils.println(endpoint.toString());
		
		StringResponse resp;

		// Unlogged access...	
		resp = sc.doRequest(endpoint.getRequest());
		testUtils.assertStatus(401, resp);
		
		HttpClient client;
		
		// Unpriviledged access...
		client = doLogin(sc, "user1", "password1"); // <-- "user1" does not have the required roles
		resp = sc.doRequest(client, endpoint.getRequest());
		testUtils.assertStatus(403, resp);
		
		// Valid access...
		client = doLogin(sc, "user2", "password2"); // <-- "user2" have the required roles
		resp = sc.doRequest(client, endpoint.getRequest());
		testUtils.assertStatus(200, resp);
		Assert.assertEquals("OK", resp.getContentString());
		
		// Valid access with group roles...
		client = doLogin(sc, "user3", "password3"); // <-- "user2" have the required roles
		resp = sc.doRequest(client, endpoint.getRequest());
		testUtils.assertStatus(200, resp);
		Assert.assertEquals("OK", resp.getContentString());
	}
	// =========================================================================
}
