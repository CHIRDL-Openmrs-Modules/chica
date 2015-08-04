package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

public class temperatureDisplay implements Rule {
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null) {
			return Result.emptyResult();
		}
		
		String conceptName = "TEMPERATURE CHICA";
		Result ruleResults = null;
		LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
		LogicCriteria fullCriteria = conceptCriteria;
		
		ruleResults = context.read(patientId, context.getLogicDataSource("obs"), fullCriteria);
		if (ruleResults == null || ruleResults.size() == 0) {
			return Result.emptyResult();
		}
		
		Result tempResult = ruleResults.get(0);
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null || tempResult == null) {
			return Result.emptyResult();
		}
		
		double temp = tempResult.toNumber();
		Integer locationId = (Integer) parameters.get("locationId");
		Location location = Context.getLocationService().getLocation(locationId);
		
		if(location != null && location.getName().equalsIgnoreCase(ChirdlUtilConstants.LOCATION_RIIUMG)) {
			// Convert to metric
			double metricTemp = org.openmrs.module.chirdlutil.util.Util.convertUnitsToMetric(
				temp, org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_fAHRENHEIT);
			metricTemp = org.openmrs.module.chirdlutil.util.Util.round(metricTemp, 1);
			Result newResult = new Result(metricTemp + " C");
			newResult.setResultDate(tempResult.getResultDate());
			return newResult;
		} else {
			Result newResult = new Result(temp + " F");
			newResult.setResultDate(tempResult.getResultDate());
			return newResult;
		}
	}
}
