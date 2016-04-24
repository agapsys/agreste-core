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
package com.agapsys.agreste.app;

import com.agapsys.agreste.JpaTransaction;
import com.agapsys.agreste.WebSecurity;
import com.agapsys.agreste.controllers.Controller;
import com.agapsys.jpa.FindBuilder;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import com.agapsys.rcf.exceptions.ForbiddenException;
import com.agapsys.security.Secured;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
@WebController("controller")
public class MyController extends Controller {
	
	private MyService myService;

	@Override
	protected void onInit() {
		super.onInit();
		myService = getService(MyService.class);
	}
	
	@WebAction(httpMethods = HttpMethod.GET, mapping = "login")
	public void login(HttpServletRequest req) {
		final String PARAM_USERNAME = "username";
		final String PARAM_PASSWORD = "password";
		
		String username = getMandatoryParameter(req, PARAM_USERNAME);
		String password = getMandatoryParameter(req, PARAM_PASSWORD);
		
		JpaTransaction jpa = getJpaTransaction();
		MyUser user = new FindBuilder<>(MyUser.class).by("username", username).findFirst(jpa.getEntityManager());
		
		if (user == null || !user.isPasswordValid(password))
			throw new ForbiddenException("Invalid credentials");
		
		WebSecurity.setCurrentUser(user);
	}
	
	@WebAction(httpMethods = HttpMethod.GET, mapping = "publicGet")
	public void publicGet(HttpServletRequest req) throws IOException {}
	
	@WebAction(httpMethods = HttpMethod.GET, mapping = "securedGet")
	@Secured
	public void securedGet(HttpServletRequest req) throws IOException {}
	
	@WebAction(httpMethods = HttpMethod.GET, mapping = "implicitSecuredGet")
	public void implicitSecuredGet(HttpServletRequest req) throws IOException {
		myService.securedMethod();
	}
}
