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

import org.openmrs.module.chica.vendor.Vendor;


/**
 *
 * @author Steve McKee
 */
public abstract class VendorImpl implements Vendor {
	
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
	 * @see org.openmrs.module.chica.vendor.Vendor#getUsername()
	 */
	public String getUsername() {
		return request.getParameter(PARAM_USERNAME);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getPassword()
	 */
	public String getPassword() {
		return request.getParameter(PARAM_PASSWORD);
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
		return request.getParameter(PARAM_PROVIDER_ID);
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
}