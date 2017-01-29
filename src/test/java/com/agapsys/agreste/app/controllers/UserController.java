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
package com.agapsys.agreste.app.controllers;

import com.agapsys.agreste.app.entities.User;
import com.agapsys.agreste.app.entities.User.UserDto;
import com.agapsys.agreste.app.services.UserService;
import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.ActionResponse;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import com.agapsys.rcf.exceptions.ForbiddenException;
import java.io.IOException;
import javax.servlet.ServletException;

@WebController
public class UserController extends BaseController {

    private UserService userService;

    @Override
    protected void onControllerInit() {
        super.onControllerInit();
        userService = getService(UserService.class);
    }

    @WebAction(mapping = "/login")
    public UserDto login(ActionRequest request, ActionResponse response) throws ServletException, IOException {
        final String PARAM_USERNAME = "username";
        final String PARAM_PASSWORD = "password";

        String username = request.getMandatoryParameter(PARAM_USERNAME);
        String password = request.getMandatoryParameter(PARAM_PASSWORD);

        User user = userService.getUserByCredentials(getJpaTransaction(request), username, password);

        if (user == null) {
            throw new ForbiddenException("Invalid credentials");
        } else {
            registerUser(request, response, user);
        }

        return new UserDto(user);
    }

    @WebAction(secured = true)
    public User me(ActionRequest request, ActionResponse response) throws ServletException, IOException {
        return (User) getUser(request, response);
    }
}
