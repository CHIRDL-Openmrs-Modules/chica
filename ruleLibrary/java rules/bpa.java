package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.util.Util;

public class bpa implements Rule
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
		Result ruleResult = null;
		
		Integer age = patient.getAge();
		ChicaService chicaService = Context.getService(ChicaService.class);
		Result diasResult = null;
		Result systResult = null;

		String conceptName = "SYSTOLIC_BP";
		Integer encounterId = (Integer) parameters.get("encounterId");

		LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);

		LogicCriteria fullCriteria = null;
		LogicCriteria encounterCriteria = null;

		if (encounterId != null)
		{
			encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());

			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else
		{
			fullCriteria = conceptCriteria;
		}
		systResult = context.read(patientId, context.getLogicDataSource("obs"),
				fullCriteria.last());

		conceptName = "DIASTOLIC_BP";
		encounterId = (Integer) parameters.get("encounterId");

		conceptCriteria = new LogicCriteriaImpl(conceptName);

		fullCriteria = null;
		encounterCriteria = null;

		if (encounterId != null)
		{
			encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());

			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else
		{
			fullCriteria = conceptCriteria;
		}
		diasResult = context.read(patientId, context.getLogicDataSource("obs"),
				fullCriteria.last());

		Double diasBPNum = null;
		Double systBPNum = null;
		
		if(diasResult != null && systResult!= null)
		{
			diasBPNum = diasResult.toNumber();
			systBPNum = systResult.toNumber();
		}
		
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		if(age >=12)
		{
			if (diasBPNum != null)
			{
				Double diastolic95 = chicaService.getHighBP(patient, 95,
						"diastolic", encounter);

				if (diastolic95 != null)
				{
					if (diasBPNum >= diastolic95)
					{
						return new Result("*");
					}
				}
			}

			if (systBPNum != null)
			{
				Double systolic95 = chicaService.getHighBP(patient, 95,
						"systolic", encounter);

				if (systolic95 != null)
				{
					if (systBPNum >= systolic95)
					{
						return new Result("*");
					}
				}
			}
		}
		return Result.emptyResult();
	}
}