package org.openmrs.module.chica.study.dp3.login;

/**
 * @author davely
 * CHICA-1029 object that will be converted to json and sent in the post body to login to Glooko
 */
public class LoginCredentials
{
	private String username;
	private String password;
	
	/**
	 * Constructor
	 * @param username
	 * @param password
	 */
	public LoginCredentials(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	/**
	 * @return username
	 */
	public String getUsername()
	{
		return this.username;
	}
	
	/**
	 * @return password
	 */
	public String getPassword()
	{
		return this.password;
	}
	
	/**
	 * @param username - the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	/**
	 * @param password - the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
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
		if (!(obj instanceof LoginCredentials)) {
			return false;
		}
		LoginCredentials other = (LoginCredentials) obj;
		if (this.password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!this.password.equals(other.password)) {
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
}
