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
package com.agapsys.agreste.app.entities;

import com.agapsys.jpa.AbstractEntity;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Represents an application user.
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
@Entity
public class User extends AbstractEntity<User> implements com.agapsys.rcf.User {
	// STATIC SCOPE ============================================================
	public static class UserDto {
		public Long id;
		public String username;
		public List<String> roles;
		
		public UserDto() {}
		public UserDto(User user) {
			this.id = user.getId();
			this.username = user.getUsername();
			this.roles = user.getRoleList();
		}
	}
	
	private static String getPasswordHash(String password) {
		int logRounds = 4;
		return (BCrypt.hashpw(password, BCrypt.gensalt(logRounds)));
	}
	
	private static boolean checkPassword(String password, String passwordHash) {
		return BCrypt.checkpw(password, passwordHash);
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	// ID ----------------------------------------------------------------------
	@Id
	@GeneratedValue
	private Long id;

	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	// -------------------------------------------------------------------------
	
	// Username ----------------------------------------------------------------
	@Column(unique = true)
	private String username;
	
	public String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		if (username == null || username.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty username");
		
		this.username = username;
	}
	// -------------------------------------------------------------------------
	
	// Password ----------------------------------------------------------------
	private String passwordHash;
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		if (passwordHash == null || passwordHash.trim().isEmpty()) throw new IllegalArgumentException("Null/Empty password hash");
			this.passwordHash = passwordHash;
	}
	public final void setPassword(String password) {
		setPasswordHash(getPasswordHash(password));
	}
	public boolean isPasswordValid(String password) {
		return checkPassword(password, getPasswordHash());
	}
	// -------------------------------------------------------------------------

	// Roles -------------------------------------------------------------------
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roleList;
	
	public void addRole(String...roles) {
		int i = 0;
		for (String role : roles) {
			if (role == null) throw new IllegalArgumentException("Null role at index " + i);
			
			getRoleList().add(role);
			i++;
		}
	}
	public void clearRoles() {
		getRoleList().clear();
	}
	public final void setRoleList(String...roles) {
		this.roleList = Arrays.asList(roles);
	}
	public List<String> getRoleList() {
		return roleList;
	}
	// -------------------------------------------------------------------------
	
	// Constructors ------------------------------------------------------------
	public User() {}
	
	public User(String username, String password, String...roles) {
		setUsername(username);
		setPassword(password);
		setRoleList(roles);
	}
	// -------------------------------------------------------------------------
	
	@Override
	public String[] getRoles() {
		List<String> roleList = getRoleList();
		return roleList.toArray(new String[roleList.size()]);
	}
	// =========================================================================
}
