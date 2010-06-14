/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.rule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class IncompleteJITName implements Rule {

   /**
    * 
    * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient, java.util.Map)
    */
    public Result eval(LogicContext context, Patient patient,
            Map<String, Object> parameters) throws LogicException {

    	ATDService atdService = Context.getService(ATDService.class);
    	
        if (parameters != null)
		{
        	Integer encounterId = (Integer) parameters.get("encounterId");
        	State incompleteState = atdService.getStateByName("JIT_incomplete");
        	if(incompleteState != null){
        		List<PatientState> incompleteStates = 
        			atdService.getPatientStateByEncounterState(encounterId, incompleteState.getStateId());
        		if(incompleteStates != null && incompleteStates.size()>0){
        			Integer formId = incompleteStates.get(0).getFormId();
        			if(formId != null){
        				FormService formService = Context.getFormService();
        				Form form = formService.getForm(formId);
        				if(form != null){
        					return new Result("Form "+form.getName());
        				}
        			}
        		}
        	}
		}
        return Result.emptyResult();
    }

    /**
     * @see org.openmrs.logic.rule.Rule#getParameterList()
     */
    public Set<RuleParameterInfo> getParameterList() {
        return null;
    }

    /**
     * @see org.openmrs.logic.rule.Rule#getDependencies()
     */
    public String[] getDependencies() {
        return null;
    }

    /**
     * @see org.openmrs.logic.rule.Rule#getTTL()
     */
    public int getTTL() {
        return 60 * 60 * 24; // 1 day
    }

    /**
     * @see org.openmrs.logic.rule.Rule#getDatatype(String)
     */
    public Datatype getDefaultDatatype() {
        return Datatype.NUMERIC;
    }

}
