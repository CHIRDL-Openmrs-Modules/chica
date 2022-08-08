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
 * Calculates height percentile based upon a provided height, the patient's age, and gender.
 * 
 * @author Seema Sarala
 */
public class CalculateMDHeightPercentile implements Rule {
    
    private static final String CALCULATION_MD_HEIGHT_PERCENTILE = "mdlength";
    
    private static final Logger log = LoggerFactory.getLogger(CalculateMDHeightPercentile.class);
    
    /**
     * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
     */
    @Override
    public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
        if (parameters == null || parameters.isEmpty()) {
            return Result.emptyResult();
        }
        
        // Ensure the parameter is a Result
        Object heightResultsObject = parameters.get(ChirdlUtilConstants.PARAMETER_1);
        if (!(heightResultsObject instanceof Result)) {
            return Result.emptyResult();
        }
        
        Result heightResults = (Result)heightResultsObject;
        
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
        
        Double height = null;
        Date dateTime = null;
        // Get the height observation
        Object heightObsObj = heightResults.getResultObject();
        if (heightObsObj instanceof Obs) {
            Obs heightObs = (Obs)heightObsObj;
            height = heightObs.getValueNumeric();
            dateTime = heightObs.getObsDatetime();
        } else if (Datatype.NUMERIC.equals(heightResults.getDatatype())) {
            height = heightResults.toNumber();
            dateTime = heightResults.getResultDate();
        } else {
            return Result.emptyResult();
        }
        
        if (height == null) {
            return Result.emptyResult();
        }
        
        // Calculate the height percentile
        Calculator calc = new Calculator();
        try {
            Double heightPercentile = calc.calculatePercentile(height, birthDate, CALCULATION_MD_HEIGHT_PERCENTILE, dateTime);
            return new Result(heightPercentile);
        } catch (Exception e) {
            log.error("Error calculating height percentile for patient {}", patientId, e);
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
