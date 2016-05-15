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

import com.agapsys.agreste.Controller;
import com.agapsys.agreste.app.entities.User;
import com.agapsys.agreste.app.entities.User.UserDto;
import com.agapsys.agreste.app.services.UserService;
import com.agapsys.rcf.HttpExchange;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import com.agapsys.rcf.exceptions.ForbiddenException;

/**
 *
 * @author Leandro Oliveira
 */
@WebController("user")
public class UserController extends Controller {
	
	private UserService userService;

	@Override
	protected void onInit() {
		super.onInit();
		
		userService = getService(UserService.class);
	}
	
	@WebAction(mapping = "login")
	public UserDto login(HttpExchange exchange) {
		final String PARAM_USERNAME = "username";
		final String PARAM_PASSWORD = "password";
		
		String username = exchange.getMandatoryRequestParameter(PARAM_USERNAME);
		String password = exchange.getMandatoryRequestParameter(PARAM_PASSWORD);
		
		User user = userService.getUserByCredentials(getJpaTransaction(exchange.getRequest()), username, password);
		if (user == null)
			throw new ForbiddenException();
		
		return new UserDto(user);
	}
	
	@WebAction(mapping = "me", secured = true)
	public UserDto me(HttpExchange exchange) {
		return new UserDto((User) getUser(exchange.getRequest()));
	}
}
