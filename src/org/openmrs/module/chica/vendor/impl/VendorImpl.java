/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.vendor.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;


/**
 *
 * @author Steve McKee
 */
public class VendorImpl implements Vendor {
	
	private static Log log = LogFactory.getLog(VendorImpl.class);
	
	protected static final String PARAM_MRN = "mrn";
	protected static final String PARAM_FORM_PAGE = "formPage";
	protected static final String PARAM_PROVIDER_ID = "providerId";
	protected static final String PARAM_END_STATE = "endState";
	protected static final String PARAM_START_STATE = "startState";
	protected static final String PARAM_FORM_NAME = "formName";
	protected static final String PARAM_PASSWORD = "password";
	protected static final String PARAM_USERNAME = "username";
	
	protected HttpServletRequest request = null;
	
	/**
	 * Constructor method
	 * 
	 * @param request HttpServletRequest object for accessing URL parameters.
	 */
	public VendorImpl(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Parameter request cannot be null.");
		}
		
		this.request = request;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getFormName()
	 */
	public String getFormName() {
		return request.getParameter(PARAM_FORM_NAME);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getStartState()
	 */
	public String getStartState() {
		return request.getParameter(PARAM_START_STATE);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getEndState()
	 */
	public String getEndState() {
		return request.getParameter(PARAM_END_STATE);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getProviderId()
	 */
	public String getProviderId() {
		// DWE CHICA-861 Trim leading and trailing white space, we found that the url contained a space in this parameter
		return removeWhiteSpaceFromParameter(PARAM_PROVIDER_ID);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getFormPage()
	 */
	public String getFormPage() {
		return request.getParameter(PARAM_FORM_PAGE);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getMrn()
	 */
	public String getMrn() {
		return request.getParameter(PARAM_MRN);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getPassword()
	 */
	public String getPassword() {
		String password = request.getParameter(PARAM_PASSWORD);
		if (password == null || password.trim().length() == 0) {
			log.error("No " + PARAM_PASSWORD + " parameter found in HTTP request.");
			return null;
		}
		
		String key = getEncryptionKey();
		if (key == null) {
			return password;
		}
		
		return Util.decryptValue(password, true, key);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getUsername()
	 */
	public String getUsername() {
		String username = request.getParameter(PARAM_USERNAME);
		if (username == null || username.trim().length() == 0) {
			log.error("No " + PARAM_USERNAME + " parameter found in HTTP request.");
			return null;
		}
		
		String key = getEncryptionKey();
		if (key == null) {
			return username;
		}
		
		return Util.decryptValue(username, true, key);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getEncryptionKey()
	 */
	public String getEncryptionKey() {
		String key = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_ENCRYPTION_KEY);
		if (key == null || key.trim().length() == 0) {
			log.warn("Cannot find value for global property " + ChirdlUtilConstants.GLOBAL_PROP_ENCRYPTION_KEY + ".  Clear text "
					+ "value will be used.");
			return null;
		}
		
		return key;
	}
	
	/**
	 * Removes whitespace from the parameter value
	 * @param parameterName
	 * @return
	 */
	public String removeWhiteSpaceFromParameter(String parameterName)
	{
		String paramValue = request.getParameter(parameterName);
		
		if(paramValue != null && paramValue.length() > 0)
		{
			paramValue = paramValue.trim();
		}
		
		return paramValue;
	}
}
