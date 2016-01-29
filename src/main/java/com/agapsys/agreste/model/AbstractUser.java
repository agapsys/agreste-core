/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.model;

import com.agapsys.jpa.AbstractEntity;
import com.agapsys.security.web.User;
import java.util.Arrays;
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
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractUser extends AbstractEntity implements User {
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
	private final List<AbstractUserGroup> groups = new LinkedList<>();

	public List<AbstractUserGroup> getGroups() {
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
			for (AbstractUserGroup userGroup : getGroups()) {
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
	public boolean hasRoles(String...roles) {
		if (roles.length == 0)
			throw new IllegalArgumentException("Empty roles");

		return getEffectiveRoles().containsAll(Arrays.asList(roles));
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
