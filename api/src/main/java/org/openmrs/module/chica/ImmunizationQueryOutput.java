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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.ChicaService;


/**
 *
 */
public class ImmunizationQueryOutput implements Serializable {

    private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(this.getClass());

	private HashMap<String,ImmunizationForecast> immunizationForecast = null;
	private HashMap<String,HashMap<Integer,ImmunizationPrevious>> immunizationPrevious = null;
	
	public ImmunizationQueryOutput(){
		
		//HashMap <[vaccine name string], ImmunizationForecast>
		this.immunizationForecast = new HashMap<String,ImmunizationForecast>();
		
		//HashMap < [vaccine name string], HashMap < [dose #], ImmunizationPrevious >>
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
    	ChicaService chicaService = Context.getService(ChicaService.class);
    	
    	
    	String vaccineName = immunizationPrevious.getVaccineName();
    
    	
    	HashMap<Integer,ImmunizationPrevious> prevImmunizations = this.immunizationPrevious.get(vaccineName);
    	
    	if(prevImmunizations == null)
    	{
    		prevImmunizations = new HashMap<Integer,ImmunizationPrevious>();
    		
    		this.immunizationPrevious.put(immunizationPrevious.getVaccineName(),prevImmunizations);

    	}
    	
     	prevImmunizations.put(immunizationPrevious.getDose(), immunizationPrevious);
		List<ImmunizationPrevious> list 
			= new ArrayList<ImmunizationPrevious> ( prevImmunizations.values());
		prevImmunizations.clear();
		try {
			Collections.sort(list, new Comparator<ImmunizationPrevious>() {
				public int compare(ImmunizationPrevious i1, ImmunizationPrevious i2) {
					Date date1 = i1.getDate();
					Date date2 = i2.getDate();
					//return date1.compareTo(date2); //ascending order;
					return date2.compareTo(date1); // descending order
				}		
			});
		} catch (Exception e) {
			log.error("Sort exception for immunization list", e);
		}
		
		// insert sorted values - remove the duplicate dates for this vaccine
		int queryDoseCount = list.size();
		int numFields = chicaService.getMergeFieldCount("ImmunizationSchedule7yrOrOlder",vaccineName);
		int maxDoseNumber  = (numFields > queryDoseCount)? queryDoseCount :  numFields;
		int dose = maxDoseNumber;
		ImmunizationPrevious prev = null;
		for (ImmunizationPrevious ip : list){
			
			if (prev == null || !ip.getDate().equals(prev.getDate())){
				prevImmunizations.put(dose, ip);
				prev = prevImmunizations.get(dose);
				dose--;
				if (dose == 0){
					break;
				}
			}
		}
	
		
    }
    
    
	
}
