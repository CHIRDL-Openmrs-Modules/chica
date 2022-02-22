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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
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
	
	private static final Logger log = LoggerFactory.getLogger(HL7StoreObsRunnable.class);
	private Integer patientId;
	private Integer locationId;
	private Integer sessionId;
	private Message message;
	private String printerLocation;
	
	/**
	 * Constructor method
	 * 
	 * @param patientId The patient identifier
	 * @param locationId The location message
	 * @param sessionid The session identifier
	 * @param message The HL7 message
	 * @param printerLocation The encounter printer location
	 */
	public HL7StoreObsRunnable(Integer patientId, Integer locationId,
	                           Integer sessionId, Message message, String printerLocation) {
		this.patientId = patientId;
		this.locationId = locationId;
		this.sessionId = sessionId;
		this.message = message;
		this.printerLocation = printerLocation;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Patient patient = Context.getPatientService().getPatient(this.patientId);
			if (patient == null) {
				this.log.error("Invalid patient ID: " + this.patientId);
				return;
			}
			
			Location location = Context.getLocationService().getLocation(this.locationId);
			if (location == null) {
				this.log.error("Invalid location ID: " + this.locationId);
				return;
			}
			
			// DWE CLINREQ-130 Get locationTagId
			LocationTag locationTag = Context.getLocationService().getLocationTagByName(this.printerLocation);
			if(locationTag == null)
			{
				this.log.error("Invalid printer location: " + this.printerLocation);
				return;
			}
			
			storeHL7Obs(patient, location, locationTag.getLocationTagId());
		}
		catch (Exception e) {
			this.log.error("Error processing file", e);
		}
	}
	
	/**
	 * Stores the observations from the HL7 message to applicable data sources.
	 * 
	 * @param patient The patient to whom the observations will be attached
	 * @param location The location of the encounter
	 * @param locationTagId - // DWE CLINREQ-130 Removed encounter parameter changed to locationTagId - see CAUTION below
	 *  
	 *  *********CAUTION: If an encounter object is needed in this thread in the future, use caution when calling setters on the object.
	 *  Hibernate will save the changes to the database even if the save method is not called. This could cause issues since the
	 *  encounter object is updated in a separate thread
	 *  *************************************************************************************************************************
	 *  
	 * @throws Exception
	 */
	private void storeHL7Obs(Patient patient, Location location, Integer locationTagId) throws Exception { 
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService  adminService = Context.getAdministrationService();	
		State state = backportsService.getStateByName(ChirdlUtilConstants.STATE_HL7_PROCESS_REGISTRATION_OBS);
		PatientState patientState = backportsService
				.addPatientState(patient, state, this.sessionId, locationTagId,
						location.getLocationId(), null); // DWE CLINREQ-130 Changed this to use the location service to get the location tag
		patientState.setStartTime(new Date());
		patientState = backportsService.updatePatientState(patientState);
		
		// MES CHICA-795 Global property will indicate if observations should be parsed and saved from registration message.
		String parseObsFromRegistration = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PARSE_OBS_FROM_REGISTRATION);
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(parseObsFromRegistration)){
			
			Integer patientId = patient.getPatientId();
			HL7ObsHandler25 obsHandler = new HL7ObsHandler25();
			
			ArrayList<Obs> allObs = obsHandler.getObs(this.message, patient);
			LogicService logicService = Context.getLogicService();
			ObsInMemoryDatasource xmlDatasource = 
					(ObsInMemoryDatasource) logicService.getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY);
			
			HashMap<String, Set<Obs>> obsByConcept = xmlDatasource.getObs(patientId);
			
			if (obsByConcept == null) {
				obsByConcept = new HashMap<>();
			}
			
			Map<Integer, Concept> mrfConceptMapping = new HashMap<>();
			Map<Integer, Concept> vitalsConceptMapping = new HashMap<>();
			Map<String, Concept> vitalsConceptByNameMapping = new HashMap<>();
			Set<Integer> mrfConceptSet = new HashSet<>();
			Set<Integer> vitalsConceptSet = new HashSet<>();
			Set<String> vitalsConceptByNameSet = new HashSet<>();
			
			ConceptService conceptService = Context.getConceptService();
			ObsService obsService = Context.getObsService();
			boolean savedToDB = false;
			
			//MES CHICA-795: Use global property for data sources
			String medicalRecordSource = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_DATA_SOURCE_MEDICAL_RECORD);
			String vitalsSource = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_DATA_SOURCE_VITALS);
			
			for (Obs currObs : allObs) {
				savedToDB = false;
				String currConceptName = ((ConceptName) currObs.getConcept().getNames().toArray()[0]).getName();
				
				//TMD CHICA-498 Look for concept mapping if this is IU Health, the codes (not names) are mapped
				Concept concept = currObs.getConcept();
				Integer conceptId = concept.getConceptId();
			
				// check to see if we've already looked up a mapping for this concept
				Concept mappedConcept = mrfConceptMapping.get(conceptId);
				if (mappedConcept == null) {
					// check to see if we've already searched this one before
					if (!mrfConceptSet.contains(conceptId)) {
						mappedConcept = conceptService.getConceptByMapping(conceptId.toString(), medicalRecordSource);
						mrfConceptSet.add(conceptId);
						mrfConceptMapping.put(conceptId, mappedConcept);
					}
				}
				
				if (mappedConcept != null) {
					currConceptName = mappedConcept.getName().getName();
					org.openmrs.module.chica.hl7.vitals.HL7SocketHandler.convertVitalsUnits(currObs, mappedConcept);
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
							mappedVitalsConcept = conceptService.getConceptByMapping(answerConceptName, vitalsSource);
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
						mappedVitalsConcept = conceptService.getConceptByMapping(conceptId.toString(), vitalsSource);
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
							this.log.error("Could not map vitals concept: " + answerConceptName
								+ ". Could not store vitals observation.");
						}
					}
					org.openmrs.module.chica.hl7.vitals.HL7SocketHandler.convertVitalsUnits(currObs, mappedVitalsConcept);
					currObs.setConcept(mappedVitalsConcept);
					currObs.setLocation(location);
					
					try{
						obsService.saveObs(currObs, null);
						savedToDB = true;
					}catch(APIException apie){
						// CHICA-1017 Catch the exception and log it so that we can continue processing the message
						this.log.error("APIException while saving obs " + currObs + ".", apie);
					}
				}
				
				//put the observation in memory if it was not saved to the database
				if (!savedToDB) {
					Set<Obs> obs = obsByConcept.get(currConceptName);
					if (obs == null) {
						obs = new HashSet<>();
						obsByConcept.put(currConceptName, obs);
					}
					obs.add(currObs);
				}
			
			}
			
			xmlDatasource.saveObs(patientId, obsByConcept);
			mrfConceptMapping.clear();
			vitalsConceptMapping.clear();
			vitalsConceptByNameMapping.clear();
			mrfConceptSet.clear();
			vitalsConceptSet.clear();
			vitalsConceptByNameSet.clear();
		}
		
		patientState.setEndTime(new Date());
		backportsService.updatePatientState(patientState);
	}
}
