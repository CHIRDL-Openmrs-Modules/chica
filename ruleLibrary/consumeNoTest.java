package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
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
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.atd.hibernateBeans.FormInstance;

public class consumeNoTest implements Rule
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
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		Integer ruleId = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			conceptName = (String) parameters.get("concept");
			
			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get("encounterId");
			ruleId = (Integer) parameters.get("ruleId");
		}

		if (formInstance == null)
		{
			throw new LogicException(
					"The xml datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteria("formInstance").equalTo(formInstance);
	
		LogicCriteria fieldNameCriteria = new LogicCriteria(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patient, this.logicService
				.getLogicDataSource("xml"), formIdCriteria);
		
		ConceptService conceptService = Context.getConceptService();
		
		if(ruleResult != null&&ruleResult.toString()!=null&&
				ruleResult.toString().length()>0)
		{
			
			String enteredValue = ruleResult.toString();
			String answer = null;
			
			if(enteredValue.equalsIgnoreCase("Y")){
				answer = "yes";
			}
			
			
			if(answer != null){
				Util.saveObs(patient, conceptService.getConceptByName(conceptName),
						encounterId, answer,formInstance.getFormInstanceId(),ruleId,
						formInstance.getFormId());
			}
		}
		
		return Result.emptyResult();
	}
}