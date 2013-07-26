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
package org.openmrs.module.chica;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class ImmunizationQueryOutput {
	private Log log = LogFactory.getLog(this.getClass());

	private HashMap<String,ImmunizationForecast> immunizationForecast = null;
	private HashMap<String,HashMap<Integer,ImmunizationPrevious>> immunizationPrevious = null;
	
	public ImmunizationQueryOutput(){
		this.immunizationForecast = new HashMap<String,ImmunizationForecast>();
		this.immunizationPrevious = new HashMap<String,HashMap<Integer,ImmunizationPrevious>>();
	}
	
    /**
     * @return the immunizationForecast
     */
    public HashMap<String,ImmunizationForecast> getImmunizationForecast() {
    	return this.immunizationForecast;
    }

	
    /**
     * @param immunizationForecast the immunizationForecast to set
     */
    public void addImmunizationForecast(ImmunizationForecast immunizationForecast) {
    	this.immunizationForecast.put(immunizationForecast.getVaccineName(),immunizationForecast);
    }

	
    /**
     * @return the immunizationPrevious
     */
    public HashMap<String,HashMap<Integer,ImmunizationPrevious>> getImmunizationPrevious() {
    	return this.immunizationPrevious;
    }
    
    /**
     * @param immunizationPrevious the immunizationPrevious to set
     */
    public void addImmunizationPrevious(ImmunizationPrevious immunizationPrevious) {
    	
    	String vaccineName = immunizationPrevious.getVaccineName();
    	
    	HashMap<Integer,ImmunizationPrevious> prevImmunizations = this.immunizationPrevious.get(vaccineName);
    	
    	if(prevImmunizations == null)
    	{
    		prevImmunizations = new HashMap<Integer,ImmunizationPrevious>();
    		this.immunizationPrevious.put(immunizationPrevious.getVaccineName(),prevImmunizations);

    	}
		prevImmunizations.put(immunizationPrevious.getDose(), immunizationPrevious);
    }
	
}
