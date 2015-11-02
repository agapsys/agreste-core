/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.web.toolkit.SingletonManager;
import com.agapsys.web.toolkit.SingletonManager.Singleton;

public abstract class Service implements Singleton {
	// CLASS SCOPE =============================================================
	private static final SingletonManager SINGLETON_MANAGER = new SingletonManager();
	
	public static void registerService(String id, Class<? extends Service> serviceClass) {
		SINGLETON_MANAGER.registerSingleton(id, serviceClass);
	}
	
	public static <T extends Service> T getInstance(Class<T> serviceClass) {
		return (T) SINGLETON_MANAGER.getSingleton(serviceClass);
	}
	
	public static Service getInstance(String id) {
		return (Service) SINGLETON_MANAGER.getSingleton(id);
	} 
	// =========================================================================
}
