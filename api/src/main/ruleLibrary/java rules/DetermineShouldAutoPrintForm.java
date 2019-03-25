package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Determines whether or not a form should be auto-printed.
 * 
 * @author Steve McKee
 */
public class DetermineShouldAutoPrintForm implements Rule {
    
    private static final String LOCATION_PEPS = "PEPS";
    private static final String FORM_ASQ = "ASQ";
    private static final String FORM_GROWTH_CHART = "Growth Chart";
    private static final String FORM_SCHOOL_EXCUSE = "School Excuse";
    
    @Override
    public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
        Object formNameObj = parameters.get(ChirdlUtilConstants.PARAMETER_1);
        Object locationNameObj = parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION);
        
        String formName = "";
        String locationName = "";
        
        if (formNameObj != null && formNameObj instanceof String) {
            formName = (String) formNameObj;
        }
        
        if (locationNameObj != null && locationNameObj instanceof String) {
            locationName = (String) locationNameObj;
        }
        
        // Check Pecar
        if (LOCATION_PEPS.equalsIgnoreCase(locationName)) {
            // They do not want ASQs, growth charts, or school excuse forms printed
            if (FORM_ASQ.equalsIgnoreCase(formName) || FORM_GROWTH_CHART.equalsIgnoreCase(formName)
                    || FORM_SCHOOL_EXCUSE.equalsIgnoreCase(formName)) {
                return new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
            }
        }
        
        return new Result(ChirdlUtilConstants.GENERAL_INFO_TRUE);
    }
    
    /**
     * @see org.openmrs.logic.Rule#getDefaultDatatype()
     */
    @Override
    public Datatype getDefaultDatatype() {
        return Datatype.TEXT;
    }
    
    /**
     * @see org.openmrs.logic.Rule#getDependencies()
     */
    @Override
    public String[] getDependencies() {
        return new String[] {};
    }
    
    /**
     * @see org.openmrs.logic.Rule#getParameterList()
     */
    @Override
    public Set<RuleParameterInfo> getParameterList() {
        return new HashSet<>();
    }
    
    /**
     * @see org.openmrs.logic.Rule#getTTL()
     */
    @Override
    public int getTTL() {
        return 0;
    }
}
