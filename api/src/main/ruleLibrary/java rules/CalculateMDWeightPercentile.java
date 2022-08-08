package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates weight percentile based upon a provided weight, the patient's age, and gender.
 * 
 * @author Seema Sarala
 */
public class CalculateMDWeightPercentile implements Rule {
    
    private static final String CALCULATION_MD_WEIGHT_PERCENTILE = "mdweight";
    
    private static final Logger log = LoggerFactory.getLogger(CalculateMDWeightPercentile.class);
    
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
            log.error("Cannot find patient with ID {}", patientId);
            return Result.emptyResult();
        }
        
        // Ensure the patient has a birthdate
        Date birthDate = patient.getBirthdate();
        if (birthDate == null) {
            log.error("Patient {} does not have a birthdate specified.", patientId);
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
            Double weightPercentile = calc.calculatePercentile(weight, birthDate, CALCULATION_MD_WEIGHT_PERCENTILE, dateTime);
            return new Result(weightPercentile);
        } catch (Exception e) {
            log.error("Error calculating weight percentile for patient {}", patientId, e);
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
