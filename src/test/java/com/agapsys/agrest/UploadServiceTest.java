/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.agrest.services.UploadService;
import com.agapsys.sevlet.test.ApplicationContext;
import com.agapsys.sevlet.test.FormUrlEncodedPost;
import com.agapsys.sevlet.test.MultipartEntityPost;
import com.agapsys.sevlet.test.ServletContainer;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.HttpMethod;
import com.agapsys.web.action.dispatcher.WebAction;
import com.agapsys.web.toolkit.AbstractService;
import java.io.File;
import java.nio.charset.Charset;
import javax.servlet.annotation.WebServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UploadServiceTest {
	// CLASS SCOPE =============================================================
	@WebServlet("/upload/*")
	public static class UploadServlet extends ActionServlet {
		private final UploadService uploadService = AbstractService.getService(UploadService.class);
		
		@WebAction(httpMethods = HttpMethod.POST, defaultAction = true)
		public void upload(HttpExchange exchange) {
			uploadService.receiveFiles(exchange.getRequest(), exchange.getResponse());
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private ServletContainer sc;
	
	@Before
	public void before() {
		sc = new ServletContainer();

		ApplicationContext context = new ApplicationContext();
		context.registerServlet(UploadServlet.class);
		
		sc.registerContext(context, "/");
		sc.startServer();
	}
	
	@After
	public void after() {
		sc.stopServer();
	}
	
	@Test
	public void test() {
		MultipartEntityPost post = new MultipartEntityPost(sc, "/upload");
		File file = new File("/home/leandro-agapsys/me-color.png");
		post.addFile(file);
		sc.doEntityRequest(post);
	}
	// =========================================================================
}
