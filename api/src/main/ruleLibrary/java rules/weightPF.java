package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.util.Util;

public class weightPF implements Rule
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

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		Integer locationId = (Integer) parameters.get("locationId");
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		if(location!= null){
			//if this is Pecar, just return "."
			if(location.getName().equalsIgnoreCase("PEPS")){
				return dotResult();
			}else{
				//return "." or "#" based on age
				return dotOrPoundResult(parameters);
			}
		}

		return Result.emptyResult();
	}
	
	private Result dotOrPoundResult(Map<String, Object> parameters){
		String units = null;
		if (parameters != null && parameters.get("param0") != null)
		{
			Result ruleResult = (Result) parameters.get("param0");
			if (ruleResult != null)
			{
				Date result = ruleResult.toDatetime();

				if (result != null)
				{
					int ageMonths = org.openmrs.module.chirdlutil.util.Util
							.getAgeInUnits(result, null,
									Util.MONTH_ABBR);

					if (ageMonths > 18)
					{
						units = ".";

					} else
					{
						units = "#";

					}
					return new Result(units);
				}
			}
		}
		return null;
	}
	
	private Result dotResult(){
		return new Result(".");	
	}
}