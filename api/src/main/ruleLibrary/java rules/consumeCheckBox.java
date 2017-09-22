package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.logic.op.OperandObject;

/**
 * The consumeCheckBox java rule converts and saves the value of the html checkbox to an observation.
 * @author Meena Sheley
 */
public class consumeCheckBox implements Rule
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
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Integer formFieldId = null; 

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
			fieldName = (String) parameters.get(ChirdlUtilConstants.PARAMETER_FIELD_NAME);
			conceptName = (String) parameters.get(ChirdlUtilConstants.PARAMETER_CONCEPT);
			
			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
			locationTagId = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
			ruleId = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_RULE_ID);
			formFieldId = (Integer)parameters.get(ChirdlUtilConstants.PARAMETER_FORM_FIELD_ID); // DWE CHICA-437
		}

		if (formInstance == null)
		{
			throw new LogicException(
					"The form datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS, new OperandObject(formInstance));
	
		LogicCriteria fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource(ChirdlUtilConstants.PARAMETER_FORM), formIdCriteria);
		
		ConceptService conceptService = Context.getConceptService();
		
		if(ruleResult != null&&ruleResult.toString()!=null&&
				ruleResult.toString().length()>0)
		{
			
			String enteredValue = ruleResult.toString();
			String answer = null;
			
			if(enteredValue.equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_Y) || enteredValue.equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_YES)){
				answer = ChirdlUtilConstants.GENERAL_INFO_YES;
			}
			
			// DWE CHICA-430 Allow the checkbox to be unchecked by voiding the obs for this concept
			Concept concept = conceptService.getConceptByName(conceptName);
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, formFieldId); // DWE CHICA-437 Added formFieldId
			
			if(answer != null){
				org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, conceptService.getConceptByName(conceptName),
						encounterId, answer,formInstance,ruleId,locationTagId, formFieldId); // DWE CHICA-437 Added formFieldId
			}
		}
		
		return Result.emptyResult();
	}
}
