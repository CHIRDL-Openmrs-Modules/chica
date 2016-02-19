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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.immunization.ImmunizationRegistryQuery;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Meena Sheley
 * 
 * This task will query CHIRP for every patient previously enrolled in
 * specific HPV study projects.  For these encounters, an observation 
 * will be saved with the count of hpv vaccinations 
 * provided to that patient prior to their enrollment encounter.
 */
public class BatchImmunizationQuery extends AbstractTask {

	private static final String CHIRP_NOT_AVAILABLE = "CHIRP_not_available";
	private static final String CHIRP_LOGIN_FAILED = "CHIRP_login_failed";
	private static final String PROPERTY_STOP_DATE = "Stop_Date";
	private static final String PROPERTY_START_DATE = "Start_Date";
	private static final String PROPERTY_KEY_ENROLLMENT_CONCEPT = "enrollment_concept";
	private static final String PROPERTY_KEY_FOLLOWUP_CONCEPT = "followup_concept";
	private static final String PROPERTY_KEY_MAX_NUMBER_OF_ENCOUNTERS = "max_number_of_encounters";
	private static final String PROPERTY_KEY_SLEEP_TIME_MS = "sleep_time_msec";
	private static final String PROPERTY_KEY_RETRY_SLEEP_TIME_MIN = "retry_sleep_time_minutes";
	private static final String CHIRP_STATUS_CONCEPT = "CHIRP_Status";
	private static final String HPV_VACCINE_NAME = "HPV, unspecified formulation";
	private static final String PROPERTY_KEY_MAX_RETRY = "max_retry_count";
	
	private Log log = LogFactory.getLog(this.getClass());

