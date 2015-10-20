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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.PSFQuestionAnswer;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 * Retrieves a question or answer from the psf question/answer pair PSFQuestionAnswer object by a one-based index.
 * 
 * @author Steve McKee
 */
public class GetPSFQuestionAnswerByIndex implements Rule {
	
	private static final String QUESTION = "question";
	private static final String ANSWER = "answer";
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Result results = (Result)parameters.get(ChirdlUtilConstants.PARAMETER_1);
		if (results == null || results.size() == 0) {
			return Result.emptyResult();
		}
		
		String indexStr = (String)parameters.get(ChirdlUtilConstants.PARAMETER_2);
		if (indexStr == null || indexStr.trim().length() == 0) {
			return Result.emptyResult();
		}
		
		int index = 0;
		try {
			index = Integer.parseInt(indexStr);
		} catch (NumberFormatException e) {
			return Result.emptyResult();
		}
		
		// Convert to zero-based
		index--;
		
		if (index > results.size() - 1) {
			return Result.emptyResult();
		}
		
		Result result = results.get(index);
		if (result == null) {
			return Result.emptyResult();
		}
		
		Object resultObj = result.getResultObject();
		if ((resultObj == null) || !(resultObj instanceof PSFQuestionAnswer)) {
			return Result.emptyResult();
		}
		
		PSFQuestionAnswer psfQuestAnswer = (PSFQuestionAnswer)resultObj;
		String value = (String)parameters.get(ChirdlUtilConstants.PARAMETER_3);
		if (QUESTION.equalsIgnoreCase(value)) {
			return new Result(psfQuestAnswer.getQuestion());
		} else if (ANSWER.equalsIgnoreCase(value)) {
			return new Result(psfQuestAnswer.getAnswer());
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
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
		return new HashSet<RuleParameterInfo>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
}
