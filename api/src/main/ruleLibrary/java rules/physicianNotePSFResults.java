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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		
		Integer encounterId = null;
		Object encounterIdObj = parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		if (encounterIdObj instanceof Integer) {
			encounterId = (Integer)encounterIdObj;
		} else if (encounterIdObj instanceof String) {
			String encounterIdStr = (String)encounterIdObj;
			try {
				encounterId = Integer.valueOf(encounterIdStr);
			} catch (NumberFormatException e) {
				this.log.error("Error parsing value " + encounterIdStr + " into an encounter ID integer.", e);
				return Result.emptyResult();
			}
		} else {
			this.log.error("Cannot determine encounter ID.  No note will be created.");
			return Result.emptyResult();
		}
		
		String note = buildPSFNote(patientId, encounterId);
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
	
	private String buildPSFNote(Integer patientId, Integer encounterId) {
		StringBuilder noteBuffer = new StringBuilder();
		List<PSFQuestionAnswer> psfVals = getQuestionsAnswers(patientId, encounterId);
		if (psfVals.isEmpty()) {
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
	
	private List<PSFQuestionAnswer> getQuestionsAnswers(Integer patientId, Integer encounterId) {
		String lastFormName = Util.getPrimaryFormNameByLocationTag(
			encounterId, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
		ATDService atdService = Context.getService(ATDService.class);
		
		List<Statistics> stats = atdService.getStatsByEncounterForm(encounterId, lastFormName);
		if (stats == null || stats.isEmpty()) {
			return new ArrayList<>();
		}
		
		Statistics stat = stats.get(0);
		Integer formInstanceId = stat.getFormInstanceId();
		Integer locationId = stat.getLocationId();
		return Context.getService(ATDService.class).getPatientFormQuestionAnswers(
			formInstanceId, locationId, patientId, lastFormName);
	}
}
