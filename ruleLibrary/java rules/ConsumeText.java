package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.logic.op.OperandObject;


/**
 * DWE CLINREQ-90 Added rule to consume text
 *
 */
public class ConsumeText implements Rule
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
		return Datatype.TEXT;
	}
	
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		FormInstance formInstance = null;
		String conceptName = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		Integer formFieldId = null;
		String fieldName = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");

			conceptName = (String) parameters.get("concept");

			ruleId = (Integer) parameters.get("ruleId");
			locationTagId = (Integer) parameters.get("locationTagId");

			if (conceptName == null)
			{
				return Result.emptyResult();
			}
			encounterId = (Integer) parameters.get("encounterId");
			fieldName = (String) parameters.get("fieldName");
			formFieldId = (Integer)parameters.get("formFieldId");
		}

		LogicCriteria formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS, new OperandObject(formInstance));

		LogicCriteria fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("form"), formIdCriteria);


		ConceptService conceptService = Context.getConceptService();

		Concept currConcept = conceptService.getConceptByName(conceptName);

		// This doesn't exist in this branch, but will be needed with the Prod_Release_1.0 branch
		org.openmrs.module.chica.util.Util.voidObsForConcept(currConcept, encounterId, formFieldId);

		Obs obs = org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, ruleResult.toString(), formInstance,
				ruleId,locationTagId, formFieldId);

		if (obs == null) {
			return Result.emptyResult();
		}

		return new Result(obs);
	}
}