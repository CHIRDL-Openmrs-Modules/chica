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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
 * Scheduled task to determine if the physician administered the vaccine at or after the visit.
 */
public class VaccineFollowUp extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());

	private TaskDefinition taskConfig;
	private String conceptProperty; //String containing concept name
	private static final String PROPERTY_KEY_CONCEPT = "concept";
	private static final String FOLLOWUP_CONCEPT_TWO_WEEK = "2wk_HPV";
	private static final String FOLLOWUP_CONCEPT_FOUR_MONTH = "4mo_HPV";
	private static final String HPV_VACCINE_NAME = "HPV, unspecified formulation";
	
	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);
		this.taskConfig = config;
		
		try {
			
			log.info("Initializing vaccine follow-up scheduled task.");
		    conceptProperty = this.taskConfig.getProperty(PROPERTY_KEY_CONCEPT);
		    
		}catch(Exception e){
			
			log.error(taskDefinition.getName() + " failed during initialize", e);
		}
	}


	@Override
	public void execute() {
		Context.openSession();

		ConceptService conceptService = Context.getConceptService();
		Concept twoWeekIntervalConcept = conceptService.getConceptByName(FOLLOWUP_CONCEPT_TWO_WEEK);
		Concept fourMonthIntervalConcept = conceptService.getConceptByName(FOLLOWUP_CONCEPT_FOUR_MONTH);

		try {

				if (conceptProperty == null || conceptProperty.trim().equals("")) {
					log.error("HPV study: Task property '" + PROPERTY_KEY_CONCEPT + " is not present in the property list for this task");
					return;
				}
				
				Concept enrollmentConcept = conceptService.getConceptByName(conceptProperty);
				if (enrollmentConcept == null) {
					log.error ("HPV study:  Task property '" + PROPERTY_KEY_CONCEPT + "' is not a valid concept");
					return;
				}
				
				followUpRequery(enrollmentConcept, twoWeekIntervalConcept, 14 );
				followUpRequery(enrollmentConcept, fourMonthIntervalConcept , 120);
				
		} catch (Exception e) {
			log.error("HPV study: Exception during vaccine follow-up check.", e);
		} finally {
			Context.closeSession();
		}
	}


	private Integer followUpRequery(Concept enrollmentConcept, Concept followUpConcept,  int lookupIntervalInDays) {


		ChicaService chicaService = Context.getService(ChicaService.class);
		ObsService obsService = Context.getObsService();

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE,  -lookupIntervalInDays);
		Date enrollmentStartDateTime = DateUtil.getStartOfDay(c.getTime());
		Date enrollmentStopDateTime = DateUtil.getEndOfDay(c.getTime());
		Date startOfToday = DateUtil.getStartOfDay(new Date()); 
		Date endOfToday = DateUtil.getEndOfDay(new Date());


		List<Encounter> encounters = chicaService
				.getEncountersForEnrolledPatients(enrollmentConcept,
						enrollmentStartDateTime, enrollmentStopDateTime);

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		log.info("HPV study: Follow-up task found " + encounters == null ? "0 " : encounters.size()
				+ " encounters found for date: "
				+ sdf.format(enrollmentStartDateTime));

		for (Encounter encounter : encounters) {

			try {

				/* 
				 * CHIRP may not have been available earlier in the day, so a requery
				 * task can be performed later in the day.
				 * If follow-up observations exist already for today,
				 * do not query for this encounter again. 
				 */
				String identifier = encounter.getPatient().getPatientIdentifier().toString();

				List<Person> patients = new ArrayList<Person>();
				patients.add(encounter.getPatient());
				
				List<Concept> concepts = new ArrayList<Concept>();
				concepts.add(followUpConcept);

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
				 * It also handles saving the observations for these CHIRP issues.
				 */

				if (queryResponse == null) {
					log.error("HPV Study: Follow-up CHIRP query problems due to CHIRP availablility.");
					continue;
				}

				ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
						.getImmunizationList(encounter.getPatientId());

				if (immunizations == null) {
					log.info("HPV Study: Vaccine requery found no immunizations in CHIRP for patient: " + identifier);
					continue;
				}

				// patient has immunization records
				Integer hpvDoses = 0;

				HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
						.getImmunizationPrevious();

				if (prevImmunizations == null){
					log.info("HPV Study: There are no historical vaccinations in CHIRP for patient: " + identifier);
					continue;
				}


				HashMap<Integer, ImmunizationPrevious> HpvHistory = prevImmunizations.get(HPV_VACCINE_NAME);
				hpvDoses = HpvHistory == null ?  0 : HpvHistory.size();
				
				Obs obs = new Obs();
				obs.setValueNumeric(hpvDoses.doubleValue());
				obs.setEncounter(encounter);
				obs.setPerson(encounter.getPatient());
				obs.setConcept(followUpConcept);
				obs.setObsDatetime(new Date());
				obsService.saveObs(obs, null);

				log.info("Follow-up check for time period " + lookupIntervalInDays
						+ " days. Patient: " + identifier
						+ " HPV doses: " + hpvDoses.toString());																				

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
