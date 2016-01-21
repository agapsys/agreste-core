/*
 * Copyright 2015 Agapsys Tecnologia Ltda-ME.
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

package com.agapsys.agreste.servlets;

import com.agapsys.web.action.dispatcher.ActionServlet;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.LazyInitializer;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;

/**
 * Specialization of {@linkplain ActionServlet} which manages JPA transactions during HTTP exchange processing.
 * A transaction will be initialized after each HTTP exchange and will be committed (when exchange is successfully processed) or rolled back (if there is an error while processing the exchange).
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class TransactionalServlet extends ActionServlet {
	// CLASS SCOPE =============================================================
	/** Name of request attribute containing the transaction. */
	private static final String REQ_ATTR_TRANSACTION = "com.agapsys.web.action.dispatcher.transaction";
	
	private static class ServletTransaction extends WrappedEntityTransaction implements Transaction {
		private final UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("Transaction is managed by servlet");
		private final EntityManager em;
		private final List<Runnable> commitQueue = new LinkedList<>();
		private final List<Runnable> rollbackQueue = new LinkedList<>();
		
		public ServletTransaction(ServletEntityManger em, EntityTransaction wrappedTransaction) {
			super(wrappedTransaction);
			this.em = em;
		}
		
		private void processQueue(List<Runnable> queue) {
			for (Runnable runnable : queue) {
				runnable.run();
			}
			queue.clear();
		}
		
		@Override
		public void commit() {
			throw unsupportedOperationException;
		}
		public void wrappedCommit() {
			super.commit();
			processQueue(commitQueue);
		}
		
		@Override
		public void begin() {
			throw unsupportedOperationException;
		}
		public void wrappedBegin() {
			super.begin();
		}

		@Override
		public void rollback() {
			throw unsupportedOperationException;
		}
		public void wrappedRollback() {
			super.rollback();
			processQueue(rollbackQueue);
		}

		@Override
		public EntityManager getEntityManager() {
			return em;
		}

		private void invokeAfter(List<Runnable> queue, Runnable runnable) {
			if (runnable == null)
				throw new IllegalArgumentException("Null runnable");
			
			queue.add(runnable);
		}
		
		@Override
		public void invokeAfterCommit(Runnable runnable) {
			invokeAfter(commitQueue, runnable);
		}

		@Override
		public void invokeAfterRollback(Runnable runnable) {
			invokeAfter(rollbackQueue, runnable);
		}
	}
	
	private static class ServletEntityManger extends WrappedEntityManager {
		private final UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("Entity manager is managed by servlet");
		private final ServletTransaction singleTransaction;
		
		public ServletEntityManger(EntityManager wrappedEntityManager) {
			super(wrappedEntityManager);
			singleTransaction = new ServletTransaction(this, super.getTransaction());
		}

		@Override
		public EntityTransaction getTransaction() {
			return singleTransaction;
		}

		@Override
		public void close() {
			throw unsupportedOperationException;
		}
		public void wrappedClose() {
			super.close();
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private final LazyInitializer<EntityManagerProvider> entityManagerProvider = new LazyInitializer<EntityManagerProvider>() {

		@Override
		protected EntityManagerProvider getLazyInstance() {
			return TransactionalServlet.this.getEntityManagerProvider();
		}
	};
	
	private void closeTransaction(HttpExchange exchange, Throwable error) {
		HttpServletRequest req = exchange.getRequest();
		ServletTransaction transaction = (ServletTransaction) getTransaction(exchange);
		
		if (transaction != null) {
			if (error != null) {
				transaction.wrappedRollback();
			} else {
				transaction.wrappedCommit();
			}
			
			req.removeAttribute(REQ_ATTR_TRANSACTION);
			
			((ServletEntityManger)transaction.getEntityManager()).wrappedClose();
		}
	}

	/** 
	 * Return the factory of entity managers used by this servlet. 
	 * This method is intended to be overridden to change servlet initialization and not be called directly
	 * @return {@link EntityManagerProvider} instance used by this servlet. Default implementation returns null.
	 */
	protected EntityManagerProvider getEntityManagerProvider() {
		return null;
	}
	
	private EntityManagerProvider _getEntityManagerProvider() {
		return entityManagerProvider.getInstance();
	}
	
	/** 
	 * Handles an error in the application and returns a boolean indicating if error shall be propagated. Default implementation rollback current transaction.
	 * @param exchange HTTP exchange
	 * @param error error
	 * @param transaction JPA transaction. If {@linkplain TransactionalServlet#getEntityManagerProvider()} returns null, this parameter will be null.
	 * @return a boolean indicating if given error shall be propagated. Default always return true.
	 */
	protected boolean onError(HttpExchange exchange, Throwable error, Transaction transaction) {
		return true;
	}
	
	@Override
	protected final boolean onError(HttpExchange exchange, Throwable throwable) {
		super.onError(exchange, throwable);
		RuntimeException error = null;
		boolean result = true;
		
		try {
			result = onError(exchange, throwable, getTransaction(exchange));
		} catch (RuntimeException ex) {
			error = ex;
		}
		
		closeTransaction(exchange, throwable);
		
		if (error != null)
			throw error;
		
		return result;
	}

	/** 
	 * Called before an action. 
	 * This method will be called only if an action associated to given request is found and it it allowed to be processed (see {@link SecurityManager}).
	 * Default implementation does nothing.
	 * @param exchange HTTP exchange
	 * @param transaction JPA transaction. If {@linkplain TransactionalServlet#getEntityManagerProvider()} returns null, this parameter will be null.
	 */
	protected void beforeAction(HttpExchange exchange, Transaction transaction) {}
	
	@Override
	protected final void beforeAction(HttpExchange exchange) {
		super.beforeAction(exchange);
		beforeAction(exchange, getTransaction(exchange));
	}

	/** 
	 * Called after an action. 
	 * This method will be called only if an action associated to given request is found, the action is allowed to be processed (see {@link SecurityManager}), and the action was successfully processed.
	 * Default implementation does nothing.
	 * @param exchange HTTP exchange
	 * @param transaction JPA transaction. If {@linkplain TransactionalServlet#getEntityManagerProvider()} returns null, this parameter will be null.
	 */
	protected void afterAction(HttpExchange exchange, Transaction transaction) {}
	
	@Override
	protected final void afterAction(HttpExchange exchange) {
		super.afterAction(exchange);

		RuntimeException error = null;
		
		try {
			afterAction(exchange, getTransaction(exchange));
		} catch (RuntimeException ex) {
			error = ex;
		}
		
		closeTransaction(exchange, error);
		
		if (error != null)
			throw error;
	}
	
	/**
	 * Returns the transaction associated with given request.
	 * Multiple calls to this methods passing the same request will return the same transaction instance.
	 * @param exchange HTTP exchange
	 * @return the transaction associated with given request. If there is no {@linkplain EntityManagerProvider} associated with the servlet (see {@linkplain TransactionalServlet#getEntityManagerProvider()}), returns null.
	 */
	public final Transaction getTransaction(HttpExchange exchange) {
		HttpServletRequest req = exchange.getRequest();
		EntityManagerProvider emp = _getEntityManagerProvider();
		
		if (emp != null) {
			ServletTransaction transaction = (ServletTransaction) req.getAttribute(REQ_ATTR_TRANSACTION);

			if (transaction == null) {
				transaction = (ServletTransaction) new ServletEntityManger(emp.getEntityManager()).getTransaction();
				transaction.wrappedBegin();
				req.setAttribute(REQ_ATTR_TRANSACTION, transaction);
			}

			return transaction;
		} else {
			return null;
		}
	}
	// =========================================================================
}