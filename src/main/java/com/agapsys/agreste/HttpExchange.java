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

import com.agapsys.jpa.AbstractEntity;
import com.agapsys.rcf.User;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Leandro Oliveira (leandro@agapsys.com.br)
 */
public class HttpExchange extends com.agapsys.rcf.HttpExchange {
    // STATIC SCOPE ============================================================
    public static final ParamMapSerializer DEFAULT_PARAM_MAP_SERIALIZER = new ParamMapSerializer();
    // =========================================================================

    // INSTANCE SCOPE ==========================================================
    public HttpExchange(HttpServletRequest req, HttpServletResponse resp) {
        super(req, resp);
    }

    @Override
    protected HttpRequest getCustomRequest(HttpServletRequest coreRequest) {
        return new HttpRequest(this, coreRequest);
    }

    @Override
    public HttpRequest getRequest() {
        return (HttpRequest) super.getRequest();
    }

    @Override
    public User getCurrentUser() {
        User user = super.getCurrentUser();

        if (user != null && (user instanceof AbstractEntity)) {
            JpaTransaction jpa = getJpaTransaction();

            if (jpa != null) {
                EntityManager em = jpa.getEntityManager();

                if (!em.contains(user)) {
                    user = em.find(user.getClass(), ((AbstractEntity)user).getId());
                }
            }
        }

        return user;
    }

    // -------------------------------------------------------------------------
    private ParamMapSerializer paramMapSerializer;

    /**
     * Returns the default parameter map serializer used by this HTTP exchange.
     *
     * @return the default parameter map serializer used by this HTTP exchange.
     */
    public ParamMapSerializer getParamMapSerializer() {
        if (paramMapSerializer == null) {
            paramMapSerializer = getCustomParamMapSerializer();
        }
        return paramMapSerializer;
    }

    /**
     * Returns a customized parameter map serializer used by this exchange.
     *
     * @return a customized parameter map serializer used by this exchange.
     */
    protected ParamMapSerializer getCustomParamMapSerializer() {
        return DEFAULT_PARAM_MAP_SERIALIZER;
    }
    // -------------------------------------------------------------------------

    /**
     * Returns the managed JPA transaction associated with this HTTP exchange.
     *
     * @return the managed JPA transaction associated with this HTTP exchange.
     */
    public JpaTransaction getJpaTransaction() {
        return (JpaTransaction) getCoreRequest().getAttribute(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
    }
    // =========================================================================
}
