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
package com.agapsys.agreste.app.controllers;

import com.agapsys.agreste.AgresteController;
import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.app.entities.User;
import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.ActionResponse;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;

public class BaseController extends AgresteController {

    @Override
    protected com.agapsys.rcf.User getUser(ActionRequest request, ActionResponse response) throws ServletException, IOException {
        User user = (User) super.getUser(request, response);

        if (user == null)
            return null;

        JpaTransaction jpa = getJpaTransaction(request);

        if (jpa != null) {
            EntityManager em = jpa.getEntityManager();

            if (!em.contains(user)) {
                user = em.find(User.class, user.getId());
            }
        }

        return user;
    }

}
