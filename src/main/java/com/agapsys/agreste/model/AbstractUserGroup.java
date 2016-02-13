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

package com.agapsys.agreste.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractUserGroup implements Serializable {
	// -------------------------------------------------------------------------
	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	// -------------------------------------------------------------------------
	@ElementCollection
	private final Set<String> roles = new LinkedHashSet<>();

	public Set<String> getRoles() {
		return roles;
	}
	public void addRole(String role) {
		if (role == null || role.trim().isEmpty())
			throw new IllegalArgumentException("Nul/Empty role");

		if (!getRoles().add(role))
			throw new IllegalArgumentException(String.format("Duplicate role: %s", role));
	}
	public boolean hasRoles(String...roles) {
		if (roles.length == 0)
			throw new IllegalArgumentException("Empty roles");

		return getRoles().containsAll(Arrays.asList(roles));
	}
	// -------------------------------------------------------------------------
	@Column(unique = true)
	private String name;

	public String getName() {
		if (name == null)
			return "";

		return name;
	}
	public void setName(String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty name");

		this.name = name;
	}
	// -------------------------------------------------------------------------
	@ManyToMany(mappedBy = "groups")
	private final List<AbstractUser> users = new LinkedList<>();

	public List<AbstractUser> getUsers() {
		return users;
	}
	public void addUser(AbstractUser user) {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");

		if (!user.getGroups().contains(this)) {
			user.getGroups().add(this);
			users.add(user);
		}
	}
	public void removeUser(AbstractUser user) {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");

		if (user.getGroups().remove(this))
			users.remove(user);
	}
	// -------------------------------------------------------------------------
	@Version
	private Long version;
	
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	// -------------------------------------------------------------------------
}
