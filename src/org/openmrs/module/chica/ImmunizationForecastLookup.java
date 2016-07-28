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

import java.util.Date;

import javax.cache.Cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.advice.QueryImmunizationForecast;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * This class maintains a list by patient of forecasted immunizations
 */
public class ImmunizationForecastLookup {
	
	private static Log log = LogFactory.getLog(ImmunizationForecastLookup.class);
	
	/**
	 * add a list of forecasted immunizations for a patient
	 */
	public static synchronized void addImmunizationList(Integer patientId, ImmunizationQueryOutput immunization) {
		try {
			Cache<Integer, ImmunizationQueryOutput> immunizationCache = getCache();
			if (immunizationCache != null) {
				immunizationCache.put(patientId, immunization);
			}
        }
        catch (Exception e) {
	        log.error("Error adding immunization entry", e);
        }
	}
	
	/**
	 * Get the forecasted immunization list for a given patient.  If any changes are made to the ImmunizationQueryOutput object, 
	 * the addImmunizationList method must be called to persist the changes.
	 */
	public static synchronized ImmunizationQueryOutput getImmunizationList(Integer patientId) {
        try {
        	Cache<Integer, ImmunizationQueryOutput> immunizationCache = getCache();
 	        if(immunizationCache == null){
 	        	return null;
 	        }
	        
	        return immunizationCache.get(patientId);
        }
        catch (Exception e) {
	        log.error("Error retrieving immunization entry for patient: " + patientId, e);
        }
        
		return null;
	}
	
	/**
	 * remove the forecasted immunization list for a given patient
	 */
	public static void removeImmunizationList(Integer patientId) {
		try {
			Cache<Integer, ImmunizationQueryOutput> immunizationCache = getCache();
			if (immunizationCache != null) {
				immunizationCache.remove(patientId);
			}
        }
        catch (Exception e) {
	        log.error("Error removing immunization entry for patient: " + patientId, e);
        }
	}
	
	/**
	 * create a thread with a timeout to query the immunization forecasting service
	 */
	public static void queryImmunizationList(Encounter encounter, boolean useTimeout) throws QueryImmunizationsException {
		AdministrationService adminService = Context.getAdministrationService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		Integer timeout = null;
		
		String queryOn = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_IMMUNIZATION_QUERY_ACTIVATED);
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(queryOn)){
		
			try {
				if (useTimeout) {
					timeout = Integer.parseInt(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_IMMUNIZATION_LIST_TIMEOUT));
					timeout = timeout * 1000; // convert seconds to
					// milliseconds
				}
			}
			catch (NumberFormatException e) {};
			
			QueryImmunizationForecast queryImmunizationsThread = new QueryImmunizationForecast(encounter);
			Thread thread = new Thread(queryImmunizationsThread);
			thread.start();
			long startTime = System.currentTimeMillis();
			
			if (timeout != null) {
				while (true) {
					//processing is done
					if (!thread.isAlive()) {
						//check for an exception
						if (queryImmunizationsThread.getException() != null) {
							throw queryImmunizationsThread.getException();
						} else {
							return;
						}
					}
					
					if ((System.currentTimeMillis() - startTime) > timeout) {
						//the timeout was exceeded so return null
						Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_WARNING, "Query Immunization List Connection", 
							"Timeout of "+timeout/1000+" seconds was exceeded for patientId: "+
							encounter.getPatientId()+"."
							, null, new Date(), null);
						chirdlutilbackportsService.saveError(error);
						return;
					}
					try {
						Thread.sleep(100);// wait for a tenth of a second
						
					}
					catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}
		
	}
	
	/**
	 * clear the entire forecasted immunization list
	 */
	public static void clearimmunizationLists() {
		Cache<Integer, ImmunizationQueryOutput> immunizationCache = getCache();
	    if(immunizationCache != null)  {
	        immunizationCache.clear();
	    }
	}
	
	/**
	 * Retrieves the cache for immunization
	 * 
	 * @return Cache object for immunization
	 */
    private static Cache<Integer, ImmunizationQueryOutput> getCache() {
    	ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		return cacheManager.getCache(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
    }
}