/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.services;

import com.agapsys.web.toolkit.AbstractService;
import org.junit.Assert;
import org.junit.Test;

public class ServiceFactoryTest {
	// CLASS SCOPE =============================================================
	public static class TestService extends AbstractService {}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	@Test
	public void testSingleton() {
		AbstractService instance1 = AbstractService.getService(TestService.class);
		AbstractService instance2 = AbstractService.getService(TestService.class);
		
		Assert.assertTrue(instance1 == instance2);
	}
	// =========================================================================
}
