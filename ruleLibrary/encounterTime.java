package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class encounterTime implements Rule
{

	private LogicService logicService = Context.getLogicService();

	/**
	 * @see org.openmrs.logic.rule.Rule#eval(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		EncounterService encounterService = (EncounterService) Context
				.getService(EncounterService.class);

		Encounter encounter = null;
		Integer encounterId = (Integer) parameters.get("encounterId");

		if (encounterId != null)
		{
			encounter = (Encounter) encounterService.getEncounter(encounterId);
			if(encounter != null)
			{
				return new Result(encounter.getEncounterDatetime());
			}
		}

		return Result.emptyResult();
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; 
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}
}
