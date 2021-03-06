/*
 * Copyright 2016-2017 Agapsys Tecnologia Ltda-ME.
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
package com.agapsys.agreste.app;

import com.agapsys.agreste.PersistenceService;
import com.agapsys.agreste.app.entities.User;
import com.agapsys.agreste.test.MockedAgresteApplication;
import javax.persistence.EntityManager;

public class TestApplication extends MockedAgresteApplication {

    @Override
    protected void onStart() {
        super.onStart();

        PersistenceService persistenceService = getServiceOnDemand(PersistenceService.class);

        EntityManager em = persistenceService.getEntityManager();

        // ---------------------------------------------------------------------
        em.getTransaction().begin();

        new User("username", "password", 0).save(em);

        em.getTransaction().commit();
        // ---------------------------------------------------------------------

        em.close();
    }
}
