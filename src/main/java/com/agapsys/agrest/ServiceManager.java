/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.web.toolkit.SingletonManager;

public class ServiceManager {
	private final SingletonManager serviceSingletonManager = new SingletonManager();

	public void registerService(String id, Class<? extends Service> serviceClass) {
		serviceSingletonManager.registerSingleton(id, serviceClass);
	}
	
	public <T extends Service> T getService(Class<T> serviceClass) {
		return (T) serviceSingletonManager.getSingleton(serviceClass);
	}
	
	public Service getService(String id) {
		return (Service) serviceSingletonManager.getSingleton(id);
	}
	
	public void clear() {
		serviceSingletonManager.clear();
	}
}
