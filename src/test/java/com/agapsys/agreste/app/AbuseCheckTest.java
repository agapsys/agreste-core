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

import com.agapsys.agreste.AbuseCheckFilter;
import com.agapsys.agreste.test.AgresteContainer;
import com.agapsys.agreste.test.MockedWebApplication;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpResponse;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.rcf.exceptions.RateLimitingException;
import com.agapsys.utils.console.printer.ConsoleColor;
import com.agapsys.utils.console.printer.ConsolePrinter;
import com.agapsys.utils.console.printer.FormatEscapeBuilder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbuseCheckTest {
    // CLASS SCOPE =============================================================
    // Utility methods ---------------------------------------------------------
    private static void pause(long millis) {
        final Object sync = new Object();

        synchronized(sync) {
            try {
                sync.wait(millis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void assertStatus(int expected, StringResponse resp) {
        if (resp.getStatusCode() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            System.out.println(resp.getContentString());
        }

        Assert.assertEquals(expected, resp.getStatusCode());
    }

    private static void println(ConsoleColor fgColor, String message, Object...args) {
        if (args.length > 0)
            message = String.format(message, args);

        FormatEscapeBuilder feb = new FormatEscapeBuilder().setFgColor(fgColor);
        message = feb.toString(message);
        ConsolePrinter.println(message);
    }
    // -------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        System.out.println(String.format("=== %s ===", AbuseCheckTest.class.getSimpleName()));
    }

    @AfterClass
    public static void afterClass() {
        System.out.println();
    }

    // Classes -----------------------------------------------------------------
    @WebListener
    public static class TestApplication extends MockedWebApplication {
        // CLASS SCOPE =========================================================
        public static final long ABUSE_INTERVAL = 500;
        public static final int  ABUSE_COUNT_LIMIT = 3;
        // =====================================================================

        // INSTANCE SCOPE ======================================================
        private boolean abuseCheckEnabled = true;

        @Override
        public int getAbuseCountLimit() {
            return ABUSE_COUNT_LIMIT;
        }

        @Override
        public long getAbuseInterval() {
            return ABUSE_INTERVAL;
        }

        @Override
        protected void afterAgresteStart() {
            super.afterAgresteStart();
            println(ConsoleColor.CYAN, "Application directory: %s", getDirectory());
        }

        public void enableAbuseCheck(boolean enabled) {
            this.abuseCheckEnabled = enabled;
        }

        @Override
        public boolean isAbuseCheckEnabled() {
            return abuseCheckEnabled;
        }

        @Override
        public String getName() {
            return "ABUSE-CHECK-TEST";
        }

        @Override
        public String getVersion() {
            return "0.1.0-SNAPSHOT";
        }
        // =====================================================================
    }

    @WebServlet("/*")
    public static class TestServlet extends HttpServlet {

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {}
    }
    // -------------------------------------------------------------------------
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    private AgresteContainer sc;
    private TestApplication app;

    @Before
    public void before() {
        sc = new AgresteContainer<>(TestApplication.class)
            .registerServlet(TestServlet.class)
            .registerFilter(AbuseCheckFilter.class, "/*");

        sc.start();
        app = (TestApplication) TestApplication.getRunningInstance();
    }

    @After
    public void after() throws IOException {
        sc.stop();
        app = null;
    }

    @Test
    public void testAbuseCheck() {
        HttpClient client;

        // Detecting abuse (rejecting session cookie) --------------------------
        for (int i = 0; i < TestApplication.ABUSE_COUNT_LIMIT + 1; i++) {
            HttpResponse.StringResponse resp = sc.doRequest(new HttpGet("/"));
            assertStatus(HttpServletResponse.SC_UNAUTHORIZED, resp);
        }
        // ---------------------------------------------------------------------

        // Detecting abuse (with session cookie) -------------------------------
        client = new HttpClient();

        for (int i = 0; i < TestApplication.ABUSE_COUNT_LIMIT + 1; i++) {
            HttpResponse.StringResponse resp = sc.doRequest(client, new HttpGet("/"));
            if (i == 0)
                assertStatus(HttpServletResponse.SC_UNAUTHORIZED, resp);

            if (i > 0 && i < TestApplication.ABUSE_COUNT_LIMIT)
                assertStatus(HttpServletResponse.SC_OK, resp);

            if (i > TestApplication.ABUSE_COUNT_LIMIT)
                assertStatus(RateLimitingException.CODE, resp);
        }
        // ---------------------------------------------------------------------

        // By-passing abuse (without session cookie) ---------------------------
        app.enableAbuseCheck(false);

        for (int i = 0; i < TestApplication.ABUSE_COUNT_LIMIT + 1; i++) {
            HttpResponse.StringResponse resp = sc.doRequest(new HttpGet("/"));
            assertStatus(HttpServletResponse.SC_OK, resp);
        }

        app.enableAbuseCheck(true);
        // ---------------------------------------------------------------------

        // By-passing abuse (with session cookie) ---------------------------
        client = new HttpClient();

        app.enableAbuseCheck(false);

        for (int i = 0; i < TestApplication.ABUSE_COUNT_LIMIT + 1; i++) {
            HttpResponse.StringResponse resp = sc.doRequest(client, new HttpGet("/"));
            assertStatus(HttpServletResponse.SC_OK, resp);
        }

        app.enableAbuseCheck(true);
        // ---------------------------------------------------------------------

        // By-passing abuse (with pauses) --------------------------------------
        client = new HttpClient();

        for (int i = 0; i < TestApplication.ABUSE_COUNT_LIMIT + 5; i++) {
            HttpResponse.StringResponse resp = sc.doRequest(client, new HttpGet("/"));
            println(ConsoleColor.MAGENTA, "Pausing in order to pass abuse check filter...");
            pause(TestApplication.ABUSE_INTERVAL);

            if (i == 0)
                assertStatus(HttpServletResponse.SC_UNAUTHORIZED, resp);

            if (i > 0)
                assertStatus(HttpServletResponse.SC_OK, resp);
        }
        // ---------------------------------------------------------------------
    }
    // =========================================================================
}
