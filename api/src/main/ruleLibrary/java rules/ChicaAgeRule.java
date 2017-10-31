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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

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
    public Result eval(LogicContext context, Integer patientId,
            Map<String, Object> parameters) throws LogicException {
		
        Date birthdate = context.read(patientId,
                context.getLogicDataSource("person"), "BIRTHDATE").toDatetime();
		
		if (birthdate == null) {
			return Result.emptyResult();
		}
		int age = 0;
		Calendar bdate = Calendar.getInstance();
		bdate.setTime(birthdate);
		
		Calendar now = Calendar.getInstance();
		now.setTime(context.getIndexDate());
		
		Date ageEndDate = now.getTime();
		
		// calculate age as the difference in what the parameter says.
		if (parameters != null) 
		{
			//if the ChicaAgeRule was called by the PWS, then use the printed timestamp
			//not the current time
			FormInstance formInstance = (FormInstance) parameters.get("formInstance");
			Integer formId = null;
			if (formInstance != null) {
				formId = formInstance.getFormId();
			}
			
			String formType = null;
			Integer locationTagId = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
			if (formId != null && locationTagId != null) {
				formType = org.openmrs.module.chica.util.Util.getFormType(formId, locationTagId, formInstance.getLocationId());
			}
			
			if (StringUtils.isNotBlank(formType) && ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType)){
				
				PatientState patientState = 
					org.openmrs.module.atd.util.Util.getProducePatientStateByFormInstanceAction(formInstance);
				
				if (patientState != null) {
					Date formPrintedTime = patientState.getStartTime();
					
					if (formPrintedTime != null) {
						ageEndDate = formPrintedTime;
					}
				}
			}
			
			String units = null;
			
			String param = (String) parameters.get("param1");
			
			if (param.compareToIgnoreCase("years") == 0) {
				
				units = Util.YEAR_ABBR;
			} else if (param.compareToIgnoreCase("months") == 0) {
				
				units = Util.MONTH_ABBR;
			} else if (param.compareToIgnoreCase("days") == 0) {
				
				units = Util.DAY_ABBR;
			} else if (param.compareToIgnoreCase("weeks") == 0) {
				
				units = Util.WEEK_ABBR;
			}
			
			if (units != null) {
				age = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, ageEndDate, units);
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
