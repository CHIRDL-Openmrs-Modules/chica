package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class scheduledTime implements Rule
{

	private LogicService logicService = Context.getLogicService();

	/**
	 * @see org.openmrs.logic.rule.Rule#eval(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria) 
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		
		Encounter encounter = null;
		Integer encounterIdParam = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
 
		if (encounterIdParam == null) {
			return Result.emptyResult();
		}
		
		EncounterService encounterService = Context.getEncounterService();
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		encounter =  encounterService.getEncounter(encounterIdParam);
		if(encounter == null){
			return Result.emptyResult();
		}
		
		EncounterAttributeValue encounterAttributeValue = chirdlUtilBackportsService
				.getEncounterAttributeValueByName( encounter.getEncounterId(),ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		
		if (encounterAttributeValue == null || encounterAttributeValue.getValueDateTime() == null) {
			return Result.emptyResult(); 
		}
		return new Result(encounterAttributeValue.getValueDateTime());

	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	@Override
	public int getTTL()
	{
		return 0; 
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	@Override
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}
}
