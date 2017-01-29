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

import com.agapsys.agreste.app.controllers.UserController;
import com.agapsys.agreste.app.entities.User.UserDto;
import com.agapsys.agreste.test.AgresteContainer;
import com.agapsys.agreste.test.TestUtils;
import com.agapsys.agreste.test.TestUtils.Endpoint;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.http.utils.Pair;
import com.agapsys.rcf.Controller;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.exceptions.ForbiddenException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    public static LoginInfo doLogin(AgresteContainer ac, String username, String password) {
        Endpoint endpoint = new Endpoint(HttpMethod.GET, "/user/login");
        HttpClient client = new HttpClient();
        StringResponse resp = ac.doRequest(client, endpoint.getRequest("username=%s&password=%s", username, password));
        return new LoginInfo(client, resp);
    }
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    private AgresteContainer ac;

    @Before
    public void before() {
        System.out.println("Starting application...");

        ac = new AgresteContainer<>(TestApplication.class)
            .registerController(UserController.class);

        ac.start();
    }

    @After
    public void after() {
        System.out.println("\nShutting down the application...");

        ac.stop();
    }

    @Test
    public void testLogin() {
        Endpoint endpoint = new Endpoint(HttpMethod.GET, "/user/login");
        TestUtils.println(endpoint.toString());

        HttpClient client = new HttpClient();
        StringResponse resp;

        // Invalid username...
        resp = ac.doRequest(client, endpoint.getRequest("username=%s&password=%s", "invalid_user", "invalid_password"));
        TestUtils.assertErrorStatus(ForbiddenException.CODE, "Invalid credentials", resp);

        // Invalid password...
        resp = ac.doRequest(client, endpoint.getRequest("username=%s&password=%s", "username", "invalid-password"));
        TestUtils.assertStatus(ForbiddenException.CODE, resp);

        // Valid credentials...
        resp = ac.doRequest(client, endpoint.getRequest("username=%s&password=%s", "username", "password"));
        TestUtils.assertStatus(200, resp);
    }

    @Test
    public void testSecuredAction() {
        Endpoint endpoint = new Endpoint(HttpMethod.GET, "/user/me");
        TestUtils.println(endpoint.toString());

        StringResponse resp;

        // Unlogged access...
        resp = ac.doRequest(endpoint.getRequest());
        TestUtils.assertStatus(401, resp);

        // Logged access (without CSRF)...
        LoginInfo loginInfo = doLogin(ac, "username", "password");
        resp = ac.doRequest(loginInfo.getClient(), endpoint.getRequest());
        TestUtils.assertStatus(401, resp);

        // Logged access (with CSRF)...
        loginInfo.getClient().addDefaultHeader(Controller.CSRF_HEADER, loginInfo.getResponse().getFirstHeader(Controller.CSRF_HEADER).getValue());
        resp = ac.doRequest(loginInfo.getClient(), endpoint.getRequest());
        TestUtils.assertStatus(200, resp);
        UserDto dto = TestUtils.readJsonObject(UserDto.class, resp);
        Assert.assertEquals("username", dto.username);
    }
    // =========================================================================
}
