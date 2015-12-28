/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.test;

import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.Transaction;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class MockedTransaction implements Transaction {
	private final List<Runnable> commitQueue = new LinkedList<>();
	private final List<Runnable> rollbackQueue = new LinkedList<>();

	private final EntityManager em;
	private final EntityTransaction et;

	public MockedTransaction(EntityManager em) {
		this.em = em;
		this.et = em.getTransaction();
		this.et.begin();
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void invokeAfterCommit(Runnable runnable) {
		if (runnable == null) throw new IllegalArgumentException("Null runnable");
		commitQueue.add(runnable);
	}

	@Override
	public void invokeAfterRollback(Runnable runnable) {
		if (runnable == null) throw new IllegalArgumentException("Null runnable");
		rollbackQueue.add(runnable);
	}

	@Override
	public HttpExchange getHttpExchange() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void close(boolean commit) {
		if (commit) {
			et.commit();
		} else {
			et.rollback();
		}
		em.close();
	}

	private void processQueue(List<Runnable> queue) {
		for (Runnable runnable : queue) {
			runnable.run();
		}

		queue.clear();
	}

	public void rollback() {
		close(false);
		processQueue(rollbackQueue);
	}

	public void commit() {
		close(true);
		processQueue(commitQueue);
	}
}
