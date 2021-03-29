package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.ObsDateComparator;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Calculates Midazolam dosage based upon patients's age and weight.
 * <pre>{@code
 * If age>= 12 years then
 *		Dose := "One 5mg spray in one nostril, repeat ONCE after 10 min in other nostril prn"
 * ElseIf age <12 years then
 *		Calc := integer(0.2 * weight) (*Round to nearest 0.1*)
 *		Vol := Calc/5  (*Round to nearest 0.1*)
 *		If Calc > 10 then
 *			Dose := "5 mg (1 ml of 5mg/ml) in each nostril."
 *		Else Dose := Calc "mg( " Vol "ml of 5mg/ml) half in each nostril."
 * EndIf
 * 
 * }</pre>
 * 
 * @author Steve McKee
 *
 */
public class CalculateMidazolamDosage implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
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
		Double weight = getLatestWeight(weightResults);
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
	 * Returns the newest weight observation from the list of results.
	 * 
	 * @param resultList The list of results containing weight observations
	 * @return Obs object or null if one is not found
	 */
	private Double getLatestWeight(List<Result> resultList) {
		List<Obs> obsList = new ArrayList<>();
		for (Result result : resultList) {
			Object resultObj = result.getResultObject();
			if (resultObj instanceof Obs) {
				Obs obs = (Obs)resultObj;
				if (obs.getValueNumeric() != null) {
					obsList.add((Obs)resultObj);
				}
			}
		}
		
		if (obsList.isEmpty()) {
			return null;
		}
		
		// Sort the weights so the latest is first
		Collections.sort(obsList, new ObsDateComparator());
		Collections.reverse(obsList);
		
		Obs obs = obsList.get(0);
		return obs.getValueNumeric();
	}
	
	/**
	 * Calculates the dosage for Midazolam based on age and weight.
	 * 
	 * @param age The patient's age
	 * @param weight The patient's weight
	 * @return The dosage message
	 */
	private Result calculateDosage(int age, double weight) {
		if (age >= 12) {
			return new Result("One 5mg spray in one nostril, repeat ONCE after 10 min in other nostril prn.");
		}
			
		Double calcObj = Util.round(Double.valueOf(0.2 * weight), 1);
		if (calcObj == null) {
			return Result.emptyResult();
		}
		
		double calc = calcObj.doubleValue();
		if (calc > 10) {
			return new Result("5 mg (1 ml of 5mg/ml) in each nostril.");
		}
		
		Double vol = Util.round(Double.valueOf(calcObj.doubleValue() / 5.0), 1);
		if (vol == null) {
			return Result.emptyResult();
		}
		
		return new Result(calc + "mg(" + vol.doubleValue() + "ml of 5mg/ml) half in each nostril.");
	}
}
