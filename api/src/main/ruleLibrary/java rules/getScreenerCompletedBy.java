package org.openmrs.module.chica.rule;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class getScreenerCompletedBy implements Rule {
    
    /**
     * *
     * 
     * @see org.openmrs.logic.Rule#getParameterList()
     */
    public Set<RuleParameterInfo> getParameterList() {
        return Collections.emptySet();
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
    public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) {
        if (parameters == null) {
            return Result.emptyResult();
        }
        
        String conceptName = "screener_completed_by";
        LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
        Result ruleResults = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
        if (ruleResults.isEmpty()) {
            return Result.emptyResult();
        }
        return new Result(ruleResults);
    }
}
