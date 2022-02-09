package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Calculates weight percentile based upon a provided weight, the patient's age, and gender.
 * 
 * @author Steve McKee
 */
public class CalculateWeightPercentile implements Rule {
	
	private static final String CALCULATION_WEIGHT_PERCENTILE = "weight";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null || parameters.isEmpty()) {
			return Result.emptyResult();
		}
		
		// Ensure the parameter is a Result
		Object weightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_1);
		if (!(weightResultsObject instanceof Result)) {
			return Result.emptyResult();
		}
		
		Result weightResults = (Result)weightResultsObject;
		
		// Ensure the patient exists
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			this.log.error("Cannot find patient with ID " + patientId);
			return Result.emptyResult();
		}
		
		// Ensure the patient has a birthdate
		Date birthDate = patient.getBirthdate();
		if (birthDate == null) {
			this.log.error("Patient " + patientId + " does not have a birthdate specified.");
			return Result.emptyResult();
		}
		
		Double weight = null;
		Date dateTime = null;
		// Get the weight observation
		Object weightObsObj = weightResults.getResultObject();
		if (weightObsObj instanceof Obs) {
			Obs weightObs = (Obs)weightObsObj;
			weight = weightObs.getValueNumeric();
			dateTime = weightObs.getObsDatetime();
		} else if (Datatype.NUMERIC.equals(weightResults.getDatatype())) {
			weight = weightResults.toNumber();
			dateTime = weightResults.getResultDate();
		} else {
			return Result.emptyResult();
		}
		
		if (weight == null) {
			return Result.emptyResult();
		}
		
		// Calculate the weight percentile
		Calculator calc = new Calculator();
		try {
			Double weightPercentile = calc.calculatePercentile(weight, patient.getGender(), birthDate, 
				CALCULATION_WEIGHT_PERCENTILE, org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_KG, 
				dateTime);
			return new Result(weightPercentile);
		} catch (Exception e) {
			this.log.error("Error calculating weight percentile for patient " + patientId, e);
			return Result.emptyResult();
		}
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
