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

import com.agapsys.agreste.dto.AbstractDtoTest;
import com.agapsys.agreste.services.UploadService;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.http.MultipartRequest.MultipartPost;
import com.agapsys.rcf.Action;
import com.agapsys.rcf.Controller;
import com.agapsys.rcf.HttpExchange;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.WebAction;
import com.agapsys.sevlet.container.ServletContainer;
import com.agapsys.sevlet.container.ServletContainerBuilder;
import com.agapsys.web.toolkit.utils.SingletonManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UploadServiceTest {

	// CLASS SCOPE =============================================================

	private static final SingletonManager SINGLETON_MANAGER = new SingletonManager();

	@BeforeClass
	public static void beforeClass() {
		System.out.println(String.format("=== %s ===", AbstractDtoTest.class.getSimpleName()));
	}

	@AfterClass
	public static void afterClass() {
		System.out.println();
	}

	@WebServlet("/upload/*")
	public static class UploadServlet extends Controller {

		private final UploadService uploadService = (UploadService) SINGLETON_MANAGER.getSingleton(UploadService.class);
		private final Action uploadAction = new Action() {

			@Override
			public void processRequest(HttpExchange exchange) throws Throwable {
				uploadService.receiveFiles(exchange.getRequest(), exchange.getResponse(), null);
				List<File> sessionFiles = uploadService.getSessionFiles(exchange.getRequest());
				if (!sessionFiles.isEmpty()) {
					for (File file : sessionFiles) {
						exchange.getResponse().getWriter().println(file.getAbsolutePath());
					}
				}
			}
		};

		@Override
		protected void onInit() {
			super.onInit();
			registerAction(HttpMethod.POST, "/upload", uploadAction);
		}

		@WebAction
		public void finish(HttpServletRequest req) {
			uploadService.clearSessionFile(req);
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private ServletContainer sc;

	@Before
	public void before() {
		sc = ServletContainerBuilder.getServletContainer(UploadServlet.class);
		sc.startServer();
	}

	@After
	public void after() {
		sc.stopServer();
	}

	private String getFileMd5(File file) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}

		try (InputStream is = new FileInputStream(file)) {
			byte[] buffer = new byte[8192];

			int read = 0;

			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}

			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			return output;
		}
	}

	@Test
	public void test() throws IOException {
		File[] uploadFiles = new File[]{
			new File("test-res/logo_box.png"),
			new File("test-res/logo_box_inv.png")
		};

		HttpClient client = new HttpClient();

		MultipartPost post = new MultipartPost("/upload/upload");
		for (File uploadFile : uploadFiles) {
			post.addFile(uploadFile);
		}

		StringResponse resp = sc.doRequest(client, post);
		String[] receivedFilePaths = resp.getContentString().split(Pattern.quote("\n"));
		File[] receivedFiles = new File[receivedFilePaths.length];
		for (int i = 0; i < receivedFiles.length; i++) {
			receivedFiles[i] = new File(receivedFilePaths[i].trim());
		}

		Assert.assertEquals(uploadFiles.length, receivedFiles.length);
		for (int i = 0; i < receivedFiles.length; i++) {
			Assert.assertTrue(receivedFiles[i].exists());
			Assert.assertEquals(getFileMd5(uploadFiles[i]), getFileMd5(receivedFiles[i]));
		}

		resp = sc.doRequest(client, new HttpGet("/upload/finish"));
		Assert.assertEquals(HttpServletResponse.SC_OK, resp.getStatusCode());

		for (File receivedFile : receivedFiles) {
			Assert.assertFalse(receivedFile.exists());
		}
	}
	// =========================================================================
}
