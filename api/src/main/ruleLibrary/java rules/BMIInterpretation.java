package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
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
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.EncounterDateComparator;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Determines the patient's BMI interpretation (underweight, normal, overweight, obesity, severe obesity).  The caller 
 * can provide a list of heights and weights as parameters or specify the BMI and BMI percentile as parameters to 
 * prevent the calculations from occurring again..
 * 
 * @author Steve McKee
 *
 */
public class BMIInterpretation implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null || parameters.isEmpty()) {
			return Result.emptyResult();
		}
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			return Result.emptyResult();
		}
		
		Result bmiResult = getLatestBMI(parameters);
		if (bmiResult == null || bmiResult.isNull()) {
			return Result.emptyResult();
		}
		
		Double bmi = bmiResult.toNumber();
		Date birthdate = patient.getBirthdate();
		String gender = patient.getGender();
		if (birthdate == null || StringUtils.isBlank(gender)) {
			return Result.emptyResult();
		}
		
		Double percentile = getBMIPercentile(bmi, patient.getGender(), birthdate, parameters);
		if (percentile != null) {
			percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2);
			return getBMIInterpretation(percentile, bmi, gender, birthdate);
		}
		
		return Result.emptyResult();
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
	 * Calculates the latest BMI for the patient.
	 * 
	 * @param parameters Map of parameters containing the patients height and weight.
	 * @return Result object containing the patient's latest BMI.
	 */
	@SuppressWarnings("unchecked")
	private Result getLatestBMI(Map<String, Object> parameters) {
		Object heightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_1);
		Object weightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_2);
		Object bmiResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_3);
		
		// Check to see if the BMI was already provided as a parameter
		if (bmiResultsObject instanceof Result) {
			Result bmiResults = (Result)bmiResultsObject;
			Double bmi = bmiResults.toNumber();
			if (bmi != null) {
				return bmiResults;
			}
		}
		
		if (!(heightResultsObject instanceof List<?>) || !(weightResultsObject instanceof List<?>)) {
			return Result.emptyResult();
		}
		
		List<Result> heightResults = (List<Result>)heightResultsObject;
		List<Result> weightResults = (List<Result>)weightResultsObject;
		
		// Group heights and weights by encounter
		Map<Encounter, Obs> encounterToHeightMap = createEncounterMap(heightResults);
		Map<Encounter, Obs> encounterToWeightMap = createEncounterMap(weightResults);
		
		// Get all the unique encounters and sort the list by encounter date, newest to oldest.
		Set<Encounter> encounterSet = new HashSet<>();
		encounterSet.addAll(encounterToHeightMap.keySet());
		encounterSet.addAll(encounterToWeightMap.keySet());
		List<Encounter> encounterList = new ArrayList<>(encounterSet);
		Collections.sort(encounterList, new EncounterDateComparator());
		Collections.reverse(encounterList);
		
		// Loop through until we find a match for both height and weight from the same encounter
		Result bmiResult = Result.emptyResult();
		for (Encounter encounter : encounterList) {
			Obs heightObs = encounterToHeightMap.get(encounter);
			Obs weightObs = encounterToWeightMap.get(encounter);
			if (heightObs != null && weightObs != null) {
				Double height = heightObs.getValueNumeric();
				Double weight = weightObs.getValueNumeric();
				bmiResult = calculateBMI(height, weight);
				if (!bmiResult.isNull()) {
					break;
				}
			}
		}
		
		encounterToHeightMap.clear();
		encounterToHeightMap.clear();
		
		return bmiResult;
	}
	
	/**
	 * Returns the BMI percentile for the patient.
	 * 
	 * @param bmi The patient's BMI
	 * @param gender The patient's gender
	 * @param birthdate The patient's birthdate
	 * @param parameters Map of parameters containing the patient informationt.
	 * @return The BMI percentile for the patient
	 */
	private Double getBMIPercentile(Double bmi, String gender, Date birthdate, Map<String, Object> parameters) {
		Calculator calculator = new Calculator();
		Object bmiPercentileResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_4);
		
		// Check to see if the BMI was already provided as a parameter
		if (bmiPercentileResultsObject instanceof Result) {
			Result bmiPercentileResults = (Result)bmiPercentileResultsObject;
			Double bmiPercentile = bmiPercentileResults.toNumber();
			if (bmiPercentile != null) {
				return bmiPercentile;
			}
		}
		
		return calculator.calculatePercentile(bmi, gender, birthdate, Calculator.PERCENTILE_BMI, null);
	}
	
	/**
	 * Creates a map of Encounter to Observation for the provided result list.
	 * 
	 * @param results The results list to process
	 * @return Map of encounter to observation
	 */
	private Map<Encounter, Obs> createEncounterMap(List<Result> results) {
		Map<Encounter, Obs> encounterToObsMap = new HashMap<>();
		for (Result result : results) {
			Object resultObject = result.getResultObject();
			if (resultObject instanceof Obs) {
				Obs obs = (Obs)resultObject;
				Encounter encounter = obs.getEncounter();
				if (encounter != null && encounter.getUuid() != null) {
					encounterToObsMap.put(encounter, obs);
				}
			}
		}
		
		return encounterToObsMap;
	}
	
	/**
	 * Calculates BMI based on the provided height and weight.
	 * 
	 * @param height The patient's height
	 * @param weight The patient's weight
	 * @return The patient's BMI
	 */
	private Result calculateBMI(Double height, Double weight) {
		//check for division by zero
		if(height == null || height.equals(Double.valueOf(0))) {
			return Result.emptyResult();
		}
		
		if(weight != null) {
			// The height is stored in centimeters, so we need to convert to meters first
			double heightMeters = height.doubleValue() / 100;
			double bmi = (weight.doubleValue() / (heightMeters * heightMeters));
			return new Result(Double.valueOf(bmi));
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * Returns the BMI interpretation of underweight, normal, overweight, obesity, or severe obesity.
	 * 
	 * @param bmiPercentile The patient's BMI percentile
	 * @param bmi The patient's BMI
	 * @param gender The patient's gender
	 * @param birthdate The patient's birth date
	 * @return Result object containing the BMI interpretation
	 */
	private Result getBMIInterpretation(Double bmiPercentile, Double bmi, String gender, Date birthdate) {
		if (bmiPercentile == null) {
			return Result.emptyResult();
		}
		
		double bmiPercentileDouble = bmiPercentile.doubleValue();
		if (bmiPercentileDouble < 5) {
			return new Result("underweight");
		} else if ((bmiPercentileDouble >= 5) && (bmiPercentileDouble < 85)) {
			return new Result("normal");
		} else if ((bmiPercentileDouble >= 85) && (bmiPercentileDouble < 95)) {
			return new Result("overweight");
		} else if (bmiPercentileDouble >= 95) {
			return determineObesityLevel(bmi, gender, birthdate);
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * Determines the level of obesity for the patient.  If the patient is < 120% of the 95th percentile, "obesity" 
	 * will be returned.  If over 120%, severe obesity will be returned.
	 * 
	 * @param bmi The patient's BMI
	 * @param gender The patient's gender
	 * @param birthdate The patient's birth date
	 * @return Result object returning the level of obesity, either "obesity" or "severe obesity"
	 */
	private Result determineObesityLevel(Double bmi, String gender, Date birthdate) {
		if (bmi == null) {
			return Result.emptyResult();
		}
		
		Calculator calculator = new Calculator();
		Integer genderInt = calculator.translateGender(gender);
		if (genderInt == null) {
			return Result.emptyResult();
		}
		
		double ageInMonths = Util.getFractionalAgeInUnits(birthdate, new Date(), Util.MONTH_ABBR);
		Percentile percentileTable =  getNinetyFifthPercentile(Double.valueOf(ageInMonths), genderInt);
		
		if (percentileTable == null) {
			return Result.emptyResult();
		}
		
		double m = percentileTable.getM().doubleValue();
		double l = percentileTable.getL().doubleValue();
		double s = percentileTable.getS().doubleValue();
		double z = 1.645;
		
		double expectedBMI = m * Math.pow(1 + (l*s*z), 1/l);
		double bmiPercentage = (bmi.doubleValue()/expectedBMI) * 100;
		
		if (bmiPercentage >= 120) {
			return new Result("severe obesity");
		}
		
		return new Result("obesity");
	}
	
	/**
	 * Returns the percentile values for a specific gender and age.
	 * 
	 * @param ageInMonths The age of the patient in months
	 * @param gender The gender (1 = male, 2 = female) of the patient
	 * @return A Percentile object for the BMI age and gender.
	 */
	private Percentile getNinetyFifthPercentile(Double ageInMonths, Integer gender) {
		if (ageInMonths == null || gender == null) {
			return null;
		}
		
		Calculator calculator = new Calculator();
		Percentile bmiPercentile = 
				calculator.lookupPercentile(Calculator.PERCENTILE_BMI, ageInMonths, gender);
		if (bmiPercentile != null) {
			return bmiPercentile;
		}
		
		double roundedAge = Math.round(ageInMonths.doubleValue());
		double leftAge = roundedAge;
		if(leftAge > 0) {
			leftAge = roundedAge - 0.5;
		}
		
		double rightAge = roundedAge + 0.5;
		Percentile percentileTableLeft = 
				calculator.lookupPercentile(Calculator.PERCENTILE_BMI, Double.valueOf(leftAge), gender);
		Percentile percentileTableRight = 
				calculator.lookupPercentile(Calculator.PERCENTILE_BMI, Double.valueOf(rightAge), gender);
		if (percentileTableLeft != null && percentileTableRight != null) {
			Double leftS = percentileTableLeft.getS();
			Double leftL = percentileTableLeft.getL();
			Double leftM = percentileTableLeft.getM();
			
			Double rightS = percentileTableRight.getS();
			Double rightL = percentileTableRight.getL();
			Double rightM = percentileTableRight.getM();
			
			Double interpL = calculator.interpolate(
				Double.valueOf(leftAge), Double.valueOf(rightAge), leftL, rightL, ageInMonths);
			Double interpM = calculator.interpolate(
				Double.valueOf(leftAge), Double.valueOf(rightAge), leftM, rightM, ageInMonths);
			Double interpS = calculator.interpolate(
				Double.valueOf(leftAge), Double.valueOf(rightAge), leftS, rightS, ageInMonths);
			
			Bmiage bmiInterpPercentile = new Bmiage();
			bmiInterpPercentile.setL(interpL);
			bmiInterpPercentile.setM(interpM);
			bmiInterpPercentile.setS(interpS);
			return bmiInterpPercentile;
		}
		
		//if we get here, we can't interpolate because one side is null
		//so just use the non-null side
		if (percentileTableLeft != null) {
			return percentileTableLeft;
		}
		
		return percentileTableRight;
	}
}
