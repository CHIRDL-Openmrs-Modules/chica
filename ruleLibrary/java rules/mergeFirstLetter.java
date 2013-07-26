/********************************************************************
 Translated from - mcad.mlm on Fri Dec 28 15:10:34 EST 2007

 Title : MCAD Reminder
 Filename:  mcad
 Version : 0 . 2
 Institution : Indiana University School of Medicine
 Author : Steve Downs
 Specialist : Pediatrics
 Date : 05 - 22 - 2007
 Validation :
 Purpose : Provides a specific reminder, tailored to the patient who identified one or more fatty acid disorders
 Explanation : Based on AAP screening recommendations
 Keywords : fatty, acid, fatty acid disorder
 Citations : Screening for fatty acid disorder AAP
 Links :

 ********************************************************************/
package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class mergeFirstLetter implements Rule {
	
	private LogicService logicService = Context.getLogicService();
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null) {
			return Result.emptyResult();
		}
		
		String conceptName = (String) parameters.get("concept");
		
		if (conceptName == null) {
			return Result.emptyResult();
		}
		
		Result ruleResult = null;
		
		Integer encounterId = (Integer) parameters.get("encounterId");
		
		LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
		
		LogicCriteria fullCriteria = null;
		
		if (encounterId != null) {
			LogicCriteria encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			
			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else {
			fullCriteria = conceptCriteria;
		}
		
		ruleResult = context.read(patientId, context.getLogicDataSource("obs"), fullCriteria.last());
		
		if (ruleResult != null && ruleResult.size() > 0) {
			ruleResult = ruleResult.get(0);
			String value = ruleResult.toString();
			if (value == null || value.length() < 1) {
				return Result.emptyResult();
			}
			
			ruleResult = new Result(value.substring(0, 1));
			return ruleResult;
		}
		
		return Result.emptyResult();
	}
}
