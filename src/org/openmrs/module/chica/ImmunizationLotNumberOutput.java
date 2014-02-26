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
public class ImmunizationLotNumberOutput {
	private Log log = LogFactory.getLog(this.getClass());


	private HashMap<String,HashMap<String,ImmunizationLotNumber>> lotNumberOutput = null;
	//string (vaccine name), <string (lot number), LotNumber class>
	
	public ImmunizationLotNumberOutput(){
			this.lotNumberOutput = new HashMap<String,HashMap<String,ImmunizationLotNumber>>();
	}
	
    /**
     * @return the immunizationPrevious
     */
  //  public HashMap<String,HashMap<Integer,ImmunizationPrevious>> getImmunizationPrevious() {
    //	return this.immunizationPrevious;
   // }
    
    /**
     * @param immunizationPrevious the immunizationPrevious to set
     */
    public void addImmunizationLotNumbers(ImmunizationLotNumber immunizationLotNumber) {
    	ChicaService chicaService = Context.getService(ChicaService.class);
    	
    	
    	String vaccineName = immunizationLotNumber.getVaccineName();
    	String lotNumber = immunizationLotNumber.getLotNumber();
    
    	
    	HashMap<String,ImmunizationLotNumber> lotNumbersForVaccine = this.lotNumberOutput.get(vaccineName);
    	
    	if(lotNumbersForVaccine == null)
    	{
    		lotNumbersForVaccine = new HashMap<String ,ImmunizationLotNumber>();
    		
    		lotNumberOutput.put(vaccineName,lotNumbersForVaccine);

    	}
    	
    	lotNumbersForVaccine.put(lotNumber, immunizationLotNumber);
     	List<ImmunizationLotNumber> list 
		= new ArrayList<ImmunizationLotNumber> ( lotNumbersForVaccine.values());
		
     	lotNumbersForVaccine.clear();
		try {
			Collections.sort(list, new Comparator<ImmunizationLotNumber>() {
				public int compare(ImmunizationLotNumber i1, ImmunizationLotNumber i2) {
					Date date1 = i1.getExpirationDate();
					Date date2 = i2.getExpirationDate();
					return date1.compareTo(date2); //ascending order;
					//return date2.compareTo(date1); // descending order
				}		
			});
		} catch (Exception e) {
			log.error("Sort exception for immunization list", e);
		}
		
		for (ImmunizationLotNumber eachLotNumber : list){
			lotNumbersForVaccine.put(vaccineName,eachLotNumber);
		}
		
    }
    
    /**
     * @return the immunizationPrevious
     */
    public HashMap<String,HashMap<String,ImmunizationLotNumber>> getImmunizationLotNumbers() {
    	return this.lotNumberOutput;
    }
    
    
	
}
