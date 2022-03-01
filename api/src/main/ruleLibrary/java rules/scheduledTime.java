package org.openmrs.module.chica.rule;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import java.text.ParseException;

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
		
		if (encounterAttributeValue == null) {
			return Result.emptyResult(); 
		}
		
		String scheduledTimeString  = encounterAttributeValue.getValueText();
	
		try {
			Date scheduledDate =new SimpleDateFormat(ChirdlUtilConstants.DATE_FORMAT_HYPHEN_yyyy_MM_dd_hh_mm_ss).parse(scheduledTimeString); 
			return new Result(scheduledDate);
		} catch (ParseException e) {
			//ignore
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
