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
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
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
	private static final String CHIRP_STATUS_CONCEPT = "CHIRP_Status";
	private static final String HPV_VACCINE_NAME = "HPV, unspecified formulation";
	
	private Log log = LogFactory.getLog(this.getClass());

	private TaskDefinition taskConfig;
	private String enrollmentConceptProperty; //String containing concept name
	private String maxEncounterCountProperty;
	private String startDateProperty;
	private String stopDateProperty;
	private String followupConceptProperty;
	private Concept chirpStatusConcept;
	private Integer maxEncounterCount = 0;
	
	
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
		    //start and stop dates are not required
		    
		    if (StringUtils.isBlank(enrollmentConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + "' is not present in the property list for this task");
				shutdown();
			}
		    if (StringUtils.isBlank(followupConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + "' is not present in the property list for this task");
				shutdown();
			}
		    if (StringUtils.isBlank(maxEncounterCountProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_MAX_NUMBER_OF_ENCOUNTERS + "' is not present in the property list for this task");
				shutdown();
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
				log.error ("HPV study:  Task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + "' is not a valid concept");
				return;
			}
			Concept followUpConcept = conceptService.getConceptByName(followupConceptProperty);
			if (followUpConcept == null) {
				log.error ("HPV study:  Task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + "' is not a valid concept");
				return;
			}
			chirpStatusConcept = conceptService.getConceptByName(CHIRP_STATUS_CONCEPT);
			if (chirpStatusConcept == null){
				log.error("HPV study: '"+ CHIRP_STATUS_CONCEPT + "'is not a valid concept.");
			}
			
			maxEncounterCount = Integer.valueOf(maxEncounterCountProperty.trim());

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
		//Count the queries to CHIRP. This is not updated unless a query was performed.
		int numberOfQueries = 0;
		Date startDate = null;
		Date stopDate = null;
		
		if (!StringUtils.isBlank(startDateProperty)) {
			startDate = DateUtil.parseYmd(startDateProperty);
		}
	    if (!StringUtils.isBlank(stopDateProperty)) {
	    	stopDate = DateUtil.parseYmd(stopDateProperty);
		}

		List<Encounter> encounters = chicaService
				.getEncountersForEnrolledPatients(enrollmentConcept,
						startDate, stopDate);
		int  numberOfEncounters = encounters == null ? 0 : encounters.size();
		log.info("Number of HPV enrollment encounters from " + startDateProperty 
				+ " through " + stopDateProperty + " : " + numberOfEncounters );
		
		Iterator<Encounter> encountersIterator = encounters.listIterator();
		
		while (encountersIterator.hasNext() && isExecuting() &&  numberOfQueries < maxEncounterCount) {
			try {
				//Do not query chirp if this obs already exists for the encounter.
				Encounter encounter = encountersIterator.next();
				Integer obsCount = obsService.getObservationCount(
								Collections.singletonList((Person) encounter.getPatient()),
								Collections.singletonList((org.openmrs.Encounter) encounter),
								Collections.singletonList(followUpConcept),
								null, null, null, null, null, null, false);

				// If this observation already exists for this encounter
				if (obsCount > 0) {
					continue;
				}

				
				String queryResponse = ImmunizationRegistryQuery.queryCHIRP(encounter);

				/* Check the latest CHIRP status observation today for this requery.  
				 * We do not want to send anymore immunization queries to CHIRP
				 * if it is not available.  Try again at the next repeat interval for the task.
				 */

				if (queryResponse == null) {

					Date now = new Date();
					Integer mostRecentCount = 1;
					List<Obs> latestChirpStatusObs = obsService.getObservations(
							Collections.singletonList((Person)encounter.getPatient()), 
							Collections.singletonList((org.openmrs.Encounter) encounter), 
							Collections.singletonList(chirpStatusConcept), 
							null, null, null, null, mostRecentCount, null, DateUtil.getStartOfDay(now), now, false);
					
					Iterator<Obs> iter = latestChirpStatusObs.iterator();
					if (iter.hasNext()){
						Obs chirpStatusObs = iter.next();
						if (CHIRP_NOT_AVAILABLE.equals(chirpStatusObs.getValueAsString(Locale.US))||
								CHIRP_LOGIN_FAILED.equals(chirpStatusObs.getValueAsString(Locale.US))){
							log.error("HPV Study: Follow-up CHIRP query problems due to CHIRP availability.");
							return;
						}
					}

					continue;
				}

				ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
						.getImmunizationList(encounter.getPatientId());

				String identifier = encounter.getPatient().getPatientIdentifier().toString();
				if (immunizations == null) {
					log.info("HPV Study: Vaccine requery found no immunizations in CHIRP for patient: " + identifier + 
							" Since this patient was enrolled in the study, this patient should have an immunization record.");
					continue;
				}

				// patient has immunization records

				HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
						.getImmunizationPrevious();

				if (prevImmunizations == null || prevImmunizations.get(HPV_VACCINE_NAME) == null){
					log.info("HPV Study: There are no HPV vaccine records in CHIRP for patient: " + identifier +
							" Since this patient was enrolled in the study, this patient " + 
							"should have historical vaccination records.");
					ImmunizationForecastLookup.removeImmunizationList(encounter.getPatientId());			
					continue;
				}

				HashMap<Integer, ImmunizationPrevious> HpvHistory = prevImmunizations.get(HPV_VACCINE_NAME);
				ImmunizationForecastLookup.removeImmunizationList(encounter.getPatientId());	
				
				Integer count = 0;
				for(ImmunizationPrevious value : HpvHistory.values()){
					if (value.getDate().before(DateUtil.getStartOfDay(encounter.getEncounterDatetime()))){
						count++;
					}
				}

				Obs obs = new Obs();
				obs.setValueNumeric(count.doubleValue());
				obs.setEncounter(encounter);
				obs.setPerson(encounter.getPatient());
				obs.setConcept(followUpConcept);
				obs.setObsDatetime(new Date());
				obsService.saveObs(obs, null);
				
				
				numberOfQueries++;

			}catch(Exception e){
				log.info("HPV Study: Exception during vaccine count requery.", e);
			}
		}
		
		log.info("Batch immunization query completed. Number of encounters = " + numberOfEncounters 
				+ ". Number of CHIRP queries = " + numberOfQueries);
		
		return;
	}

	@Override
	public void shutdown() {
		stopExecuting();
		super.shutdown();
		log.info("Shutting down BatchImmunizationQuery task.");
	}
	

}
