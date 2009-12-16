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
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.advice.QueryMeds;
import org.openmrs.module.chirdlutil.ReadWriteManager;
import org.openmrs.module.rgccd.Medication;

/**
 *
 */
public class MedicationListLookup {
	
	private static Log log = LogFactory.getLog(MedicationListLookup.class);
	
	private static Hashtable<Integer, List<Medication>> medicationLists = new Hashtable<Integer, List<Medication>>();
	private static ReadWriteManager medicationListsLock = new ReadWriteManager();
	
	
	public static synchronized void addMedicationList(Integer patientId, List<Medication> medicationList) {
		medicationListsLock.getWriteLock();
		try {
	        medicationLists.put(patientId, medicationList);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	medicationListsLock.releaseWriteLock();
        }
	}
	
	public static synchronized List<Medication> getMedicationList(Integer patientId) {
		medicationListsLock.getReadLock();
		List<Medication> medications = new ArrayList<Medication>();
        try {
	        List<Medication> medicationList = medicationLists.get(patientId);
	        if(medicationList == null){
	        	return null;
	        }
	        
	        for(Medication currMed:medicationList){
	        	medications.add(new Medication(currMed));
	        }
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	medicationListsLock.releaseReadLock();
        }
		return medications;
	}
	
	public static synchronized void removeMedicationList(Integer patientId) {
		medicationListsLock.getWriteLock();
		try {
	        medicationLists.remove(patientId);
        }
        catch (Exception e) {
	        log.error("",e);
        }finally{
        	medicationListsLock.releaseWriteLock();
        }
	}
	
	public static void queryMedicationList(Encounter encounter, boolean useTimeout) throws QueryMedicationListException {
		AdministrationService adminService = Context.getAdministrationService();
		Integer timeout = null;
		
		try {
			if (useTimeout) {
				timeout = Integer.parseInt(adminService.getGlobalProperty("chica.medicationListTimeout"));
				timeout = timeout * 1000; // convert seconds to
				// milliseconds
			}
		}
		catch (NumberFormatException e) {}
		QueryMeds queryMedsThread = new QueryMeds(encounter);
		Thread thread = new Thread(queryMedsThread);
		thread.start();
		long startTime = System.currentTimeMillis();
		
		if (timeout != null) {
			while (true) {
				//processing is done
				if (!thread.isAlive()) {
					//check for an exception
					if (queryMedsThread.getException() != null) {
						throw queryMedsThread.getException();
					} else {
						return;
					}
				}
				
				if ((System.currentTimeMillis() - startTime) > timeout) {
					//the timeout was exceeded so return null
					log.warn("Timeout exceeded.");
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
