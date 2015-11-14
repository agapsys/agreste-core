/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.modules;

import com.agapsys.agreste.modules.LdapModule.LdapException.LdapExceptionType;
import com.agapsys.web.toolkit.AbstractModule;
import com.agapsys.web.toolkit.AbstractWebApplication;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapModule extends AbstractModule {
	// CLASS SCOPE =============================================================
	public static class LdapException extends Exception {
		// CLASS SCOPE =========================================================
		public static enum LdapExceptionType {
			INVALID_CREDENTIALS,
			AUTHENTICATION_NOT_SUPPORTED,
			COMMUNICATION_FAILURE,
			NAMING_ERROR
		}
		// =====================================================================
		
		// INSTANCE SCOPE ======================================================
		private final LdapExceptionType exceptionType;
		
		private LdapException(LdapExceptionType exceptionType, String message, Throwable cause) {
			super(message, cause);
			this.exceptionType = exceptionType;
		}
		
		private LdapException(LdapExceptionType exceptionType, Throwable cause) {
			super(cause);
			this.exceptionType = exceptionType;
		}
		
		public LdapExceptionType getExceptionType() {
			return exceptionType;
		}
		// =====================================================================
	}
	
	public static class LdapAttribute {
		private final String name;
		private final List<String> values = new LinkedList<>();
		
		private List<String> unmodifiableValues = null;
		
		private LdapAttribute(Attribute attribute) throws NamingException {
			this.name = attribute.getID();
			
			NamingEnumeration nem = attribute.getAll();
			
			while(nem.hasMoreElements()) {
				Object valueObj = nem.next();
				
				if (valueObj instanceof String) 
					this.values.add(valueObj.toString());
			}
		}
		
		public String getName() {
			return name;
		}
		
		public List<String> getValues() {
			if (unmodifiableValues == null) {
				unmodifiableValues = Collections.unmodifiableList(values);
			}
			
			return unmodifiableValues;
		}

		@Override
		public String toString() {
			return String.format("%s: %s", getName(), getValues().toString());
		}
	}
	
	public static class LdapUser {
		private final String dn;
		private final List<LdapAttribute> attributes = new LinkedList<>();
		private List<LdapAttribute> unmodifiableAttributes = null;
		
		private LdapUser(String dn, Attributes coreAttributes) throws NamingException {
			this.dn = dn;
			
			NamingEnumeration<? extends Attribute> attrs = coreAttributes.getAll();
			
			while(attrs.hasMoreElements()) {
				Attribute attr = attrs.next();
				this.attributes.add(new LdapAttribute(attr));
			}
		}
		
		public String getDn() {
			return dn;
		}
		
		public List<LdapAttribute> getAttributes() {
			if (unmodifiableAttributes == null) {
				unmodifiableAttributes = Collections.unmodifiableList(attributes);
			}
			
			return unmodifiableAttributes;
		}
	}
	
	public static final String KEY_LDAP_URL             = "agapsys.agrest.ldap.url";
	public static final String KEY_SEARCH_BASE_DN       = "agapsys.agrest.ldap.baseDn";
	public static final String KEY_SEARCH_PATTERN       = "agapsys.agrest.ldap.searchPattern";
	public static final String KEY_SEARCH_USER_DN       = "agapsys.agrest.ldap.searchUserDn";
	public static final String KEY_SEARCH_USER_PASSWORD = "agapsys.agrest.ldap.searchUserPassword";
	
	private static final String DEFAULT_LDAP_URL             = "ldaps://ldap.server:9876";
	private static final String DEFAULT_SEARCH_BASE_DN       = "ou=users,dc=ldap,dc=server";
	private static final String DEFAULT_SEARCH_PATTERN       = "(&(objectClass=uidObject)(uid=%s))";
	private static final String DEFAULT_SEARCH_USER_DN       = "cn=admin,dc=ldap,dc=sever";
	private static final String DEFAULT_SEARCH_USER_PASSWORD = "password";
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private String ldapUrl            = null;
	private String searchBaseDn       = null;
	private String searchPattern      = null;
	private String searchUserDn       = null;
	private String searchUserPassword = null;
	
	@Override
	public Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		
		defaultProperties.setProperty(KEY_LDAP_URL,             DEFAULT_LDAP_URL);
		defaultProperties.setProperty(KEY_SEARCH_BASE_DN,       DEFAULT_SEARCH_BASE_DN);
		defaultProperties.setProperty(KEY_SEARCH_PATTERN,       DEFAULT_SEARCH_PATTERN);
		defaultProperties.setProperty(KEY_SEARCH_USER_DN,       DEFAULT_SEARCH_USER_DN);
		defaultProperties.setProperty(KEY_SEARCH_USER_PASSWORD, DEFAULT_SEARCH_USER_PASSWORD);
		
		return defaultProperties;
	}
	
	@Override
	protected void onStart(AbstractWebApplication webApp) {
		Properties appProperties = webApp.getProperties();
		
		ldapUrl            = appProperties.getProperty(KEY_LDAP_URL,             DEFAULT_LDAP_URL);
		searchBaseDn       = appProperties.getProperty(KEY_SEARCH_BASE_DN,       DEFAULT_SEARCH_BASE_DN);
		searchPattern      = appProperties.getProperty(KEY_SEARCH_PATTERN,       DEFAULT_SEARCH_PATTERN);
		searchUserDn       = appProperties.getProperty(KEY_SEARCH_USER_DN,       DEFAULT_SEARCH_USER_DN);
		searchUserPassword = appProperties.getProperty(KEY_SEARCH_USER_PASSWORD, DEFAULT_SEARCH_USER_PASSWORD);
	}
	
	@Override
	protected void onStop() {
		ldapUrl            = null;
		searchBaseDn       = null;
		searchPattern      = null;
		searchUserDn       = null;
		searchUserPassword = null;
	}
	

	protected String getLdapUrl() {
		return ldapUrl;
	}
	
	protected String getSearchBaseDn() {
		return searchBaseDn;
	}
	
	protected String getSearchPattern() {
		return searchPattern;
	}
	
	protected String getSearchUserDn() {
		return searchUserDn;
	}
	
	protected String getSearchUserPassword() {
		return searchUserPassword;
	}
	

	private DirContext getContext(String url, String userDn, String password) throws LdapException {
		Properties properties;
		
		properties = new Properties();
		
		properties.setProperty(Context.PROVIDER_URL,            url);
		properties.setProperty(Context.SECURITY_PRINCIPAL,      userDn);
		properties.setProperty(Context.SECURITY_CREDENTIALS,    password);
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.setProperty(Context.URL_PKG_PREFIXES,        "com.sun.jndi.url");
		properties.setProperty(Context.REFERRAL,                "ignore");
		properties.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
		
		try {
			return new InitialDirContext(properties);	
		} catch (AuthenticationException ex) {
			throw new LdapException(LdapExceptionType.INVALID_CREDENTIALS, String.format("Invalid credentials for %s", userDn), ex);
		} catch (AuthenticationNotSupportedException ex) {
			throw new LdapException(LdapExceptionType.AUTHENTICATION_NOT_SUPPORTED, "Authentication not supported", ex);
		} catch (CommunicationException ex) {
			throw new LdapException(LdapExceptionType.COMMUNICATION_FAILURE, "Communication failure", ex);
		} catch (NamingException ex) {
			throw new LdapException(LdapExceptionType.NAMING_ERROR, ex);
		}
	}
		
	private SearchResult searchUser(DirContext ctx, String searchBase, String searchPattern, String userId) throws LdapException {
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<SearchResult> results = ctx.search(
				searchBase, 
				String.format(searchPattern, userId),
				constraints
			);

			if (results.hasMoreElements()) {
				SearchResult sr = (SearchResult) results.next();
				return sr;
			} else {
				return null;
			}
		} catch (NamingException ex) {
			throw new LdapException(LdapExceptionType.NAMING_ERROR, ex);
		}
	}
	
	private LdapUser _getUser(String userId, String password) throws LdapException, NamingException {
		DirContext ctx;
		SearchResult searchResult;
		String userDn = null;

		ctx = getContext(getLdapUrl(), getSearchUserDn(), getSearchUserPassword());
		searchResult = searchUser(ctx, getSearchBaseDn(), getSearchPattern(), userId);
		
		boolean found;
		if (searchResult != null) {
			userDn = searchResult.getNameInNamespace();
			found = true;
		} else {
			found = false;
		}
		
		ctx.close();
		ctx = null;
		
		if (found) {
			// Once a user is found, try to authenticate it
			try {
				ctx = getContext(getLdapUrl(), userDn, password);
				return new LdapUser(userDn, ctx.getAttributes(userDn));
			} catch (LdapException ex) {
				if (ex.getExceptionType() == LdapExceptionType.INVALID_CREDENTIALS) return null;
				throw ex;
			} finally {
				if (ctx != null) ctx.close();
			}
		} else {
			return null;
		}
	}
	
	public final LdapUser getUser(String userId, String password) throws LdapException {
		if (!isRunning()) throw new RuntimeException("Module is not running");
		
		try {
			return _getUser(userId, password);
		} catch (NamingException ex) {
			throw new LdapException(LdapExceptionType.NAMING_ERROR, ex);
		}
	}
	// =========================================================================
}
