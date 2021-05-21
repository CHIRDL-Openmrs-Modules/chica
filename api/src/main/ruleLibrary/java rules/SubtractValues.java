package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * Rule to subtract two numeric values and return the result.
 * 
 * @author Steve McKee
 */
public class SubtractValues implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null || parameters.isEmpty()) {
			return Result.emptyResult();
		}
		
		Object firstResultObj = parameters.get("param1");
		Object secondResultObj = parameters.get("param2");
		
		if (!(firstResultObj instanceof String) || !(secondResultObj instanceof String)) {
			return Result.emptyResult();
		}
		
		String firstResultStr = (String)firstResultObj;
		String secondResultStr = (String)secondResultObj;
		
		if (StringUtils.isBlank(firstResultStr) || StringUtils.isBlank(secondResultStr)) {
			return Result.emptyResult();
		}
		
		Double firstValue = null;
		Double secondValue = null;
		try {
			firstValue = Double.valueOf(firstResultStr);
		} catch (NumberFormatException e) {
			this.log.error("Error parsing the following value into a Double: " + firstResultStr + " for patient " 
					+ patientId , e);
			return Result.emptyResult();
		}
		
		try {
			secondValue = Double.valueOf(secondResultStr);
		} catch (NumberFormatException e) {
			this.log.error("Error parsing the following value into a Double: " + secondResultStr + " for patient " 
					+ patientId , e);
			return Result.emptyResult();
		}
		
		return new Result(Double.valueOf(firstValue.doubleValue() - secondValue.doubleValue()));
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[0];
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.NUMERIC;
	}
	
}
