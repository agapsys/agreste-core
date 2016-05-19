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
package com.agapsys.agreste.app;

import com.agapsys.agreste.CsrfHttpExchange;
import com.agapsys.agreste.app.controllers.UserController;
import com.agapsys.agreste.app.entities.User.UserDto;
import com.agapsys.agreste.test.ServletContainerBuilder;
import com.agapsys.agreste.test.TestUtils;
import com.agapsys.agreste.test.TestUtils.RestEndpoint;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.http.utils.Pair;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.exceptions.ForbiddenException;
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
		System.out.println(String.format("=== %s ===", AppTest.class.getSimpleName()));
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println();
	}
	
	public static class LoginInfo extends Pair<HttpClient, StringResponse> {
		
		public LoginInfo(HttpClient first, StringResponse second) {
			super(first, second);
		}
		
		public HttpClient getClient() {
			return getFirst();
		}
		
		public StringResponse getResponse() {
			return getSecond();
		}
		
	}
	
	public static LoginInfo doLogin(ServletContainer sc, String username, String password) {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/user/login");
		HttpClient client = new HttpClient();
		StringResponse resp = sc.doRequest(client, endpoint.getRequest("username=%s&password=%s", username, password));
		return new LoginInfo(client, resp);
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final TestUtils testUtils = TestUtils.getInstance();
	
	private ServletContainer sc;
	
	@Before
	public void before() {
		System.out.println("Starting application...");

		sc = new ServletContainerBuilder(TestApplication.class)
			.registerController(UserController.class)
			.build();
		
		sc.startServer();
	}
	
	@After
	public void after() {
		System.out.println("\nShutting the application down...");

		sc.stopServer();
	}
	
	@Test
	public void testLogin() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/user/login");
		testUtils.println(endpoint.toString());
		
		HttpClient client = new HttpClient();
		StringResponse resp;

		// Invalid username...
		resp = sc.doRequest(client, endpoint.getRequest("username=%s&password=%s", "invalid_user", "invalid_password"));
		testUtils.assertErrorStatus(ForbiddenException.CODE, "Invalid credentials", resp);
		
		// Invalid password...
		resp = sc.doRequest(client, endpoint.getRequest("username=%s&password=%s", "username", "invalid-password"));
		testUtils.assertStatus(ForbiddenException.CODE, resp);
		
		// Valid credentials...
		resp = sc.doRequest(client, endpoint.getRequest("username=%s&password=%s", "username", "password"));
		testUtils.assertStatus(200, resp);
	}
	
	@Test
	public void testSecuredAction() {
		RestEndpoint endpoint = new RestEndpoint(HttpMethod.GET, "/user/me");
		testUtils.println(endpoint.toString());
		
		StringResponse resp;

		// Unlogged access...	
		resp = sc.doRequest(endpoint.getRequest());
		testUtils.assertStatus(401, resp);
		
		// Logged access (without CSRF)...
		LoginInfo loginInfo = doLogin(sc, "username", "password");
		resp = sc.doRequest(loginInfo.getClient(), endpoint.getRequest());
		testUtils.assertErrorStatus(403, "Invalid CSRF header", resp);
		
		// Logged access (with CSRF)...
		loginInfo.getClient().addDefaultHeader(CsrfHttpExchange.CSRF_HEADER, loginInfo.getResponse().getFirstHeader(CsrfHttpExchange.CSRF_HEADER).getValue());
		resp = sc.doRequest(loginInfo.getClient(), endpoint.getRequest());
		testUtils.assertStatus(200, resp);
		UserDto dto = testUtils.readObjectResponse(UserDto.class, resp);
		Assert.assertEquals("username", dto.username);
	}
	// =========================================================================
}
