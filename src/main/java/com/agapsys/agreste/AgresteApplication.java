/*
 * Copyright 2017 Agapsys Tecnologia Ltda-ME.
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

import com.agapsys.web.toolkit.AbstractWebApplication;
import javax.servlet.ServletException;

public abstract class AgresteApplication extends AbstractWebApplication {

    public interface Factory<T> {
        public T createInstance();
    }

    protected Factory<PersistenceService> getPersistenceServiceFactory() {
        return new Factory<PersistenceService>() {
            @Override
            public PersistenceService createInstance() {
                return new PersistenceService();
            }
        };
    }

    @Override
    protected void beforeStart() {
        super.beforeStart();

        Factory<PersistenceService> persistenceServiceFactory = getPersistenceServiceFactory();
        if (persistenceServiceFactory != null) {
            PersistenceService persistenceService = persistenceServiceFactory.createInstance();
            if (persistenceService != null) {
                registerService(persistenceService);
            }
        }

    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        try {

            JpaTransactionFilter jpaTransactionFilter = JpaTransactionFilter.getInstance();
            
            if (jpaTransactionFilter != null)
                jpaTransactionFilter.init(null);
                        
        } catch (ServletException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        JpaTransactionFilter jpaTransactionFilter = JpaTransactionFilter.getInstance();
            
        if (jpaTransactionFilter != null)
            jpaTransactionFilter.destroy();
    }

}
