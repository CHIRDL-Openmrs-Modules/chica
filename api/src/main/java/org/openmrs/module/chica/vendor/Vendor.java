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
package org.openmrs.module.chica.vendor;


/**
 * Vendor interface for receiving data from external sources.
 * 
 * @author Steve McKee
 */
public interface Vendor {
	
	/**
	 * Returns the username parameter.
	 * 
	 * @return The username.
	 */
	public String getUsername();
	
	/**
	 * Returns the password parameter.
	 * 
	 * @return The password.
	 */
	public String getPassword();
	
	/**
	 * Returns the formName parameter.
	 * 
	 * @param locationId The encounter location identifier.
	 * @param locationTagId The encounter location identifier.
	 * @return The form name.
	 */
	public String getFormName(Integer locationId, Integer locationTagId);
	
	/**
	 * Returns the startState parameter.
	 * 
	 * @param locationId The encounter location identifier.
	 * @param locationTagId The encounter location identifier.
	 * @param formName The name of the form being accessed.
	 * @return The start state.
	 */
	public String getStartState(Integer locationId, Integer locationTagId, String formName);
	
	/**
	 * Returns the endState parameter.
	 * 
	 * @param locationId The encounter location identifier.
	 * @param locationTagId The encounter location identifier.
	 * @param formName The name of the form being accessed.
	 * @return The end state.
	 */
	public String getEndState(Integer locationId, Integer locationTagId, String formName);
	
	/**
	 * Returns the providerId parameter.
	 * 
	 * @return The provider ID.
	 */
	public String getProviderId();
	
	/**
	 * Returns the formPage parameter.
	 * 
	 * @param locationId The encounter location identifier.
	 * @param locationTagId The encounter location identifier.
	 * @param formName The name of the form being accessed.
	 * @return The form page.
	 */
	public String getFormPage(Integer locationId, Integer locationTagId, String formName);
	
	/**
	 * Returns the mrn parameter.
	 * 
	 * @return The MRN.
	 */
	public String getMrn();
	
	/**
	 * Returns the encryption key for the vendor.
	 * 
	 * @return The encryption key
	 */
	public String getEncryptionKey();
}
