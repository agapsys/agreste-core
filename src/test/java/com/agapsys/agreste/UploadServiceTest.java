/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste;

import com.agapsys.agreste.services.UploadService;
import com.agapsys.http.HttpClient;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.http.MultipartRequest.MultipartPost;
import com.agapsys.sevlet.test.ServletContainer;
import com.agapsys.sevlet.test.ServletContainerBuilder;
import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.HttpMethod;
import com.agapsys.web.action.dispatcher.WebAction;
import com.agapsys.agreste.exceptions.BadRequestException;
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
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UploadServiceTest {
	private static final SingletonManager SINGLETON_MANAGER = new SingletonManager();
	
	// CLASS SCOPE =============================================================
	@WebServlet("/upload/*")
	public static class UploadServlet extends ActionServlet {
		
		private final UploadService uploadService = (UploadService) SINGLETON_MANAGER.getSingleton(UploadService.class);

		@WebAction
		public void finish(HttpExchange exchange) {
			uploadService.clearSessionFile(exchange.getRequest());
		}
		
		@WebAction(httpMethods = HttpMethod.POST)
		public void upload(HttpExchange exchange) throws IOException, BadRequestException {
			uploadService.receiveFiles(exchange.getRequest(), exchange.getResponse(), null);
			List<File> sessionFiles = uploadService.getSessionFiles(exchange.getRequest());
			if (!sessionFiles.isEmpty()) {
				for (File file : sessionFiles) {
					exchange.getResponse().getWriter().println(file.getAbsolutePath());
				}
			}
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
			
			while( (read = is.read(buffer)) > 0) {
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
		File[] uploadFiles = new File[] {
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
