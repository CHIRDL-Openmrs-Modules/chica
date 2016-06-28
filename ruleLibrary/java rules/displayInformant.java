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

public class displayInformant implements Rule
{
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
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		if(parameters == null) {
			return Result.emptyResult();
		}
		
		String conceptName = (String) parameters.get("concept");
		if(conceptName == null) {
			return Result.emptyResult();
		}
		
		Integer encounterId = (Integer) parameters.get("encounterId");
		LogicCriteria criteria = new LogicCriteriaImpl(conceptName);

		if (encounterId!= null) {
			LogicCriteria encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			criteria = criteria.and(encounterCriteria);
			Result result = context.read(patientId, context.getLogicDataSource("obs"), criteria.last());
			return new Result(result.toString());
		}
		return Result.emptyResult();
	}
}