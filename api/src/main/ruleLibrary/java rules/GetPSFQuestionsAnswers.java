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
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 * Rule used to populate the PSF questions and answers.
 * 
 * @author Steve McKee
 */
public class GetPSFQuestionsAnswers implements Rule {

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer encounterId = (Integer)parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		if (encounterId == null) {
			return Result.emptyResult();
		}
		
		List<PSFQuestionAnswer> psfResults = getQuestionsAnswers(patientId, encounterId);
		if (psfResults.isEmpty()) {
			return Result.emptyResult();
		}
		
		Result results = new Result();
		for (int i = 0; i < psfResults.size(); i++) {
			PSFQuestionAnswer psfResult = psfResults.get(i);
			Result questAnswerResult = new Result();
			questAnswerResult.setResultObject(psfResult);
			results.add(questAnswerResult);
		}
		
		return results;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
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
		return 0;
	}
	
	/**
	 * Retrieves the PSF questions and answers for the current patient and encounter.
	 * 
	 * @param patientId The patient ID used to find the PSF questions and answers.
	 * @param encounterId The encounter ID used to find the PSF questions and answers.
	 * @return List of PSFQuestionAnswer objects containing the PSF questions and answers.
	 */
	private List<PSFQuestionAnswer> getQuestionsAnswers(Integer patientId, Integer encounterId) {
		ATDService atdService = Context.getService(ATDService.class);
		String patientForm = org.openmrs.module.chica.util.Util.getPrimaryFormNameByLocationTag(encounterId, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
		boolean includeNullObs = true;
		List<Statistics> stats = atdService.getStatsByEncounterForm(encounterId, patientForm, includeNullObs);
		if (stats == null || stats.isEmpty()) {
			return new ArrayList<>();
		}
		
		for (Statistics stat : stats) {
			Integer formInstanceId = stat.getFormInstanceId();
			Integer locationId = stat.getLocationId();
			if (formInstanceId != null && locationId != null) {
				return Context.getService(ATDService.class).getPatientFormQuestionAnswers(formInstanceId, locationId, patientId, patientForm);
			}
		}
		
		return new ArrayList<>();
	}
}
