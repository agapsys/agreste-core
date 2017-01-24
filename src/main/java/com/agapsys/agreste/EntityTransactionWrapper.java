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

import javax.persistence.EntityTransaction;

/**
 * Wrapper for an {@linkplain EntityTransaction} instance.
 */
class EntityTransactionWrapper implements EntityTransaction {
    private final EntityTransaction wrappedTransaction;

    /**
     * Constructor
     * @param wrappedTransaction wrapped instance.
     */
    EntityTransactionWrapper(EntityTransaction wrappedTransaction) {
        this.wrappedTransaction = wrappedTransaction;
    }

    @Override
    public void begin() {
        wrappedTransaction.begin();
    }

    @Override
    public void commit() {
        wrappedTransaction.commit();
    }

    @Override
    public void rollback() {
        wrappedTransaction.rollback();
    }

    @Override
    public void setRollbackOnly() {
        wrappedTransaction.setRollbackOnly();
    }

    @Override
    public boolean getRollbackOnly() {
        return wrappedTransaction.getRollbackOnly();
    }

    @Override
    public boolean isActive() {
        return wrappedTransaction.isActive();
    }
}
