package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.atd.hibernateBeans.FormInstance;

import org.openmrs.module.chica.impl.ChicaServiceImpl;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.atd.service.ATDService;

public class consumeHeight implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
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
		ATDService atdService = (ATDService) Context.getService(ATDService.class);
		Integer ruleId = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			conceptName = (String) parameters.get("concept");
			ruleId = (Integer) parameters.get("ruleId");

			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get("encounterId");
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
		
		String primaryResult = ruleResult.toString();
		
		fieldName = (String) parameters.get("child0");
		formIdCriteria = new LogicCriteria("formInstance").equalTo(formInstance);

		fieldNameCriteria = new LogicCriteria(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		ruleResult = context.read(patient, this.logicService
				.getLogicDataSource("xml"), formIdCriteria);
		
		String secondaryResult = ruleResult.toString();
			
		ConceptService conceptService = Context.getConceptService();
		
		if((primaryResult == null || primaryResult.length() == 0) && 
				(secondaryResult == null || secondaryResult.length() == 0)){
			return Result.emptyResult();
		}
		
		if(primaryResult == null || primaryResult.length() == 0)
		{
			primaryResult = "0";
		}
		
		if(secondaryResult == null || secondaryResult.length() == 0)
		{
			secondaryResult = "0";
		}
		
		String heightUnit = atdService.evaluateRule( "birthdate>heightUnits", 
				  patient,parameters,null).toString();

		String fullResult = primaryResult+"."+secondaryResult;
		
		if(heightUnit != null && heightUnit.equals("cm."))
		{
			double measurement = Double.parseDouble(fullResult);
			double inches = 
				org.openmrs.module.dss.util.Util.convertUnitsToEnglish(measurement, 
						org.openmrs.module.dss.util.Util.MEASUREMENT_CM);
			fullResult = String.valueOf(inches);
		}else
		{
			fullResult = primaryResult+"."+secondaryResult;
		}
		
		if(fullResult != null&&fullResult.length()>0)
		{
			Util.saveObs(patient, conceptService.getConceptByName(conceptName),
					encounterId, fullResult,formInstance.getFormInstanceId(),
					ruleId,formInstance.getFormId());
		}
		
		return Result.emptyResult();
	}
}