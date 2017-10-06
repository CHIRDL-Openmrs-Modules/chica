package org.openmrs.module.chica.study.dp3.login;

public class LoginResponse
{
	private User user;
	private String token;
	
	public LoginResponse()
	{
		
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String getToken()
	{
		return token;
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
		
		public User()
		{
			
		}
		
		public String getUsername()
		{
			return username;
		}
		
		public String[] getScope()
		{
			return scope;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public void setUsername(String username)
		{
			this.username = username;
		}
		
		public void setScope(String[] scope)
		{
			this.scope = scope;
		}
		
		public void setKey(String key)
		{
			this.key = key;
		}
	}
}
