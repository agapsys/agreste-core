/* 
 * Copyright (C) 2016 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.agapsys.agreste.model;

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
import javax.persistence.Table;

@Entity
@Table(name="usr")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AbstractUserGroup {
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
	public boolean hasRole(String role) {
		if (role == null || role.trim().isEmpty())
			throw new IllegalArgumentException("Nul/Empty role");

		return getRoles().contains(role);
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
	@ManyToMany(mappedBy = "userGroups")
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
}
