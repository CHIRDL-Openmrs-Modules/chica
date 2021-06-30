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
package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;


/**
 *
 * @author Steve McKee
 */
public class physicianNotePSFResults implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			this.log.error("Patient cannot be found with ID: " + patientId);
			System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
			return Result.emptyResult();
		}
		
		Integer encounterId = Util.getIntegerFromMap(parameters, ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		if (encounterId == null) {
			this.log.error("Cannot determine encounter ID.  No note will be created.");
			return Result.emptyResult();
		}
		
		Integer locationTagId = Util.getIntegerFromMap(parameters, ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
		if (locationTagId == null) {
			this.log.error("Cannot determine location tag ID.  No note will be created.");
			return Result.emptyResult();
		}
		
		Integer locationId = Util.getIntegerFromMap(parameters, ChirdlUtilConstants.PARAMETER_LOCATION_ID);
		if (locationId == null) {
			this.log.error("Cannot determine location ID.  No note will be created.");
			return Result.emptyResult();
		}
		
		String obsNote = buildObsNote(patient, encounterId, locationId, locationTagId);
		if (obsNote.trim().length() > 0) {
			System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
			return new Result(obsNote);
		}
		
		System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
     * Builds a note with all observations for the day containing the provided question Concept.
     * 
     * @param patient The patient used to retrieve the observations.
     * @param encounterId The current encounter identifier
     * @param locationId The location identifier
     * @param locationTagId The location tag identifier
     * @return String containing a note with the observations for the day for the provided patient and question Concept.  
     * This will not return null.
     */
    private String buildObsNote(Patient patient, Integer encounterId, Integer locationId, Integer locationTagId) {
    	Concept noteConcept = Context.getConceptService().getConceptByName("CHICA_Note");
		if (noteConcept == null) {
			this.log.error(
				"Physician note observations cannot be constructed because concept \"CHICA_Note\" does not exist.");
			return ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
		}
		
		LocationTagAttributeValue attrVal = 
				Context.getService(ChirdlUtilBackportsService.class).getLocationTagAttributeValue(
					locationTagId, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM, locationId);
		if (attrVal == null || StringUtils.isBlank(attrVal.getValue())) {
			return ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
		}
		
		String formName = attrVal.getValue();
		ATDService atdService = Context.getService(ATDService.class);
		List<Statistics> stats = atdService.getStatsByEncounterForm(encounterId, formName);
		if (stats == null || stats.isEmpty()) {
			return ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
		}
		
		ObsService obsService = Context.getObsService();
		List<Obs> obs = new ArrayList<>();
		for (Statistics stat : stats) {
			Integer obsId = stat.getObsvId();
			if (obsId == null) {
				continue;
			}
			
			Obs ob = obsService.getObs(obsId);
			if (ob != null && ob.getConcept() != null && noteConcept.equals(ob.getConcept())) {
				obs.add(ob);
			}
		}
		
		return Util.createClinicalNote(obs);
    }
}
