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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.lang.jstl.IntegerLiteral;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
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
	private List<Integer> followUpIntervals = new ArrayList<Integer>();
	
	private String PROPERTY_NAME_CONCEPT = "concept";
	private String TWO_WEEKS = "2wk";
	private String FOUR_MONTHS = "4mo";

	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);
		this.taskConfig = config;
		
		try {
			log.info("Initializing vaccine follow-up scheduled task.");
	
			Map<String,String> propertyMap = this.taskConfig.getProperties();
			for (Map.Entry<String, String> parameter : propertyMap.entrySet()) {
			    String parameterName = parameter.getKey();
			    String value = null;
			    if (!parameterName.isEmpty() && parameterName.startsWith("interval")
			    		&& isInteger( value = parameter.getValue() ))
			    	{
			    	Integer  interval = Integer.valueOf(value);
			    	followUpIntervals.add(interval);
			    }
			    
			}
		    
		    conceptProperty = this.taskConfig.getProperty(PROPERTY_NAME_CONCEPT);
		    
			//perform2wkFollupUp = this.taskConfig.getProperty(PROPERTY_NAME_TWO_WEEK);
			//conceptProperty = this.taskConfig.getProperty(PROPERTY_NAME_CONCEPT);
			//perform4moFollupUp = this.taskConfig.getProperty(PROPERTY_NAME_FOUR_MONTH_CHECK);
			//testTime = this.taskConfig.getProperty(PROPERTY_NAME_TEST_CHECK);
			//testPeriod = Integer.valueOf(testTime);
			
		}catch(Exception e){
			log.error(taskDefinition.getName() + " failed during initialize", e);
		}
	}


	@Override
	public void execute() {
		Context.openSession();


		ConceptService conceptService = Context.getConceptService();

		try {

				if (conceptProperty == null || conceptProperty.trim().equals("")) {
					log.error("HPV study: Task property '" + PROPERTY_NAME_CONCEPT + "' does not exist for this patient for this task. ");
					return;
				}
				
				Concept enrollmentConcept = conceptService.getConceptByName(conceptProperty);
				if (enrollmentConcept == null) {
					log.error ("HPV study:  Task property '" + PROPERTY_NAME_CONCEPT + "' is not a valid concept");
					return;
				}
				
				followUpCheck(enrollmentConcept, followUpIntervals);
				
		} catch (Throwable e) {
			log.error("HPV study: Error during vaccine follow-up check.", e);
		} finally {
			Context.closeSession();
		}
	}


	private Integer followUpCheck(Concept enrollmentConcept,
			List<Integer> timePeriods) {

		ConceptService conceptService = Context.getConceptService();
		ChicaService chicaService = Context.getService(ChicaService.class);

		for (Integer followUpTimePeriod : timePeriods){
			
			
			
			log.info("HPV study: Lookup enrollments for encounters " + followUpTimePeriod + " days ago");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -(followUpTimePeriod));
			Date startDateTime = DateUtil.getStartOfDay(c.getTime());
			Date stopDatetime = DateUtil.getEndOfDay(c.getTime());

			List<Encounter> encounters = chicaService
					.getEncountersForEnrolledPatients(enrollmentConcept,
							startDateTime, stopDatetime);

			SimpleDateFormat sdf = new SimpleDateFormat("mm-dd-yyyy");

			log.info("HPV study: " + encounters == null ? "0" : encounters.size()    
					+ "encounters found for date "  + sdf.format(startDateTime) );


			for (Encounter encounter : encounters) {


				try {
					
					//check if observation chirp not available exists for the start and stop dates
					//for this patient
					String queryResponse = ImmunizationRegistryQuery
							.queryCHIRP(encounter);

					//queryCHIRP() returns null for any issues with query such as CHIRP availability, parse errors, no patient match, etc
					//queryCHIRP() handles saving the chirp status observations for these issues.
					if (queryResponse == null) {
						log.error("	HPV Study: Unable to access immunization records from "
								+ " chirp for  followup. MRN: "
								+ encounter.getPatient().getPatientIdentifier());
						continue;
					}

					ImmunizationQueryOutput immunizations = ImmunizationForecastLookup
							.getImmunizationList(encounter.getPatientId());

					if (immunizations == null) {
						log.error("	HPV Study: The immunization list is empty after the query to "
								+ " chirp for  followup. MRN: "
								+ encounter.getPatient().getPatientIdentifier());
						continue;
					}

					// patient has immunization records

					Integer hpvDoses = 0;
					Integer TdapDoses = 0;
					Integer MCVDoses = 0;
					//
					HashMap<String, String> map = this.setupVISNameLookup();
					String HPVName = map.get("HPV");
					String TdapName = map.get("Tdap");
					String MCVName = map.get("MCV");

					HashMap<String, HashMap<Integer, ImmunizationPrevious>> prevImmunizations = immunizations
							.getImmunizationPrevious();

					if (prevImmunizations != null) {

						// HPV
						HashMap<Integer, ImmunizationPrevious> HPVGiven = prevImmunizations
								.get(HPVName);
						if (HPVGiven != null) {
							hpvDoses = HPVGiven.size();
						}

						// Tdap

						HashMap<Integer, ImmunizationPrevious> TdapGiven = prevImmunizations
								.get(TdapName);
						if (TdapGiven != null) {
							TdapDoses = TdapGiven.size();
						}

						// MCV

						HashMap<Integer, ImmunizationPrevious> MCVGiven = prevImmunizations
								.get(MCVName);
						if (MCVGiven != null) {
							MCVDoses = MCVGiven.size();
						}

					}

					String followUpType = null;	
					if (followUpTimePeriod == 14 ) followUpType = TWO_WEEKS;
					if (followUpTimePeriod == 120) followUpType = FOUR_MONTHS;
					if (followUpType != null){
						Concept hpvConcept = conceptService.getConceptByName(followUpType + "_HPV");
						saveObs(encounter.getPatient(), hpvConcept,
								null, hpvDoses.toString());

						Concept TdapConcept = conceptService.getConceptByName(followUpType + "_Tdap");
						saveObs(encounter.getPatient(), TdapConcept,
								null, TdapDoses.toString());

						Concept MCVConcept = conceptService.getConceptByName(followUpType + "_MCV");
						saveObs(encounter.getPatient(), MCVConcept,
								null, MCVDoses.toString());
						return null;

					}
					
					// Follow-up is not a defined follow-up time period for the HPV Study, but write results to log
					log.info("Follow-up check for time period " + followUpTimePeriod + " days. Patient: " + encounter.getPatientId() 
							+ " HPV doses: " + hpvDoses
							+ " MCV doses: " + MCVDoses 
							+ " TDaP doses: " + TdapDoses);
					
				} catch (Exception e) {
					log.error(" HPV Study exception for encounter = " + encounter.getId() + " patient: " + encounter.getPatientId());
				}

			}
		}

		return null;

	}

	private HashMap<String, String> setupVISNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();


		map.put("DTaP", "DTaP, unspecified formulation");
		map.put("HepA", "Hep A, unspecified formulation");
		map.put("HepB", "Hep B, unspecified formulation");
		map.put("Hib","Hib, unspecified formulation");
		map.put("influenza", "Influenza, unspecified formulation");
		map.put("MMR", "MMR");
		map.put("PPV", "pneumococcal, unspecified formulation");
		map.put("PCV13", "Pneumococcal Conjugate, unspecified formulation");
		map.put("Rotavirus", "rotavirus, unspecified formulation");
		map.put("varicella", "Varicella");
		map.put("HPV", "HPV, unspecified formulation");
		map.put("flulive", "influenza, live, intranasal");
		map.put("IPV", "polio, unspecified formulation");
		map.put("MCV", "meningococcal MCV4, unspecified formulation");
		map.put("PPD", "TST-PPD intradermal");
		map.put("Tdap", "Td(adult) unspecified formulation");


		return map;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		log.info("HPV study: Shutting down hpv follow-up scheduled task.");
	}
	
	private boolean isInteger(String str)
	{
		try
		{
			Integer.parseInt(str);
			return true;
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
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


}
