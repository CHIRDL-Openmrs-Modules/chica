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
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.advice.QueryImmunizationForecast;
import org.openmrs.module.chirdlutil.ReadWriteManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * This class maintains a list by patient of forecasted immunizations
 */
public class ImmunizationForecastLookup {
	
	private static Log log = LogFactory.getLog(ImmunizationForecastLookup.class);
	//Hashtable < [patient id], ImmunizationQueryOutput>
	private static Hashtable<Integer, ImmunizationQueryOutput> immunizationLists = new Hashtable<Integer, ImmunizationQueryOutput>();
	private static ReadWriteManager immunizationListsLock = new ReadWriteManager();
	
	/**
	 * add a list of forecasted immunizations for a patient
	 */
	public static synchronized void addImmunizationList(Integer patientId, ImmunizationQueryOutput immunization) {
		immunizationListsLock.getWriteLock();
		try {
			immunizationLists.put(patientId, immunization);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	immunizationListsLock.releaseWriteLock();
        }
	}
	
	/**
	 * get the forecasted immunization list for a given patient
	 */
	public static synchronized ImmunizationQueryOutput getImmunizationList(Integer patientId) {
 		immunizationListsLock.getReadLock();
		LinkedList<ImmunizationForecast> immunizations = new LinkedList<ImmunizationForecast>();
        try {
	        ImmunizationQueryOutput immunizationList = immunizationLists.get(patientId);
 	        if(immunizationList == null){
 	        	return null;
 	        }
	        
	        return immunizationList;
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	immunizationListsLock.releaseReadLock();
        }
		return null;
	}
	
	/**
	 * remove the forecasted immunization list for a given patient
	 */
	public static void removeImmunizationList(Integer patientId) {
		immunizationListsLock.getWriteLock();
		try {
	        immunizationLists.remove(patientId);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	immunizationListsLock.releaseWriteLock();
        }
	}
	
	/**
	 * create a thread with a timeout to query the immunization forecasting service
	 */
	public static void queryImmunizationList(Encounter encounter, boolean useTimeout) throws QueryImmunizationsException {
		AdministrationService adminService = Context.getAdministrationService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		org.openmrs.module.chica.service.EncounterService encounterService = Context
		.getService(org.openmrs.module.chica.service.EncounterService.class);
		
		Integer timeout = null;
		
		Patient patient = encounter.getPatient();
		String queryOn = adminService.getGlobalProperty("chica.ImmunizationQueryActivated");
		if (queryOn.equalsIgnoreCase("true")){
		
			try {
				if (useTimeout) {
					timeout = Integer.parseInt(adminService.getGlobalProperty("chica.immunizationListTimeout"));
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
						Error error = new Error("Warning", "Query Immunization List Connection", 
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
	    if(immunizationLists != null && !immunizationLists.isEmpty())  {
	        log.info("Before clearing immunizationList cache, No. of elements" + immunizationLists.size());
	        immunizationLists.clear();
	        log.info("After clearing immunizationList cache, No. of elements" + immunizationLists.size());
	    }
	}
	
}