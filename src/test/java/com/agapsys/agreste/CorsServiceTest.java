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

import com.agapsys.agreste.test.AgresteContainer;
import com.agapsys.agreste.test.TestUtils;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpHeader;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.ActionResponse;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import com.agapsys.web.toolkit.MockedWebApplication;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CorsServiceTest {
    // CLASS SCOPE =============================================================
    @BeforeClass
    public static void beforeClass() {
        System.out.println(String.format("=== %s ===", CorsServiceTest.class.getSimpleName()));
    }

    @AfterClass
    public static void afterClass() {
        System.out.println();
    }

    @WebListener
    public static class TestApplication extends MockedWebApplication {

        public static final String VAL_ALLOWED_HEADERS = "testHeaders";
        public static final String[] VAL_ALLOWED_ORIGINS = new String[] {"testOrigin1", "testOrigin2"};
        public static final String VAL_ALLOWED_METHODS = "testMethods";

        @Override
        protected void beforeStart() {
            super.beforeStart();

            registerService(new CorsService() {
                @Override
                public String getAllowedHeaders() {
                    return VAL_ALLOWED_HEADERS;
                }

                @Override
                public String[] getAllowedOrigins() {
                    return VAL_ALLOWED_ORIGINS;
                }

                @Override
                public String getAllowedMethods() {
                    return VAL_ALLOWED_METHODS;
                }
                
            });
        }
    }

    @WebController("test")
    public static class TestController extends AgresteController {

        @Override
        protected void beforeAction(ActionRequest request, ActionResponse response) throws ServletException, IOException {
            CorsService corsService = getService(CorsService.class);
            corsService.putCorsHeaders(response.getServletResponse());
        }

        @WebAction
        public void get(HttpServletRequest req) {}
    }
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    private AgresteContainer ac;

    @Before
    public void before() {
        System.out.println("Starting application...");
        ac = new AgresteContainer<>(TestApplication.class)
            .registerController(TestController.class);

        ac.start();
    }

    @After
    public void after() {
        System.out.println("\nShutting down application...");
        ac.stop();
    }

    @Test
    public void testCorsHeaders() {
        StringResponse resp = ac.doRequest(new HttpGet("/test/get"));
        TestUtils.assertStatus(200, resp);

        List<HttpHeader> allowedOrigins = resp.getHeaders("Access-Control-Allow-Origin");
        HttpHeader allowMethodsHeader = resp.getFirstHeader("Access-Control-Allow-Methods");
        HttpHeader allowHeadersHeader = resp.getFirstHeader("Access-Control-Allow-Headers");

        Assert.assertEquals(2, allowedOrigins.size());
        Assert.assertNotNull(allowMethodsHeader);
        Assert.assertNotNull(allowHeadersHeader);

        Assert.assertEquals("testOrigin1", allowedOrigins.get(0).getValue());
        Assert.assertEquals("testOrigin2", allowedOrigins.get(1).getValue());
        Assert.assertEquals(TestApplication.VAL_ALLOWED_METHODS, allowMethodsHeader.getValue());
        Assert.assertEquals(TestApplication.VAL_ALLOWED_HEADERS, allowHeadersHeader.getValue());
    }
    // =========================================================================
}
