package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
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

public class bp implements Rule
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
		Result diasResult = null;
		Result systResult = null;

		String conceptName = "SYSTOLIC_BP";
		Integer encounterId = (Integer) parameters.get("encounterId");

		LogicCriteria conceptCriteria = new LogicCriteria(conceptName);

		LogicCriteria fullCriteria = null;
		LogicCriteria encounterCriteria = null;

		if (encounterId != null)
		{
			encounterCriteria = new LogicCriteria("encounterId").equalTo(encounterId);

			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else
		{
			fullCriteria = conceptCriteria;
		}
		systResult = context.read(patient, context.getLogicDataSource("obs"),
				fullCriteria.last());

		conceptName = "DIASTOLIC_BP";
		encounterId = (Integer) parameters.get("encounterId");

		conceptCriteria = new LogicCriteria(conceptName);

		fullCriteria = null;
		encounterCriteria = null;

		if (encounterId != null)
		{
			encounterCriteria = new LogicCriteria("encounterId").equalTo(encounterId);

			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else
		{
			fullCriteria = conceptCriteria;
		}
		diasResult = context.read(patient, context.getLogicDataSource("obs"),
				fullCriteria.last());

		if(diasResult != null && systResult!= null)
		{
			Double diasBPNum = diasResult.toNumber();
			Double systBPNum = systResult.toNumber();
			
			if(diasBPNum != null && systBPNum != null)
			{
				String diasBloodPressure = String.valueOf(diasBPNum.intValue());
				String systBloodPressure = String.valueOf(systBPNum.intValue());

				return new Result(systBloodPressure + "/" + diasBloodPressure);
			}
		}
		return Result.emptyResult();
	}
}