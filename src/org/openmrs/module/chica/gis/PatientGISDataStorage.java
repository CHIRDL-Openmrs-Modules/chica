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
package org.openmrs.module.chica.gis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Storage utility for storing different means of geocoded data for a patient.
 * 
 * @author Steve McKee
 */
public class PatientGISDataStorage {
	
	private static Map<Integer, Map<String,PatientGISData>> patientGISData = 
		new ConcurrentHashMap<Integer, Map<String,PatientGISData>>(new HashMap<Integer, Map<String,PatientGISData>>());
	private static Log log = LogFactory.getLog(PatientGISDataStorage.class);
	
	/**
	 * Stores GIS data for a patient.
	 * 
	 * @param patientId The ID of the patient for which to store the GIS data.
	 * @param storageType The resource the geocoded information is for.
	 * @param data The actual data to be stored.
	 */
	public static void storePatientGISData(Integer patientId, String storageType, PatientGISData data) {
		Map<String, PatientGISData> storedData = patientGISData.get(patientId);
		if (storedData == null) {
			storedData = new HashMap<String, PatientGISData>();
		}
		
		storedData.put(storageType, data);
		patientGISData.put(patientId, storedData);
	}
	
	/**
	 * Retrieves GIS data for a patient. 
	 * 
	 * @param patientId The ID of the patient for the the GIS data is requested.
	 * @param storageType The resource for which the geocoded information is requested.
	 * @return PatientGISData The request patient GIS data or null if none is found.
	 */
	public static PatientGISData getPatientGISData(Integer patientId, String storageType) {
		Map<String, PatientGISData> storedData = patientGISData.get(patientId);
		if (storedData == null) {
			return null;
		}
		
		return storedData.get(storageType);
	}
	
	/**
	 * Clears all data from the storage maps.
	 */
	public static void clearAllPatientGISData() {
		synchronized(patientGISData) {
			Set<Entry<Integer,Map<String,PatientGISData>>> entries = patientGISData.entrySet();
			if (entries != null) {
				Iterator<Entry<Integer,Map<String,PatientGISData>>> iter = entries.iterator();
				while (iter.hasNext()) {
					Entry<Integer,Map<String,PatientGISData>> entry = iter.next();
					Map<String,PatientGISData> data = entry.getValue();
					if (data != null) {
						data.clear();
					}
				}
			}
			
			log.info("Removed " + patientGISData.size() + " patient entries from the GIS cache.");
			patientGISData.clear();
		}
	}
	
	/**
	 * Clears GIS information for a particular patient.
	 * 
	 * @param patientId The ID of the patient to use to clear the GIS information.
	 */
	public static void clearPatientGISData(Integer patientId) {
		synchronized(patientGISData) {
			Map<String,PatientGISData> data = patientGISData.get(patientId);
			if (data != null) {
				data.clear();
				patientGISData.remove(patientId);
			}
		}
	}
}
