/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agrest;

import com.agapsys.agreste.servlets.ThreadAttributeMap;
import com.agapsys.agreste.test.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author leandro-agapsys
 */
public class ThreadAttributeMapTest {
	// CLASS SCOPE =============================================================
	private static class ErrorWrapper {
		private Throwable error = null;
		
		public synchronized Throwable getError() {
			return error;
		}
		
		public synchronized void setError(Throwable error) {
			this.error = error;
		}
	}
	// =========================================================================
	
	private final ThreadAttributeMap tam = new ThreadAttributeMap();
	private final TestUtils testUtils = TestUtils.getInstance();
	
	
	@After
	public void after() {
		tam.destroyAttributes();
	}
	
	@Test
	public void test() throws InterruptedException {
		final ErrorWrapper errorWrapper = new ErrorWrapper();
		
		tam.setAttribute("val", "mainThread");
		Assert.assertEquals("mainThread", tam.getAttribute("val"));
		
		Thread anotherThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Assert.assertNull(tam.getAttribute("val"));
					tam.setAttribute("val", "anotherThread");
					Assert.assertEquals("anotherThread", tam.getAttribute("val"));
				} catch (Throwable t) {
					errorWrapper.setError(t);
				}
			}
		});
		
		anotherThread.start();
		anotherThread.join();
		Assert.assertEquals("mainThread", tam.getAttribute("val"));
		Assert.assertNull(errorWrapper.getError());
	}
}
