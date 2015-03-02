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
 * Cerner vendor implementation for receiving data from external sources.
 * 
 * @author Steve McKee
 */
public class CernerVendor implements Vendor {
	
	private HttpServletRequest request = null;
	
	/**
	 * Constructor method
	 * 
	 * @param request HttpServletRequest object for accessing URL parameters.
	 */
	public CernerVendor(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Parameter request cannot be null.");
		}
		
		this.request = request;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getUsername()
	 */
	public String getUsername() {
		return request.getParameter("username");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getPassword()
	 */
	public String getPassword() {
		return request.getParameter("password");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getFormName()
	 */
	public String getFormName() {
		return request.getParameter("formName");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getStartState()
	 */
	public String getStartState() {
		return request.getParameter("startState");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getEndState()
	 */
	public String getEndState() {
		return request.getParameter("endState");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getProviderId()
	 */
	public String getProviderId() {
		return request.getParameter("providerId");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getFormPage()
	 */
	public String getFormPage() {
		return request.getParameter("formPage");
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getMrn()
	 */
	public String getMrn() {
		return request.getParameter("mrn");
	}
}
