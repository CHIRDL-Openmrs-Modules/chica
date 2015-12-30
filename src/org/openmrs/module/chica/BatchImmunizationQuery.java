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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Meena Sheley
 * 
 * Scheduled task to determine if the physician administered the vaccine at or after the visit.
 */
public class BatchImmunizationQuery extends AbstractTask {

	private static final String CHIRP_NOT_AVAILABLE = "CHIRP_not_available";
	private static final String PROPERTY_STOP_DATE = "Stop_Date";
	private static final String PROPERTY_START_DATE = "Start_Date";

	private Log log = LogFactory.getLog(this.getClass());

	private TaskDefinition taskConfig;
	private String enrollmentConceptProperty; //String containing concept name
	private static final String PROPERTY_KEY_ENROLLMENT_CONCEPT = "enrollment_concept";
	private static final String PROPERTY_KEY_FOLLOWUP_CONCEPT = "followup_concept";
	private String startDateProperty;
	private String stopDateProperty;
	private String followupConceptProperty;
	private static final String CHIRP_STATUS_CONCEPT = "CHIRP_Status";

	private static final String HPV_VACCINE_NAME = "HPV";
	private Concept statusConcept;
	
	
	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);
		this.taskConfig = config;
		
		try {
			
			log.info("Initializing vaccine follow-up scheduled task.");
			enrollmentConceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_ENROLLMENT_CONCEPT);
		    startDateProperty = this.taskConfig.getProperty(PROPERTY_START_DATE);
		    stopDateProperty = this.taskConfig.getProperty(PROPERTY_STOP_DATE);
		    followupConceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_FOLLOWUP_CONCEPT);
		    
		    if (StringUtils.isBlank(enrollmentConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + " is not present in the property list for this task");
				shutdown();
			}
		    if (StringUtils.isBlank(startDateProperty)) {
				log.error("Batch immunization query task property " + PROPERTY_START_DATE + " is not present in the property list for this task");
				shutdown();
			}
		    if (StringUtils.isBlank(stopDateProperty)) {
				log.error("Batch immunization query task property " + PROPERTY_STOP_DATE + " is not present in the property list for this task");
				shutdown();
			}
		    if (StringUtils.isBlank(followupConceptProperty)) {
				log.error("Batch immunization query task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + " is not present in the property list for this task");
				shutdown();
			}
		    
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
				if (enrollmentConcept == null) {
					log.error ("HPV study:  Task property '" + PROPERTY_KEY_ENROLLMENT_CONCEPT + "' is not a valid concept");
					return;
				}
				Concept followUpConcept = conceptService.getConceptByName(followupConceptProperty);
				if (followUpConcept == null) {
					log.error ("HPV study:  Task property '" + PROPERTY_KEY_FOLLOWUP_CONCEPT + "' is not a valid concept");
					return;
				}
				statusConcept = conceptService.getConceptByName(CHIRP_STATUS_CONCEPT);
				
				queryChirp(enrollmentConcept, followUpConcept);
				
		} catch (Exception e) {
			log.error("HPV study: Exception during vaccine follow-up check.", e);
		} finally {
			Context.closeSession();
		}
	}


	private Integer queryChirp(Concept enrollmentConcept, Concept followUpConcept) {

		ChicaService chicaService = Context.getService(ChicaService.class);
		ObsService obsService = Context.getObsService();

		Date startDate = DateUtil.parseYmd(startDateProperty);
		Date stopDate = DateUtil.parseYmd(stopDateProperty);
		
		List<Encounter> encounters = chicaService
				.getEncountersForEnrolledPatients(enrollmentConcept,
						startDate, stopDate);

		//if encounter count is null set count to 0
		// add count to info log
		
		int size = (encounters == null ? 0 : encounters.size());
		
		log.info("Number of HPV enrollment encounters for encounters starting " + startDateProperty 
				+ " through " + stopDateProperty + " : " + size);

		for (Encounter encounter : encounters) {

			try {

				//Do not query chirp if this obs already exists for the encounter.
			
				Integer obsCount = obsService.getObservationCount(Collections.singletonList((Person) encounter.getPatient()), 
						Collections.singletonList((org.openmrs.Encounter) encounter), 
						Collections.singletonList(followUpConcept), null, null, null, null, null, null, false);
				
				// If this observation already exists for this encounter
				if (obsCount > 0) {
					continue;
				}

				String queryResponse = ImmunizationRegistryQuery.queryCHIRP(encounter);

				/* The method queryCHIRP() returns null for any issues with query such
				 * as CHIRP availability, parse errors, no patient match, etc.
				 * It also handles saving the observations for these CHIRP issues.
				 */
				
				if (queryResponse == null) {
					//If null response is due to CHIRP availability, then stop. CHIRP might be down during off hours
					ConceptService conceptService = Context.getConceptService();
					Concept statusConcept = conceptService .getConceptByName(CHIRP_STATUS_CONCEPT);
					Date now = new Date();
					Integer mostRecentCount = 1;
					List<Obs> latestChirpAvailabilityObs = obsService.getObservations(Collections.singletonList((Person)encounter.getPatient()), 
							Collections.singletonList((org.openmrs.Encounter) encounter), 
							Collections.singletonList(statusConcept), 
							null, null, null, null, mostRecentCount, null, DateUtil.getStartOfDay(now), now, false);
					if (latestChirpAvailabilityObs != null ) {
						Obs status = latestChirpAvailabilityObs.get(0);
						if (CHIRP_NOT_AVAILABLE.equals(status.getConcept().getName())){
							log.error("HPV Study: Follow-up CHIRP query problems due to CHIRP availability.");
							shutdown();
						}
					}
					
					continue;
				}

				ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
						.getImmunizationList(encounter.getPatientId());
				
				String identifier = encounter.getPatient().getPatientIdentifier().toString();
				if (immunizations == null) {
					log.info("HPV Study: Vaccine requery found no immunizations in CHIRP for patient: " + identifier);
					continue;
				}

				// patient has immunization records
				
				HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
						.getImmunizationPrevious();

				if (prevImmunizations == null){
					log.info("HPV Study: There are no historical vaccinations in CHIRP for patient: " + identifier);
					continue;
				}

				Integer count = 0;
				HashMap<Integer, ImmunizationPrevious> HpvHistory = prevImmunizations.get(HPV_VACCINE_NAME);
				for(ImmunizationPrevious value : HpvHistory.values()){
					if (value.getDate().before(encounter.getEncounterDatetime())){
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

				log.info("Follow-up HPV count at encounter for patient: " + identifier
						+ " HPV doses: " + count);																				

			} catch (Exception e) {
				log.error(" HPV Study exception for encounter = "
						+ encounter.getId() + " patient: "
						+ encounter.getPatientId());
				continue;
			}

		}

		return null;

	}

	@Override
	public void shutdown() {
		super.shutdown();
		log.info("HPV study: Shutting down hpv follow-up scheduled task.");
	}
	

}
