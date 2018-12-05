package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

import org.openmrs.module.chica.Calculator;

public class percentile implements Rule
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
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		
		if(parameters!=null)
		{
			Result ruleResult = (Result) parameters.get("param0");
			String conceptName = (String) parameters.get("concept");
			if(ruleResult != null)
			{
				Calculator calculator = new Calculator();
				String type = null;
				String measurementUnits = null;
				
				if(conceptName == null)
				{
					type = "bmi";
				}else if(conceptName.equalsIgnoreCase("HEIGHT"))
				{
					type = "length";
					measurementUnits = org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN;
				}else if(conceptName.equalsIgnoreCase("WEIGHT"))
				{
					type = "weight";
					measurementUnits = org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_LB;
				}else if(conceptName.equalsIgnoreCase("HC"))
				{
					type = "hc";
				}
				
				if (type != null)
				{
					Double result = ruleResult.toNumber();

					if (result != null)
					{
						String percentile = calculator
								.calculatePercentileAsString(result, patient
										.getGender(), patient.getBirthdate(),
										type, measurementUnits);
						if (percentile != null)
						{
							return new Result(percentile);
						}
					}
				}
			}
		}
		return Result.emptyResult();
	}

}