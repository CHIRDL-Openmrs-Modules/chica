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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;


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
	private static final char CHARACTER_SPACE = ' ';
	private static final String STRING_SPACE = " ";
	private static final String REPLACEMENT_VALUE_ZERO = "0";
	private String formName;
	private String formPage;
	private String startStateStr;
	private String endStateStr;
	
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
		return formName;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getStartState()
	 */
	public String getStartState() {
		return startStateStr;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getEndState()
	 */
	public String getEndState() {
		return endStateStr;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getProviderId()
	 */
	public String getProviderId() {
		// DWE CHICA-861 Trim leading and trailing white space, we found that the url contained a space in this parameter
		try
		{
			return removeLeadingTrailingSpacesAddLeadingZeros(PARAM_PROVIDER_ID);
		}
		catch(Exception e)
		{
			log.error("Error getting " + PARAM_PROVIDER_ID + " parameter in HTTP request.", e);
			return "";
		}
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getFormPage()
	 */
	public String getFormPage() {
		return formPage;
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
	 * DWE CHICA-861
	 * Removes the first whitespace and replaces all others with zeros
	 * Also removes trailing spaces, but leaves any spaces in the middle
	 * 
	 * The examples observed so far always contain atleast one space, followed by more spaces that should be zeros
	 * For example, "   1234" should be "001234" but comes across with 3 leading spaces. 
	 * The first is removed but the others are replaced by zeros. Also remove trailing spaces.
	 * If the parameter contains spaces in the middle of the value, they are left in place
	 * For example, "     1234   5678   " will become "00001234   5678"
	 * 
	 * @param parameterName
	 * @return
	 */
	public String removeLeadingTrailingSpacesAddLeadingZeros(String parameterName) throws Exception
	{
		String paramValue = request.getParameter(parameterName);
		
		if(paramValue != null && paramValue.length() > 0)
		{
			if(paramValue.charAt(0) == CHARACTER_SPACE) 
			{
				paramValue = paramValue.substring(1); // Remove the leading space, but leave the rest of the leading spaces
			}
			
			paramValue = StringUtils.stripEnd(paramValue, STRING_SPACE); // Remove all trailing spaces
			
			if(paramValue.length() > 0 && paramValue.charAt(0) == CHARACTER_SPACE)
			{
				// Replace all of the remaining leading spaces with zeros. This will leave any spaces in the middle as is
				Pattern p = Pattern.compile("^(\\s*)(.+)$");
				Matcher m = p.matcher(paramValue);

				if (m.matches()) 
				{
					paramValue = m.group(1).replaceAll(STRING_SPACE, REPLACEMENT_VALUE_ZERO) + m.group(2);
				}
			}
		}
		
		return paramValue;
	}
	
	/**
	 * Retrieves FormName, FormPage, StartState and EndState 
	 * @param encounter The patient's encounter.
	 */
	public void getURLAttributes(org.openmrs.module.chica.hibernateBeans.Encounter encounter) {
		
		String locationTagString = encounter.getPrinterLocation();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		LocationTag locationTag = null;
    	if (StringUtils.isNotBlank(locationTagString)) { 
    		LocationService locationService = Context.getLocationService();
    		locationTag = locationService.getLocationTagByName(locationTagString);
		}
    	
    	Location location = encounter.getLocation();
    	LocationTagAttributeValue locationTagAttributeValueForm = null;
    	
     	if (locationTag != null && location != null) {
     		locationTagAttributeValueForm = chirdlutilbackportsService.getLocationTagAttributeValue(locationTag.getLocationTagId(), 
        			ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PHYSICIAN_FORM,location.getLocationId());
     	}
    	Form form = null;
    	formName = null;
    	formPage = null;
    	startStateStr = null;
    	endStateStr = null;
    	
    	if (locationTagAttributeValueForm != null && !locationTagAttributeValueForm.equals("") ) {
    		formName = locationTagAttributeValueForm.getValue(); 
    		
    		form = Context.getFormService().getForm(formName);
    		
    		if (form != null && !form.equals("")) {
    			FormAttributeValue formAttributeValueURL = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_URL, locationTag.getLocationTagId(), location.getLocationId());
        		FormAttributeValue formAttributeValueStartState = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE, locationTag.getLocationTagId(), location.getLocationId());
        		FormAttributeValue formAttributeValueEndState = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_END_STATE, locationTag.getLocationTagId(), location.getLocationId());

        		if (formAttributeValueURL != null && !formAttributeValueURL.equals("") ) { 
        			formPage = formAttributeValueURL.getValue();
        		} 
        		if (formAttributeValueStartState != null && !formAttributeValueStartState.equals("") ) {
        			startStateStr = formAttributeValueStartState.getValue();
        		}
        		if (formAttributeValueEndState != null && !formAttributeValueEndState.equals("") ) {
        			endStateStr = formAttributeValueEndState.getValue();
        		}
    		}
    	} 
	}
	
}