	private TaskDefinition taskConfig;
	private String enrollmentConceptProperty; //String containing concept name
	private String maxEncounterCountProperty;
	private String maxRetryCountProperty;
	private String startDateProperty;
	private String stopDateProperty;
	private String followupConceptProperty;
	private Concept chirpStatusConcept;
	private String sleepTimeProperty;
	private String retrySleepTimeProperty;
	private Integer maxEncounterCount = 0;
	private Date startDate = null;
	private Date stopDate = null;
	private Integer sleep = null;
	private Integer retrySleep = null;
	private Integer maxRetries = 0;
	
	
	
	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);
		this.taskConfig = config;
		
		try {
			
			log.info("Initializing BatchImmunizationQuery task.");
			enrollmentConceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_ENROLLMENT_CONCEPT);
		    startDateProperty = this.taskConfig.getProperty(PROPERTY_START_DATE);
		    stopDateProperty = this.taskConfig.getProperty(PROPERTY_STOP_DATE);
		    followupConceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_FOLLOWUP_CONCEPT);
		    maxEncounterCountProperty = this.taskConfig.getProperty(PROPERTY_KEY_MAX_NUMBER_OF_ENCOUNTERS);
		    maxRetryCountProperty = this.taskConfig.getProperty(PROPERTY_KEY_MAX_RETRY);
		    sleepTimeProperty = this.taskConfig.getProperty(PROPERTY_KEY_SLEEP_TIME_MS);
		    retrySleepTimeProperty = this.taskConfig.getProperty(PROPERTY_KEY_RETRY_SLEEP_TIME_MIN);
		    //start and stop dates are not required
		    //StringUtils isBlank() and isNumeric() have null checking
		    if (StringUtils.isBlank(enrollmentConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + "' is not present in the property list for this task");
				return;
			}
		    if (StringUtils.isBlank(followupConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + "' is not present in the property list for this task");
				return;
			}
		    if (StringUtils.isBlank(maxEncounterCountProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_MAX_NUMBER_OF_ENCOUNTERS + "' is not present in the property list for this task");
				return;
			}
		    if (StringUtils.isNumeric(sleepTimeProperty) && !StringUtils.isWhitespace(sleepTimeProperty)) {
				sleep = Integer.valueOf(sleepTimeProperty);
			}
		    if (StringUtils.isNumeric(retrySleepTimeProperty) && !StringUtils.isWhitespace(retrySleepTimeProperty)) {
		    	retrySleep = Integer.valueOf(retrySleepTimeProperty);
		    	retrySleep = retrySleep * 60000;
			}
		    
		    try {
				if (!StringUtils.isBlank(startDateProperty)) {
					startDate = DateUtil.parseYmd(startDateProperty);
				}
				if (!StringUtils.isBlank(stopDateProperty)) {
					stopDate = DateUtil.parseYmd(stopDateProperty);
				}
			} catch (RuntimeException e) {
				log.info("Batch immunization query task property '" + PROPERTY_STOP_DATE + " and/or " +  "PROPERTY_START_DATE' exist, but are either null, blank, or invalid format");
				//ok to continue
			}
		    
		   startExecuting();
		    
		}catch(Exception e){
			
			log.error(taskDefinition.getName() + " failed during initialize", e);
		}
	}


	@Override
	public void execute() {
		Context.openSession();

		ConceptService conceptService = Context.getConceptService();

		try {

			Concept enrollmentConcept = conceptService.getConceptByName(enrollmentConceptProperty);
			if (enrollmentConcept == null ) {
				log.info ("HPV study:  Task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + "' is not a valid concept");
				return;
			}
			Concept followUpConcept = conceptService.getConceptByName(followupConceptProperty);
			if (followUpConcept == null) {
				log.info ("HPV study:  Task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + "' is not a valid concept");
				return;
			}
			
			try {
				maxEncounterCount = Integer.valueOf(maxEncounterCountProperty.trim());
			} catch (NumberFormatException e) {
				log.info("HPV Study: Task property 'max_number_of_encounters' could not be parsed as an Integer");
				return;
			}
			
			try {
				maxRetries = Integer.valueOf(maxRetryCountProperty.trim());
			} catch (NumberFormatException e) {
				log.info("HPV Study: Task property 'max_retry_count' could not be parsed as an Integer");
			}
			
			chirpStatusConcept = conceptService.getConceptByName(CHIRP_STATUS_CONCEPT);
			if (chirpStatusConcept == null){
				log.info("HPV study: '"+ CHIRP_STATUS_CONCEPT + "'is not a valid concept.");
				return;
			}
			if (chirpStatusConcept == null){
				log.info("HPV study: '"+ CHIRP_STATUS_CONCEPT + "'is not a valid concept.");
				return;
			}
			
			queryChirp(enrollmentConcept, followUpConcept);
			
		} catch (Exception e) {
			log.error("HPV study: Exception during vaccine follow-up check.", e);
		} finally {
			Context.closeSession();
		}
	}

	private void queryChirp(Concept enrollmentConcept, Concept followUpConcept) {
		

		ChicaService chicaService = Context.getService(ChicaService.class);
		ObsService obsService = Context.getObsService();
		
		//Count the queries to CHIRP. This is not updated unlessa query was performed.
		int numberOfQueries = 0;
		int failureCount = 0;
		int errorCount  = 0;
		int retries = 0;
		
		String identifier = "";
		

		List<Encounter> encounters = chicaService
				.getEncountersForEnrolledPatientsExcludingConcepts(enrollmentConcept, followUpConcept,
						startDate, stopDate);
		int  numberOfEncounters = encounters == null ? 0 : encounters.size();
		
		String startDatetext = (startDate != null) ? (" from " + startDateProperty): "";
		String stopDateText = (stopDate != null) ? (" through " + stopDateProperty): "";
		log.info("Number of HPV enrollment encounters" + startDatetext + stopDateText + " : " + numberOfEncounters );
		log.info("Max number of HPV enrollment encounters to query = " + maxEncounterCount);
		
		Iterator<Encounter> encountersIterator = encounters.listIterator();
		
		while ( isExecuting() && encountersIterator.hasNext() &&  numberOfQueries < maxEncounterCount) {
			
			Encounter encounter = encountersIterator.next();
			
			try {
				Integer vaccineCount = 0;
				retries = 0;
				Date startQuery = new Date();
				String queryResponse = ImmunizationRegistryQuery.queryCHIRP(encounter);

				/* Queries can fail due to CHIRP access, invalid CHIRP response, CHIRP could not find patient,
				 * parsing error, or patient not match to our database.
				 */
				//If it is a chirp availablility issue, sleep and retry the query
				while (queryResponse == null 
						&& ( isChirpIssue(encounter, startQuery)) 
						&& retries < maxRetries){
					
					log.info("CHIRP not available, retry query in " + (retrySleep == null||retrySleep == 0 ? 0 :retrySleep/60000) + " min.");
					if (retrySleep != null) {
						Thread.sleep(retrySleep);
					}
					startQuery = new Date();
					log.info("Requerying...");
					queryResponse = ImmunizationRegistryQuery.queryCHIRP(encounter);
					retries++;
				}
				//Last retry was still null
				if (queryResponse == null){
					failureCount++;
					//Check if it is still a CHIRP issue but ran out of retries
					if (isChirpIssue(encounter, startQuery)){
						//Chirp problems, but reached max retry limit
						log.info("CHIRP query issues due to CHIRP availability. PatientId = " + encounter.getPatientId() + ".\r\n" 
								+ "Number of encounters = " + numberOfEncounters + ".\r\n"
								+ "Number of CHIRP queries performed before CHIRP error = " + numberOfQueries + ".\r\n"
								+ "Number of failed queries = " + failureCount  + ".\r\n"
								+ "Number of retries = " + retries + ".\r\n");
						return;
					}
					//Not a chirp issue, but possible problems such as patient matching and parsing errors
					continue;
				}


				ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
						.getImmunizationList(encounter.getPatientId());

				identifier = encounter.getPatient().getPatientIdentifier().toString();

				if (immunizations != null) {
					HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
							.getImmunizationPrevious();

					if (prevImmunizations != null){

						HashMap<Integer, ImmunizationPrevious> HpvHistory = prevImmunizations.get(HPV_VACCINE_NAME);

						if (HpvHistory != null){
							
							for(ImmunizationPrevious value : HpvHistory.values()){
								if (value.getDate().before(DateUtil.getStartOfDay(encounter.getEncounterDatetime()))){
									vaccineCount++;
								}
							}
						}
					}
				}

				
				Patient patient = encounter.getPatient();
				Hibernate.initialize(patient);
				Obs obs = new Obs();
				obs.setValueNumeric(vaccineCount.doubleValue());
				obs.setEncounter(encounter);
				obs.setPerson(patient);
				obs.setConcept(followUpConcept);
				obs.setObsDatetime(encounter.getEncounterDatetime());
				obsService.saveObs(obs, null);
				if (sleep != null) Thread.sleep(sleep);

			}catch (InterruptedException e){
				log.info("Exception executing Thread.sleep. Sleep time = " +  sleep);
			}catch(Exception e){
				log.info("HPV Study: Exception during requery for patientId = " + encounter.getPatientId() +
						" identifier = " + identifier, e);
				errorCount++;
			}finally{
				ImmunizationForecastLookup.removeImmunizationList(encounter.getPatientId());
				numberOfQueries++;
			}
		}
		
		log.info("Batch immunization query completed. \r\n Number of encounters = " + numberOfEncounters + ".\r\n"
				+ "Number of CHIRP queries performed = " + numberOfQueries + ".\r\n"
				+ "Number of failed queries = " + failureCount + ".\r\n"
				+ "Error count = " + errorCount);
		
		return;
	}
	
	/**
	 * Check if the latest CHIRP status observation indicates CHIRP is not available.  
	 * @param encounter
	 * @param startQuery
	 * @return
	 */
	private boolean isChirpIssue(Encounter encounter, Date startQuery){
		 
		ObsService obsService = Context.getObsService();
		Date now = new Date();
		Integer mostRecentCount = 1;
		boolean isChirp = false;
		List<Obs> latestChirpStatusObs = obsService.getObservations(
				Collections.singletonList((Person)encounter.getPatient()), 
				Collections.singletonList((org.openmrs.Encounter) encounter), 
				Collections.singletonList(chirpStatusConcept), 
				null, null, null, null, mostRecentCount, null, startQuery, now, false);
		
		Iterator<Obs> iter = latestChirpStatusObs.iterator();
		if (iter.hasNext()){
			Obs chirpStatusObs = iter.next();
			if (CHIRP_NOT_AVAILABLE.equals(chirpStatusObs.getValueAsString(Locale.US))||
					CHIRP_LOGIN_FAILED.equals(chirpStatusObs.getValueAsString(Locale.US))){
				isChirp = true;
			}
			
		}
	
		return isChirp;
	}

	@Override
	public void shutdown() {
		stopExecuting();
		super.shutdown();
		log.info("Shutting down BatchImmunizationQuery task.");
	}
	

}
