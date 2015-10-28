/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.agrest.services.UploadService;
import com.agapsys.http.MultipartRequest.MultipartPost;
import com.agapsys.sevlet.test.ServletContainer;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.HttpMethod;
import com.agapsys.web.action.dispatcher.WebAction;
import com.agapsys.web.toolkit.AbstractService;
import java.io.File;
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
		sc = ServletContainer.getInstance(UploadServlet.class);
		sc.startServer();
	}
	
	@After
	public void after() {
		sc.stopServer();
	}
	
	@Test
	public void test() {
		MultipartPost post = new MultipartPost("/upload");
		File file = new File("/home/leandro-agapsys/me-color.png");
		post.addFile(file);
		sc.doRequest(post);
	}
	// =========================================================================
}
