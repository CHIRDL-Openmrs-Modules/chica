package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Determine whether or not the child's BMI is 2 or more standard deviations below the mean.
 * 
 * @author Seema Sarala
 *
 */
public class IsBMITwoBelowSD implements Rule {
    
    private static final String RULE_CALCULATE_BMI = "CalculateBMI";
    private static final String CALCULATION_MD_BMI_TWO_BELOW_SD = "mdbmi2belowsd";
    
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
        
        Result bmiResult = calculateBMI(patient, parameters);
        if (bmiResult == null || bmiResult.isNull()) {
            return Result.emptyResult();
        }
        
        Calculator calculator = new Calculator();
        Boolean flag = calculator.isBMITwoBelowSD(bmiResult.toNumber(), patient.getBirthdate(), 
            CALCULATION_MD_BMI_TWO_BELOW_SD, bmiResult.getResultDate());
        if (flag != null) {
            return new Result(flag);
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
     * Calculates the BMI for the patient.
     * 
     * @param patient The patient to calculate the BMI for
     * @param parameters Map of patient information.
     * @return The patient's BMI
     */
    private static Result calculateBMI(Patient patient, Map<String, Object> parameters) {
        Object bmiResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_3);
        
        // Check to see if the BMI was already provided as a parameter
        if (bmiResultsObject instanceof Result) {
            Result bmiResults = (Result)bmiResultsObject;
            Double bmi = bmiResults.toNumber();
            if (bmi != null) {
                return bmiResults;
            }
        }
        
        return Context.getService(ATDService.class).evaluateRule(RULE_CALCULATE_BMI, patient, parameters);
    }
}
