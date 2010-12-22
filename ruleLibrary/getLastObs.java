package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.chica.util.Util;

public class getLastObs implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	/**
	 * Limits results by a given encounter id
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient, java.util.Map)
	 */
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		if(parameters == null)
		{
			return Result.emptyResult();
		}
		
		String conceptName = (String) parameters.get("param1");
		
		if(conceptName == null)
		{
			return Result.emptyResult();
		}
		Result ruleResult = null;
		
		Integer encounterId = (Integer) parameters.get("encounterId");
		
		if(encounterId == null){
			return Result.emptyResult();
		}
		
		LogicCriteria conceptCriteria = new LogicCriteria(
				conceptName);
		
		LogicCriteria fullCriteria = null;
		
			LogicCriteria encounterCriteria = 
				new LogicCriteria("encounterId").equalTo(encounterId);
			
			fullCriteria = conceptCriteria.and(encounterCriteria);

		ruleResult = context.read(patient,context.getLogicDataSource("obs"), 
				fullCriteria.last());

		if (ruleResult != null&&ruleResult.size()>0)
		{
			return ruleResult.get(0);
		}
		return Result.emptyResult();
	}
}