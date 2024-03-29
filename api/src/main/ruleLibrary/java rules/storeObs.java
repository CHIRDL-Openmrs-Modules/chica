package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
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
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chica.util.Util;

public class storeObs implements Rule
{
	private static final Logger log = LoggerFactory.getLogger(storeObs.class);
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
		FormInstance formInstance = null;
		String conceptName = null;
		Integer encounterId = null;
		Integer ruleId = null;
		String value = null;
		Integer locationTagId = null;
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		Integer formFieldId = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");

			conceptName = (String) parameters.get("param1");

			ruleId = (Integer) parameters.get("ruleId");
			locationTagId = (Integer) parameters.get("locationTagId");

			if (conceptName == null)
			{
				return Result.emptyResult();
			}
			encounterId = (Integer) parameters.get("encounterId");
			value = (String) parameters.get("param2");
			formFieldId = (Integer)parameters.get("formFieldId"); // DWE CHICA-437
		}
		ConceptService conceptService = Context.getConceptService();

		Concept currConcept = conceptService.getConceptByName(conceptName);

		Integer formInstanceId = null;

		if(formInstance != null){
			formInstanceId = formInstance.getFormInstanceId();
		}
		
		Integer formId = null;
		
		if(formInstance != null){
			formId = formInstance.getFormId();
		}

		Obs obs = org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, value, formInstance,
				ruleId,locationTagId, formFieldId); // DWE CHICA-437 Added formFieldId
		
		if (obs == null) {
			return Result.emptyResult();
		}

		return new Result(obs);
	}
}