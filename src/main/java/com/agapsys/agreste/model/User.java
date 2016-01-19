/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.model;

import com.agapsys.jpa.AbstractEntity;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="usr")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User extends AbstractEntity implements com.agapsys.web.action.dispatcher.User {
	// -------------------------------------------------------------------------
	@Id
	@GeneratedValue
	private Long id;

	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	// -------------------------------------------------------------------------
	@ManyToMany
	private final List<UserGroup> groups = new LinkedList<>();

	public List<UserGroup> getGroups() {
		return groups;
	}
	// -------------------------------------------------------------------------
	@ElementCollection
	private final Set<String> roles = new LinkedHashSet<>();

	@Transient
	private Set<String> effectiveRoles = null;

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	public Set<String> getEffectiveRoles() {
		if (effectiveRoles == null) {
			effectiveRoles = new LinkedHashSet<>();
			effectiveRoles.addAll(getRoles());
			for (UserGroup userGroup : getGroups()) {
				effectiveRoles.addAll(userGroup.getRoles());
			}
		}

		return effectiveRoles;
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
}
