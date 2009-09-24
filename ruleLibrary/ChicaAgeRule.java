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
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.dss.util.Util;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class ChicaAgeRule implements Rule {

   /**
    * 
    * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient, java.util.Map)
    */
    public Result eval(LogicContext context, Patient patient,
            Map<String, Object> parameters) throws LogicException {

        Date birthdate = context.read(patient,
                context.getLogicDataSource("person"), "BIRTHDATE").toDatetime();

        if(birthdate == null){
        	return Result.emptyResult();
        }
        int age = 0;
        Calendar bdate = Calendar.getInstance();
        bdate.setTime(birthdate);

        Calendar now = Calendar.getInstance();
        now.setTime(context.getIndexDate());

        // calculate age as the difference in what the parameter says.
        if (parameters != null)
		{
        	String param = (String) parameters.get("param1");
		
        	
        	if(param.compareToIgnoreCase("years") == 0) {
        
        		 age = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, now.getTime(), Util.YEAR_ABBR);
        	}
        	else if(param.compareToIgnoreCase("months") == 0) {
        
        		 age = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, now.getTime(), Util.MONTH_ABBR);
        	}
        	else if(param.compareToIgnoreCase("days") == 0) {
                
        		 age = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, now.getTime(), Util.DAY_ABBR);
        	}
        	else if(param.compareToIgnoreCase("weeks") == 0) {
        	
        		 age = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, now.getTime(), Util.WEEK_ABBR);
        	}        	
		}
        else
        {
        	return Result.emptyResult();
        }
        		
        return new Result(age);

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
        return new String[] { "%%patient.birthdate" };
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
