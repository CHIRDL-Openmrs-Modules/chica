package org.openmrs.module.chica.study.dp3.login;

import java.util.Arrays;

/**
 * @author davely
 * CHICA-1029 Object that represents a login response from Glooko
 */
public class LoginResponse
{
	private User user;
	private String token;
	
	/**
	 * Default constructor
	 */
	public LoginResponse()
	{
		// This constructor is intentionally left empty.
	}
	
	/**
	 * @return user
	 */
	public User getUser()
	{
		return this.user;
	}
	
	/**
	 * @return token
	 */
	public String getToken()
	{
		return this.token;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LoginResponse)) {
			return false;
		}
		LoginResponse other = (LoginResponse) obj;
		if (this.token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!this.token.equals(other.token)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		return true;
	}

	/**
	 * Private User class for use during Glooko login
	 * The response from Glooko includes a User object
	 * and a String token
	 */
	private class User
	{
		private String username;
		private String[] scope;
		private String key;
		
		/**
		 * Default constructor
		 */
		public User()
		{
			// This constructor is intentionally left empty.
		}
		
		/**
		 * @return username
		 */
		public String getUsername()
		{
			return this.username;
		}
		
		/**
		 * @return scope
		 */
		public String[] getScope()
		{
			return this.scope;
		}
		
		/**
		 * @return key
		 */
		public String getKey()
		{
			return this.key;
		}
		
		/**
		 * @param username - username to set
		 */
		public void setUsername(String username)
		{
			this.username = username;
		}
		
		/**
		 * @param scope - scope to set
		 */
		public void setScope(String[] scope)
		{
			this.scope = scope;
		}
		
		/**
		 * @param key - key to set
		 */
		public void setKey(String key)
		{
			this.key = key;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + Arrays.hashCode(scope);
			result = prime * result + ((this.username == null) ? 0 : username.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof User)) {
				return false;
			}
			User other = (User) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!this.key.equals(other.key)) {
				return false;
			}
			if (!Arrays.equals(scope, other.scope)) {
				return false;
			}
			if (this.username == null) {
				if (other.username != null) {
					return false;
				}
			} else if (!this.username.equals(other.username)) {
				return false;
			}
			return true;
		}

		private LoginResponse getOuterType() {
			return LoginResponse.this;
		}
	}
}
