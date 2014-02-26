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

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.ReadWriteManager;

/**
 * @author msheley
 * This class maintains a list by vaccine name of immunization lot numbers
 */
public class ImmunizationLotNumberLookup {
	
	private static Log log = LogFactory.getLog(ImmunizationForecastLookup.class);
	
	private static Hashtable<Integer, ImmunizationLotNumberOutput> lotNumberLists = 
		new Hashtable<Integer, ImmunizationLotNumberOutput>();
	private static ReadWriteManager lotNumberLock = new ReadWriteManager();
	
	/**
	 * add a list of of lot numbers for a vaccine
	 */
	public static synchronized void addLotNumberList(Integer locationId, 
			ImmunizationLotNumberOutput lotNumberOutput) {
		lotNumberLock.getWriteLock();
		try {
			lotNumberLists.put(locationId, lotNumberOutput);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	lotNumberLock.releaseWriteLock();
        }
	}
	
	/**
	 * get the forecasted immunization list for a given patient
	 */
	public static synchronized ImmunizationLotNumberOutput getLotNumberList(Integer locationId) {
		lotNumberLock.getReadLock();
		    
        try {
        	ImmunizationLotNumberOutput lotNumberOutput = lotNumberLists.get(locationId);
 	        if(lotNumberOutput == null){
 	        	return null;
 	        }
	        
	        return lotNumberOutput;
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	lotNumberLock.releaseReadLock();
        }
		return null;
	}
	
	/**
	 * remove the forecasted immunization list for a given patient
	 */
	public static void removeImmunizationList(Integer patientId) {
		lotNumberLock.getWriteLock();
		try {
			lotNumberLists.remove(patientId);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	lotNumberLock.releaseWriteLock();
        }
	}
	
	
	
	/**
	 * clear the entire forecasted immunization list
	 */
	public static void clearimmunizationLists() {
	    if(lotNumberLists != null && !lotNumberLists.isEmpty())  {
	        log.info("Before clearing immunizationList cache, No. of elements" + lotNumberLists.size());
	        lotNumberLists.clear();
	        log.info("After clearing immunizationList cache, No. of elements" + lotNumberLists.size());
	    }
	}
	
}