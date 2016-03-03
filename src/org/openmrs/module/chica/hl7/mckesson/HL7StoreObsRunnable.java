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
package org.openmrs.module.chica.hl7.mckesson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import ca.uhn.hl7v2.model.Message;

/**
 * Used to store observations from an HL7 message to data sources.
 * 
 * @author Steve McKee
 */
public class HL7StoreObsRunnable implements Runnable {
	
	private Log log = LogFactory.getLog(this.getClass());
	private Integer patientId;
	private Integer locationId;
	private Integer encounterId;
	private Integer sessionId;
	private Message message;
	private String printerLocation;
	
	/**
	 * Constructor method
	 * 
	 * @param patientId The patient identifier
	 * @param locationId The location message
	 * @param encounterId The encounter identifier
	 * @param sessionid The session identifier
	 * @param message The HL7 message
	 * @param printerLocation The encounter printer location
	 */
	public HL7StoreObsRunnable(Integer patientId, Integer locationId, Integer encounterId, 
	                           Integer sessionId, Message message, String printerLocation) {
		this.patientId = patientId;
		this.locationId = locationId;
		this.encounterId = encounterId;
		this.sessionId = sessionId;
		this.message = message;
		this.printerLocation = printerLocation;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		try {
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
			    adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
			Patient patient = Context.getPatientService().getPatient(patientId);
			if (patient == null) {
				log.error("Invalid patient ID: " + patientId);
				return;
			}
			
			Location location = Context.getLocationService().getLocation(locationId);
			if (location == null) {
				log.error("Invalid location ID: " + locationId);
				return;
			}
			
			EncounterService encounterService = Context.getService(EncounterService.class);
			org.openmrs.Encounter encounter = encounterService.getEncounter(encounterId);
			if (encounter == null) {
				log.error("Invalid encounter ID: " + encounterId);
				return;
			}
			
			Encounter chicaEncounter = (Encounter) encounter;
			chicaEncounter.setPrinterLocation(printerLocation);
			storeHL7Obs(patient, location, chicaEncounter);
		}
		catch (Exception e) {
			log.error("Error processing file", e);
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * Stores the observations from the HL7 message to applicable data sources.
	 * 
	 * @param patient The patient to whom the observations will be attached
	 * @param location The location of the encounter
	 * @param encounter The encounter for the patient
	 * @throws Exception
	 */
	private void storeHL7Obs(Patient patient, Location location, Encounter encounter) throws Exception {
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State state = backportsService.getStateByName(ChirdlUtilConstants.STATE_HL7_PROCESS_REGISTRATION_OBS);
		PatientState patientState = backportsService
				.addPatientState(patient, state, sessionId, org.openmrs.module.chica.util.Util.getLocationTagId(encounter),
						location.getLocationId(), null);
		patientState.setStartTime(new Date());
		patientState = backportsService.updatePatientState(patientState);
		Integer patientId = patient.getPatientId();
		HL7ObsHandler25 obsHandler = new HL7ObsHandler25();
		ArrayList<Obs> allObs = obsHandler.getObs(message, patient);
		LogicService logicService = Context.getLogicService();
		ObsInMemoryDatasource xmlDatasource = 
				(ObsInMemoryDatasource) logicService.getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY);
		
		HashMap<Integer, HashMap<String, Set<Obs>>> patientObsMap = xmlDatasource.getObs();
		HashMap<String, Set<Obs>> obsByConcept = patientObsMap.get(patientId);
		
		if (obsByConcept == null) {
			obsByConcept = new HashMap<String, Set<Obs>>();
			patientObsMap.put(patientId, obsByConcept);
		}
		
		final String SOURCE = ChirdlUtilConstants.DATA_SOURCE_IU_HEALTH_MEDICAL_RECORD;
		final String VITALS_SOURCE = ChirdlUtilConstants.DATA_SOURCE_IU_HEALTH_VITALS;
		
		Map<Integer, Concept> mrfConceptMapping = new HashMap<Integer, Concept>();
		Map<Integer, Concept> vitalsConceptMapping = new HashMap<Integer, Concept>();
		Map<String, Concept> vitalsConceptByNameMapping = new HashMap<String, Concept>();
		Set<Integer> mrfConceptSet = new HashSet<Integer>();
		Set<Integer> vitalsConceptSet = new HashSet<Integer>();
		Set<String> vitalsConceptByNameSet = new HashSet<String>();
		
		ConceptService conceptService = Context.getConceptService();
		ObsService obsService = Context.getObsService();
		boolean savedToDB = false;
		
		for (Obs currObs : allObs) {
			savedToDB = false;
			String currConceptName = ((ConceptName) currObs.getConcept().getNames().toArray()[0]).getName();
			
			//TMD CHICA-498 Look for concept mapping if this is IU Health, the codes (not names) are mapped
			if (location != null && ChirdlUtilConstants.LOCATION_RIIUMG.equalsIgnoreCase(location.getName())) {
				Concept concept = currObs.getConcept();
				Integer conceptId = concept.getConceptId();
				
				//convert units for historical data
				switch (conceptId) {
					case 39650704: //Birth Weight (kg)
						double kilograms = currObs.getValueNumeric();
						double pounds = org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(kilograms,
						    org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_KG);
						currObs.setValueNumeric(pounds);//BIRTH WEIGHT in chica in pounds 
						break;
					default:
				}
				
				//convert units for historical vitals data
				org.openmrs.module.chica.hl7.iuHealthVitals.HL7SocketHandler.convertIUHealthVitalsUnits(conceptId,
				    currObs);
				
				// check to see if we've already looked up a mapping for this concept
				Concept mappedConcept = mrfConceptMapping.get(conceptId);
				if (mappedConcept == null) {
					// check to see if we've already searched this one before
					if (!mrfConceptSet.contains(conceptId)) {
						mappedConcept = conceptService.getConceptByMapping(conceptId.toString(), SOURCE);
						mrfConceptSet.add(conceptId);
						mrfConceptMapping.put(conceptId, mappedConcept);
					}
				}
				
				if (mappedConcept != null) {
					currConceptName = mappedConcept.getName().getName();
					currObs.setConcept(mappedConcept);
				}
				
				Concept answerConcept = currObs.getValueCoded();
				//see if any answer concepts need mapped
				if (answerConcept != null) {
					String answerConceptName = answerConcept.getName().getName();
					Concept mappedVitalsConcept = vitalsConceptByNameMapping.get(answerConceptName);
					if (mappedVitalsConcept == null) {
						// check to see if we've already searched this one before
						if (!vitalsConceptByNameSet.contains(answerConceptName)) {
							mappedVitalsConcept = conceptService.getConceptByMapping(answerConceptName, VITALS_SOURCE);
							vitalsConceptByNameSet.add(answerConceptName);
							vitalsConceptByNameMapping.put(answerConceptName, mappedVitalsConcept);
						}
					}
					
					if (mappedVitalsConcept != null) {
						currObs.setValueCoded(mappedVitalsConcept);
					}
				}
				
				//If this is a historical vital, save it to the database
				Concept mappedVitalsConcept = vitalsConceptMapping.get(conceptId);
				if (mappedVitalsConcept == null) {
					// check to see if we've already searched this one before
					if (!vitalsConceptSet.contains(conceptId)) {
						mappedVitalsConcept = conceptService.getConceptByMapping(conceptId.toString(), VITALS_SOURCE);
						vitalsConceptSet.add(conceptId);
						vitalsConceptMapping.put(conceptId, mappedVitalsConcept);
					}
				}
				
				if (mappedVitalsConcept != null) {
					if (currObs.getValueCoded() != null && currObs.getValueCoded().getConceptId() == 1) {
						currObs.setValueCoded(null);
						if (answerConcept != null) {
							String answerConceptName = answerConcept.getName().getName();
							currObs.setValueText(answerConceptName);
							log.error("Could not map IU Health Cerner vitals concept: " + answerConceptName
							        + ". Could not store vitals observation.");
						}
					}
					currObs.setConcept(mappedVitalsConcept);
					currObs.setLocation(location);
					obsService.saveObs(currObs, null);
					savedToDB = true;
				}
				
			}
			
			//put the observation in memory if it was not saved to the database
			if (!savedToDB) {
				Set<Obs> obs = obsByConcept.get(currConceptName);
				if (obs == null) {
					obs = new HashSet<Obs>();
					obsByConcept.put(currConceptName, obs);
				}
				obs.add(currObs);
			}
		}
		
		mrfConceptMapping.clear();
		vitalsConceptMapping.clear();
		vitalsConceptByNameMapping.clear();
		mrfConceptSet.clear();
		vitalsConceptSet.clear();
		vitalsConceptByNameSet.clear();
		
		patientState.setEndTime(new Date());
		backportsService.updatePatientState(patientState);
	}
}
