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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.advice.QueryMeds;
import org.openmrs.module.chica.xmlBeans.UnfilteredMedications;
import org.openmrs.module.chirdlutil.ReadWriteManager;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.rgccd.Medication;
import org.openmrs.module.rgccd.MedicationListComparator;

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
		ATDService atdService = Context.getService(ATDService.class);
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
					//the timeout was exceeded so return null
					ATDError error = new ATDError("Warning", "Query Medication List Connection", 
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
	
	public static void filterMedListByDate(List<Medication> medicationList, 
	                                       Integer numMonths,Integer locationTagId,
	                                       Integer locationId) {
		
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		LocationTagAttributeValue locationTagAttrVal = chirdlUtilService.getLocationTagAttributeValue(locationTagId, "unfilteredMedicationFile",
	                                                              locationId);
		
		String medExceptionConfigFilename = null;
		
		if(locationTagAttrVal != null){
			medExceptionConfigFilename = locationTagAttrVal.getValue();
		}
		
		UnfilteredMedications unfilteredMedications = null;
		FileInputStream input;
		HashMap<String,org.openmrs.module.chica.xmlBeans.Medication> exceptionMeds = null;
		
		if (medExceptionConfigFilename != null) {
			try {
				input = new FileInputStream(medExceptionConfigFilename);
				
				try {
					unfilteredMedications = (UnfilteredMedications) XMLUtil.deserializeXML(
						UnfilteredMedications.class, input);
					
					exceptionMeds = new HashMap<String, org.openmrs.module.chica.xmlBeans.Medication>();
					
					for (org.openmrs.module.chica.xmlBeans.Medication medication : unfilteredMedications
					        .getUnfilteredMedications()) {
						exceptionMeds.put(medication.getName(), medication);
					}
				}
				catch (IOException e) {
					log.error(e.getMessage());
					log.error(Util.getStackTrace(e));
				}
			}
			catch (FileNotFoundException e1) {
				log.error("", e1);
			}
		}
		
		//sort by dispense date in descending order
		Collections.sort(medicationList,new MedicationListComparator());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -numMonths);
		Date threshholdDate = calendar.getTime();
		
		//remove medications with dispense date older than 2 months
		System.out.println(medicationList.size()+ " size of medication list");
		Iterator<Medication> iter = medicationList.iterator();
		while(iter.hasNext()){
			Medication currMed = iter.next();
			Date dispenseDate = currMed.getDispenseDate();
			String dateString = "";
			if(dispenseDate != null){
				dateString = dispenseDate.toString();
			}
			
			System.out.println(currMed.getName()+" ("+dateString+")");
			if(dispenseDate!=null&&dispenseDate.before(threshholdDate)){
				boolean filtered = true;
				//don't remove exception list meds from the list even if they are outside
				//the dispenseDate range
				if(exceptionMeds != null){
					org.openmrs.module.chica.xmlBeans.Medication unfilteredMed = 
						exceptionMeds.get(currMed.getNdcName());
					
					if(unfilteredMed != null&&unfilteredMed.getSystem().equalsIgnoreCase("NDC")){
						filtered = false;
					}
					
					unfilteredMed = 
						exceptionMeds.get(currMed.getRegenstriefName());
					
					if(unfilteredMed != null&&unfilteredMed.getSystem().equalsIgnoreCase("RMRS")){
						filtered = false;
					}
				}
				if(filtered){
					iter.remove();
				}
			}
		}
	}
}
