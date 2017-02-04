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
import com.agapsys.rcf.Controller.Dto;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents an application user.
 */
@Entity
public class User extends AbstractEntity<User> implements com.agapsys.rcf.User, Dto{
    // STATIC SCOPE ============================================================
    public static class UserDto {
        public Long id;
        public String username;
        public Set<String> roles;

        public UserDto() {}
        public UserDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.roles = user.getRoles();
        }
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
    private String password;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Null/Empty password hash");
            this.password = password;
    }
    public boolean isPasswordValid(String password) {
        return Objects.equals(getPassword(), password);
    }
    // -------------------------------------------------------------------------

    // Roles -------------------------------------------------------------------
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new LinkedHashSet<>();

    @Override
    public Set<String> getRoles() {
        return roles;
    }
    public void setRoles(Set<String> roles) {
        if (roles == null)
            throw new IllegalArgumentException("Role set cannot be null");

        this.roles = roles;
    }
    public final void setRoles(String...roles) {
        setRoles(new LinkedHashSet<>(Arrays.asList(roles)));
    }

    public void addRole(String...roles) {
        int i = 0;
        for (String role : roles) {
            if (role == null) throw new IllegalArgumentException("Null role at index " + i);

            getRoles().add(role);
            i++;
        }
    }
    public void clearRoles() {
        getRoles().clear();
    }
    // -------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------
    public User() {}

    public User(String username, String password, String...roles) {
        setUsername(username);
        setPassword(password);
        setRoles(roles);
    }
    // -------------------------------------------------------------------------

    @Override
    public Object getDto() {
        return new UserDto(this);
    }
    // =========================================================================
}
