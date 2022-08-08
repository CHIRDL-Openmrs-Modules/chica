package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * The consumeCheckBox java rule is a wrapper for consumeNoTest which converts and saves the value of the html checkbox to an observation.
 * @author Meena Sheley
 */
public class consumeCheckBox implements Rule
{

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	@Override
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
	@Override
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	@Override
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}
	@Override
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		context.eval(patientId, "consumeNoTest",parameters);	
		return Result.emptyResult();
	}
}
