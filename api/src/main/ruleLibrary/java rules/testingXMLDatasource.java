package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
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
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.logic.op.OperandObject;

public class testingXMLDatasource implements Rule
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
		FormInstance formInstance = null;
		String fieldName = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
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
				.getLogicDataSource("form"), formIdCriteria);
		return ruleResult.get(0);
	}
	
}