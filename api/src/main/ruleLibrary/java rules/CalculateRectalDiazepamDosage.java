package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Calculates Rectal Diazepam dosage based upon patient's age and weight.  The value must be rounded UP to the nearest 
 * 2.5 (i.e. 2.5, 5.0, 7.5, 10.0).
 * <pre>{@code
 * If (age >=2) and (age <6) then
 *		Dose := Round_to_2.5(weight * 0.5) "mg diazepam per rectum."
 * ElseIf (age >=6) and (age <12) then
 *		Dose := Round_to_2.5(weight * 0.3) "mg diazepam per rectum."
 * ElseIf (age >=12) then
 *		Dose := Round_to_2.5(weight * 0.2) "mg diazepam per rectum."
 * EndIf
 * 
 * }</pre>
 * 
 * @author Steve McKee
 *
 */
public class CalculateRectalDiazepamDosage implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final String MESSAGE_DOSAGE = "mg diazepam per rectum";
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null || parameters.isEmpty()) {
			return Result.emptyResult();
		}
		
		// Ensure the parameter is a list
		Object weightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_1);
		if (!(weightResultsObject instanceof List<?>)) {
			return Result.emptyResult();
		}
		
		// Ensure there is at least one weight present
		List<Result> weightResults = (List<Result>)weightResultsObject;
		if (weightResults.isEmpty()) {
			return Result.emptyResult();
		}
		
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
		
		// Get the patient's age in years
		int age = Util.getAgeInUnits(birthDate, new Date(), Util.YEAR_ABBR);
		
		// Get the latest weight observation
		Double weight = Util.getLatestNumericValue(weightResults);
		if (weight == null) {
			return Result.emptyResult();
		}
		
		return calculateDosage(age, weight.doubleValue());
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
	
	/**
	 * Calculates the dosage for Midazolam based on age and weight.
	 * 
	 * @param age The patient's age
	 * @param weight The patient's weight
	 * @return The dosage message
	 */
	private Result calculateDosage(int age, double weight) {
		if ((age >= 2) && (age < 6)) {
			double dose = weight * 0.5;
			double doseRounded = roundDose(dose);
			return new Result(doseRounded + MESSAGE_DOSAGE);
		}
		
		if ((age >= 6) && (age < 12)) {
			double dose = weight * 0.3;
			double doseRounded = roundDose(dose);
			return new Result(doseRounded + MESSAGE_DOSAGE);
		}
		
		if (age >= 12) {
			double dose = weight * 0.2;
			double doseRounded = roundDose(dose);
			return new Result(doseRounded + MESSAGE_DOSAGE);
		}
			
		return Result.emptyResult();
	}
	
	/**
	 * Rounds the provided dose value up to the nearest 2.5.
	 * 
	 * @param dose The dose to round
	 * @return Dose rounded up to the nearest 2.5
	 */
	private double roundDose(double dose) {
		double result = 2.5 * (Math.ceil(Math.abs(dose / 2.5)));
		return Util.round(Double.valueOf(result), 1).doubleValue();
	}
}
