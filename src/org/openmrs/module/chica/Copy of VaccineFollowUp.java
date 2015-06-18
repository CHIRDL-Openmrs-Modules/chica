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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.immunization.ImmunizationRegistryQuery;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Meena Sheley
 * 
 * Scheduled task to determine if the physician administered the vaccine at or after the visit.
 */
public class VaccineFollowUp extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());

	private TaskDefinition taskConfig;
	private String conceptProperty; //String containing concept name
	private List<Integer> followUpIntervals = new ArrayList<Integer>();
	
	private String PROPERTY_KEY_CONCEPT = "concept";
	private String HPV_VACCINE_NAME = "HPV, unspecified formulation";	
	private String TWO_WEEKS = "2wk"; //this is part of the concept name
	private String FOUR_MONTHS = "4mo"; //this is part of the concept name
	private String PROPERTY_KEY_TIME_PERIOD_DAYS = "follow_up_days";
	private String followUpDays = "";
	
	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);
		this.taskConfig = config;
		
		try {
			
			log.info("Initializing vaccine follow-up scheduled task.");
			followUpDays = this.taskConfig.getProperty(PROPERTY_KEY_TIME_PERIOD_DAYS);
		    conceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_CONCEPT);
		    
		}catch(Exception e){
			
			log.error(taskDefinition.getName() + " failed during initialize", e);
		}
	}


	@Override
	public void execute() {
		Context.openSession();

		ConceptService conceptService = Context.getConceptService();

		try {
				Integer intervalInDays = 14;
				if (conceptProperty == null || conceptProperty.trim().equals("")) {
					log.error("HPV study: Task property '" + PROPERTY_KEY_CONCEPT + "' does not exist for this patient for this task. ");
					return;
				}
				
				Concept enrollmentConcept = conceptService.getConceptByName(conceptProperty);
				if (enrollmentConcept == null) {
					log.error ("HPV study:  Task property '" + PROPERTY_KEY_CONCEPT + "' is not a valid concept");
					return;
				}
				
				Concept enrollmentFollowupConcept = conceptService.getConceptByName("2wk_HPV");
				
				
				List<String> requeryIntervals = Arrays.asList(followUpDays
						.split("\\s*,\\s*"));
				Integer intervalInDays = null;
				
				for (String requeryInterval : requeryIntervals) {

					try {
						intervalInDays = Integer.valueOf(requeryInterval);
						followUpCheck(enrollmentConcept, enrollmentFollowupConcept, intervalInDays, HPV_VACCINE_NAME);
					} catch (NumberFormatException e) {
						// String was not an integer
						continue;
					}
				}
				
				
		} catch (Exception e) {
			log.error("HPV study: Exception during vaccine follow-up check.", e);
		} finally {
			Context.closeSession();
		}
	}


	private Integer getVaccineDoses(String enrollmentConceptName,  String followUpConceptName,  String vaccineName,  Integer followUpInterval) {

		ConceptService conceptService = Context.getConceptService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		ObsService obsService = Context.getObsService();

		
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -(followUpInterval));
			Date startDateTime = DateUtil.getStartOfDay(c.getTime());
			Date stopDatetime = DateUtil.getEndOfDay(c.getTime());
			Date startOfToday = DateUtil.getStartOfDay(new Date()); 
			Date endOfToday = DateUtil.getEndOfDay(new Date());
			

			List<Encounter> encounters = chicaService
					.getEncountersForEnrolledPatients(enrollmentConcept,
							startDateTime, stopDatetime);

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			log.info("HPV study: " + encounters == null ? "0 " : encounters.size()
					+ " encounters found for date: "
					+ sdf.format(startDateTime));

			for (Encounter encounter : encounters) {

				try {
					
					/* This task may run multiple times per day to handle conditions
					 * when CHIRP was not available previously.
					 * If follow-up observations exist already for today,
					 * do not query for this encounter again. 
					 */

						List<Person> patients = new ArrayList<Person>();
						patients.add(encounter.getPatient());
						List<Concept> concepts = new ArrayList<Concept>();

						concepts.add(conceptService
								.getConceptByName(followUpConceptName));
						List<Obs> obsList = obsService.getObservations(
								patients, null, concepts, null, null, null, null, null, null,
								startOfToday, 
								endOfToday,
								false);
						
						// If this observation already exists for today, do not query CHIRP again.
						if (obsList.size() > 0) {
							continue;
						}
					

					
					String queryResponse = ImmunizationRegistryQuery.queryCHIRP(encounter);

					/* The method queryCHIRP() returns null for any issues with query such
					 * as CHIRP availability, parse errors, no patient match, etc.
					 * It also handles saving the observations for these CHIRP issues
					 */
					
					if (queryResponse == null) {
						log.info("	HPV Study: Follow-up CHIRP query problems: CHIRP availablility, parse errors, or no patient matches. MRN: "
								+ encounter.getPatient().getPatientIdentifier());
						continue;
					}

					ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
							.getImmunizationList(encounter.getPatientId());

					if (immunizations == null) {
						log.error("ERROR", "Vaccine requery found no immunizations in CHIRP for patient: " 
										+ encounter.getPatient().getPatientIdentifier());
						continue;
					}

					// patient has immunization records
					Integer hpvDoses = 0;
					
					HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
							.getImmunizationPrevious();
					
					if (prevImmunizations == null){
						log.error("ERROR", "Follow-up are no previous vacciniations in CHIRP for vaccine: " 
								+ vaccineName + " patient: "
								+ encounter.getPatient().getPatientIdentifier());
						continue;
					}

					if (prevImmunizations != null) {
						HashMap<Integer, ImmunizationPrevious> HpvHistory = prevImmunizations
								.get(vaccineName);
						if (HpvHistory != null) {
							hpvDoses = HpvHistory.size();
						}
					}

					Concept hpvConcept = conceptService
								.getConceptByName(followUpConceptName);
					saveObs(encounter.getPatient(), hpvConcept, null,
								hpvDoses.toString());

					

					log.info("Follow-up check for time period " + followUpInterval
							+ " days. Patient: " + encounter.getPatientId()
							+ " HPV doses: " + hpvDoses;

				} catch (Exception e) {
					log.error(" HPV Study exception for encounter = "
							+ encounter.getId() + " patient: "
							+ encounter.getPatientId());
					continue;
				}

			}
		}

		return null;

	}

	private HashMap<String, String> setupVISNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();


		
		map.put("HPV", "HPV, unspecified formulation");

		return map;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		log.info("HPV study: Shutting down hpv follow-up scheduled task.");
	}
	

	private Obs saveObs (Patient patient, Concept concept, Integer encounterId, String value) {
		
		if (value == null || value.length() == 0) {
			return null;
		}
		
		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		try {
			obs.setValueNumeric(Double.parseDouble(value));
		}
		catch (NumberFormatException e) {
			//save as text
			obs.setValueText(value);
			log.error("HPV Study: Could not save value: " + value + " to the database for concept "
			        + concept.getName().getName());
		}
		
		//No need for an encounter for this obs. Obs was created after the visit.
		EncounterService encounterService = Context.getService(EncounterService.class);
		if (encounterId != null ){
			org.openmrs.Encounter encounter = encounterService.getEncounter(encounterId);
			if (encounter != null){
				obs.setLocation(encounter.getLocation());
				obs.setEncounter(encounter);
			}
		}
		
		obs.setPerson(patient);
		obs.setConcept(concept);
		obs.setObsDatetime(new Date());
		obsService.saveObs(obs, null);
		return obs;
	
	}
	
	private void logError(String severity, String details, Encounter encounter){
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		Error error = new Error(severity, ChirdlUtilConstants.ERROR_QUERY_IMMUNIZATION_CONNECTION, 
				details + " Patient id: " + encounter.getPatientId() 
				+ "encounter id: " + encounter.getEncounterId()  , null, new Date(), null);
		chirdlutilbackportsService.saveError(error);
		log.error( details);
		return;
	}


}
