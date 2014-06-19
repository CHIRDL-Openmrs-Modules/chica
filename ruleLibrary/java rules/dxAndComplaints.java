package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class dxAndComplaints implements Rule
{

	/**
	 * @see org.openmrs.logic.rule.Rule#eval(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		String conceptName = "DX and COMPLAINTS";
		Integer index = null;
		
		if(parameters != null)
		{
			index = Integer.parseInt((String) parameters.get("param0"));
		}
		
		Result result = null;

		LogicDataSource dataSource = context.getLogicDataSource("RMRS");
		result = context.read(patientId, dataSource,
				new LogicCriteriaImpl(conceptName));
		Set<String> distinctResultSet = new HashSet<String>();
		
		if (result != null)
		{
			// remove any null or duplicate results
			for (Result currResult : result)
			{
				// ignore null values
				Concept concept = currResult.toConcept();
				if (concept != null)
				{
					// store distinct results
					String elementString = ((ConceptName) concept.getNames().toArray()[0]).getName();
					distinctResultSet.add(elementString);
				}
			}
		}

		if (index != null && index < distinctResultSet.size())
		{
			result = new Result((String) distinctResultSet.toArray()[index]);
			return result;
		}
		return Result.emptyResult();
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; 
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}

}
