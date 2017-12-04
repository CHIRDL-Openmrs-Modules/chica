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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.PSFQuestionAnswer;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 *
 * @author Steve McKee
 */
public class physicianNotePSFResults implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		String note = buildPSFNote(patientId);
		if (note.trim().length() > 0) {
			System.out.println("chicaNotePSFResults: " + (System.currentTimeMillis() - startTime) + "ms");
			return new Result(note);
		}
		
		System.out.println("chicaNotePSFResults: " + (System.currentTimeMillis() - startTime) + "ms");
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	private String buildPSFNote(Integer patientId) {
		StringBuffer noteBuffer = new StringBuffer();
		List<PSFQuestionAnswer> psfVals = getQuestionsAnswers(patientId);
		if (psfVals.size() == 0) {
			return noteBuffer.toString();
		}
		
		noteBuffer.append("PATIENT SURVEY RESULTS\n");
		noteBuffer.append("Response--Question\n");
		for (PSFQuestionAnswer val : psfVals) {
			noteBuffer.append(val.getAnswer());
			noteBuffer.append("--");
			noteBuffer.append(val.getQuestion());
			noteBuffer.append("\n");
		}
		
		noteBuffer.append("\n");
		return noteBuffer.toString();
	}
	
	private List<PSFQuestionAnswer> getQuestionsAnswers(Integer patientId) {
		
		// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - Util.getFormTimeLimit());
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		Patient patient = Context.getPatientService().getPatient(patientId);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return new ArrayList<PSFQuestionAnswer>();
		}

		Encounter lastEncounter = null;
		String lastFormName = null;
		if (encounters.size() == 1) {
			lastEncounter =  encounters.get(0);
			lastFormName = Util.getPrimaryFormNameByLocationTag((org.openmrs.module.chica.hibernateBeans.Encounter) lastEncounter, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
		} else {
			// Do a check to find the latest encounters with observations with a scanned timestamp for the PSF.
			ATDService atdService = Context.getService(ATDService.class);
			for (int i = encounters.size() - 1; i >= 0 && lastEncounter == null; i--) {
				Encounter encounter = encounters.get(i);
				lastFormName = Util.getPrimaryFormNameByLocationTag(encounter.getEncounterId(), ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
				List<Statistics> stats = atdService.getStatsByEncounterForm(encounter.getEncounterId(), lastFormName);
				if (stats == null || stats.size() == 0) {
					continue;
				}
				
				for (Statistics stat : stats) {
					if (stat.getScannedTimestamp() != null) {
						lastEncounter = encounter;
						break;
					}
				}
			}
		}
		
		if (lastEncounter == null) {
			return new ArrayList<PSFQuestionAnswer>();
		}

		ATDService atdService = Context.getService(ATDService.class);
		List<Statistics> stats = atdService.getStatsByEncounterForm(lastEncounter.getEncounterId(), lastFormName);
		if (stats == null || stats.size() == 0) {
			return new ArrayList<PSFQuestionAnswer>();
		}
		
		Statistics stat = stats.get(0);
		Integer formInstanceId = stat.getFormInstanceId();
		Integer locationId = stat.getLocationId();
		return Context.getService(ATDService.class).getPatientFormQuestionAnswers(formInstanceId, locationId, patientId, lastFormName);
	}
}
