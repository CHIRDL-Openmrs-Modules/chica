package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.util.Util;

public class LookupBPcentile implements Rule
{
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
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
				
		ChicaService chicaService = (ChicaService)Context.getService(ChicaService.class);

		if (parameters != null)
		{    	
        	try {
	        	String bpType = (String) parameters.get("param1");
	        	Integer whichCentile = Integer.parseInt((String) parameters.get("param2"));	
	        	Double patientHtCentile = Double.parseDouble((String) parameters.get("param3"));
	        	        	    		
	        	Double pcile = chicaService.getHighBP(patient, whichCentile, bpType, 
	        			patientHtCentile,new Date());
        	
        	return new Result(pcile);
        	} catch(Exception e){
    			return Result.emptyResult();
    		}
		}
        else
        {
        	return Result.emptyResult();
        }
	}
	
}