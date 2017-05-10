package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

public class pwsId implements Rule
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
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		if (parameters != null)
		{
			Integer encounterId = (Integer) parameters.get("encounterId");
			
			if (encounterId != null)
			{
				String formName = "PWS";
				FormService formService = Context.getFormService();
				Form form = formService.getForm(formName);
				Integer formId = null;
				if (form != null)
				{
					formId = form.getFormId();
				}
				if (formId != null)
				{
					PatientState patientState = org.openmrs.module.atd.util.Util.getProducePatientStateByEncounterFormAction(
						encounterId, formId);
					if (patientState != null)
					{
						return new Result(String.valueOf(patientState
								.getFormInstanceId()));
					}
				}
			}
		}
		return Result.emptyResult();
	}

}