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
public abstract class AbstractUser<T extends AbstractUser> extends AbstractEntity<T> implements User {
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
	private final Set<String> userRoles = new LinkedHashSet<>();

	public Set<String> getUserRoles() {
		return userRoles;
	}
	
	@Transient
	private Set<String> effectiveRoles = null;

	@Override
	public Set<String> getRoles() {
		if (effectiveRoles == null) {
			effectiveRoles = new LinkedHashSet<>();
			effectiveRoles.addAll(getUserRoles());
			for (AbstractUserGroup userGroup : getGroups()) {
				effectiveRoles.addAll(userGroup.getRoles());
			}
		}

		return effectiveRoles;
	}
	
	public void addRole(String role) {
		if (role == null || role.trim().isEmpty())
			throw new IllegalArgumentException("Nul/Empty role");

		if (!getUserRoles().add(role))
			throw new IllegalArgumentException(String.format("Duplicate role: %s", role));
	}
	public boolean hasRoles(String...roles) {
		if (roles.length == 0)
			throw new IllegalArgumentException("Empty roles");

		return getRoles().containsAll(Arrays.asList(roles));
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
