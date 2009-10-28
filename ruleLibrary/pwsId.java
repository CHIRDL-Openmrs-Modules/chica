package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.atd.hibernateBeans.Session;

public class pwsId implements Rule
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
		ATDService atdService = Context.getService(ATDService.class);
		if (parameters != null)
		{
			Integer encounterId = (Integer) parameters.get("encounterId");
			
			if (encounterId != null)
			{
				String formName = "PWS";
				FormService formService = Context.getFormService();
				Form form = formService.getForms(formName,null,null,false,null,null,null).get(0);
				Integer formId = null;
				if (form != null)
				{
					formId = form.getFormId();
				}
				if (formId != null)
				{
					PatientState patientState = atdService
							.getPatientStateByEncounterFormAction(encounterId,
									formId, "PRODUCE FORM INSTANCE");
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