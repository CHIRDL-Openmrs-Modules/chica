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
package org.openmrs.module.chica.web;


/**
 *
 */
public class FormDisplay implements Comparable{
	
	private String formName = null;
	private String displayName = null;
	private Integer formId = null;
	private String displayFrmGp = null;
	
    /**
     * @return the formName
     */
    public String getFormName() {
    	return this.formName;
    }
	
    /**
     * @param formName the formName to set
     */
    public void setFormName(String formName) {
    	this.formName = formName;
    }
	
    /**
     * @return the displayName
     */
    public String getDisplayName() {
    	return this.displayName;
    }
	
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
    	this.displayName = displayName;
    }

	/**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * sort by display name
     */
    public int compareTo(Object arg0) {
    	
    	return this.displayName.compareTo(((FormDisplay) arg0).displayName);
	   
    }
    
    /**
     * @return the formId
     */
    public Integer getFormId() {
    	return this.formId;
    }

	
    /**
     * @param formId the formId to set
     */
    public void setFormId(Integer formId) {
    	this.formId = formId;
    }
    

    /**
     * @return the displayFrmGp
     */
    public String getDisplayFrmGp() {
    	return this.displayFrmGp;
    }
	
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayFrmGp(String displayFrmGp) {
    	this.displayFrmGp = displayFrmGp;
    }
}
