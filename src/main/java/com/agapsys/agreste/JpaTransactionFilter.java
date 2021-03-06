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

package com.agapsys.agreste;

import com.agapsys.web.toolkit.AbstractApplication;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exposes a JPA transaction associated with a request.
 * If application does not have a persistence service, this filter does nothing.
 */
public class JpaTransactionFilter implements Filter {

    // STATIC SCOPE ============================================================
    public static final String JPA_TRANSACTION_ATTRIBUTE = JpaTransactionFilter.class.getName() + ".JPA_TRANSACTION_ATTRIBUTE";

    private static class ServletTransaction extends EntityTransactionWrapper implements JpaTransaction {
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

    private static class ServletEntityManger extends EntityManagerWrapper {
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        AbstractApplication app = AbstractApplication.getRunningInstance();
        PersistenceService persistenceService = (app != null ? app.getService(PersistenceService.class, false) : null);

        if (persistenceService != null) {

            HttpServletResponse resp = (HttpServletResponse) response;
            HttpServletRequest  req = (HttpServletRequest) request;

            ServletTransaction jpaTransaction = (ServletTransaction) new ServletEntityManger(persistenceService.getEntityManager()).getTransaction();

            try {
                jpaTransaction.wrappedBegin();
                req.setAttribute(JPA_TRANSACTION_ATTRIBUTE, jpaTransaction);
            
                chain.doFilter(request, response);
                jpaTransaction.wrappedCommit();

            } catch (Throwable error) {
                if (jpaTransaction.isActive())
                    jpaTransaction.wrappedRollback();

                if (error instanceof OptimisticLockException) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } else {
                    if (error instanceof IOException)
                        throw (IOException)error;

                    if (error instanceof ServletException)
                        throw (ServletException) error;

                    if (error instanceof RuntimeException)
                        throw (RuntimeException) error;

                    throw new RuntimeException(error);
                }
            } finally {
                req.removeAttribute(JPA_TRANSACTION_ATTRIBUTE);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
    // =========================================================================
}
