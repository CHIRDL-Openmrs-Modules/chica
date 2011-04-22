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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.advice.QueryImmunizationForecast;
import org.openmrs.module.chirdlutil.ReadWriteManager;
import org.openmrs.module.rgccd.Immunization;

/**
 * @author tmdugan
 * This class maintains a list by patient of forecasted immunizations
 */
public class ImmunizationForecastLookup {
	
	private static Log log = LogFactory.getLog(ImmunizationForecastLookup.class);
	
	private static Hashtable<Integer, List<Immunization>> immunizationLists = new Hashtable<Integer, List<Immunization>>();
	private static ReadWriteManager immunizationListsLock = new ReadWriteManager();
	
	/**
	 * add a list of forecasted immunizations for a patient
	 */
	public static synchronized void addImmunizationList(Integer patientId, List<Immunization> immunizationList) {
		immunizationListsLock.getWriteLock();
		try {
			immunizationLists.put(patientId, immunizationList);
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
	public static synchronized LinkedList<Immunization> getImmunizationList(Integer patientId) {
		immunizationListsLock.getReadLock();
		LinkedList<Immunization> immunizations = new LinkedList<Immunization>();
        try {
	        List<Immunization> immunizationList = immunizationLists.get(patientId);
	        if(immunizationList == null){
	        	return null;
	        }
	        
	        for(Immunization currImmunization:immunizationList){
	        	immunizations.add(new Immunization(currImmunization));
	        }
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	immunizationListsLock.releaseReadLock();
        }
		return immunizations;
	}
	
	/**
	 * remove the forecasted immunization list for a given patient
	 */
	public static synchronized void removeImmunizationList(Integer patientId) {
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
	public static void queryImmunizationList(Encounter encounter, boolean useTimeout,String queryInputFilename) throws QueryImmunizationsException {
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		Integer timeout = null;
		
		try {
			if (useTimeout) {
				timeout = Integer.parseInt(adminService.getGlobalProperty("chica.immunizationListTimeout"));
				timeout = timeout * 1000; // convert seconds to
				// milliseconds
			}
		}
		catch (NumberFormatException e) {}
		QueryImmunizationForecast queryImmunizationsThread = new QueryImmunizationForecast(encounter,queryInputFilename);
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
					ATDError error = new ATDError("Warning", "Query Immunization List Connection", 
						"Timeout of "+timeout/1000+" seconds was exceeded for patientId: "+
						encounter.getPatientId()+"."
						, null, new Date(), null);
					atdService.saveError(error);
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