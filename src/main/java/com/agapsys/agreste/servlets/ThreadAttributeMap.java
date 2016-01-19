/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste.servlets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadAttributeMap {
	private final Map<Thread, Map<String, Object>> threadMap = new ConcurrentHashMap<>();

	public ThreadAttributeMap() {}

	private Map<String, Object> getAttributeMap() {
		Thread currentThread = Thread.currentThread();
		Map<String, Object> attributeMap = threadMap.get(currentThread);
		if (attributeMap == null) {
			attributeMap = new LinkedHashMap<>();
			threadMap.put(currentThread, attributeMap);
		}

		return attributeMap;
	}

	public Object getAttribute(String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty name");

		return getAttributeMap().get(name);
	}
	public void setAttribute(String name, Object attribute) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty name");

		getAttributeMap().put(name, attribute);
	}

	public void destroyAttribute(String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty name");

		Map<String, Object> attributeMap = threadMap.get(Thread.currentThread());
		if (attributeMap != null)
			attributeMap.remove(name);
	}

	public void destroyAttributes() {
		threadMap.remove(Thread.currentThread());
	}
}
