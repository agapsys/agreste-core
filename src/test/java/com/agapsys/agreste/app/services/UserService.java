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
package com.agapsys.agreste.app.services;

import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.app.entities.User;
import com.agapsys.jpa.FindBuilder;
import com.agapsys.web.toolkit.Service;

public class UserService extends Service {

    public User getUserByCredentials(JpaTransaction jpa, String username, String password) {
        User user = new FindBuilder<>(User.class).by("username", username).findFirst(jpa.getEntityManager());
        if (user == null) return null;

        return user.isPasswordValid(password) ? user : null;
    }

}
