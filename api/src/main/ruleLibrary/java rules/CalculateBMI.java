package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.EncounterDateComparator;

/**
 * Calculates a patient's BMI.  The caller must provide a list of heights and weights as parameters.
 * 
 * @author Steve McKee
 *
 */
public class CalculateBMI implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null || parameters.isEmpty()) {
			return Result.emptyResult();
		}
		
		Object heightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_1);
		Object weightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_2);
		
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
		Result result = Result.emptyResult();
		for (Encounter encounter : encounterList) {
			Obs height = encounterToHeightMap.get(encounter);
			Obs weight = encounterToWeightMap.get(encounter);
			if (height != null && weight != null) {
				result = calculateBMI(height, weight);
				if (!result.isNull()) {
					break;
				}
			}
		}
		
		encounterToHeightMap.clear();
		encounterToHeightMap.clear();
		
		return result;
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
	private Result calculateBMI(Obs height, Obs weight) {
		Double heightNum = height.getValueNumeric();
		Double weightNum = weight.getValueNumeric();
		
		//check for division by zero
		if(heightNum == null || heightNum.equals(Double.valueOf(0))) {
			return Result.emptyResult();
		}
		
		if(weightNum != null) {
			// The height is stored in centimeters, so we need to convert to meters first
			double heightMeters = heightNum.doubleValue() / 100;
			double bmi = (weightNum.doubleValue() / (heightMeters * heightMeters));
			return new Result(Double.valueOf(bmi));
		}
		
		return Result.emptyResult();
	}
}
