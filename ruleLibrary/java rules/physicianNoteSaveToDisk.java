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

import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.dss.PhysicianNoteSaver;


/**
 *
 * @author Steve McKee
 */
public class physicianNoteSaveToDisk implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String historyAndPhysicalNote = (String)parameters.get("param1");
		String pe = (String)parameters.get("param2");
		String psfNote = (String)parameters.get("param3");
		String physicianNote = (String)parameters.get("param4");
		String assessmentAndPlanNote = (String)parameters.get("param5");
		StringBuffer buffer = new StringBuffer();
		if (historyAndPhysicalNote != null) {
			buffer.append(historyAndPhysicalNote);
		}
		
		if (pe != null) {
			buffer.append(pe);
		}
		
		if (psfNote != null) {
			buffer.append(psfNote);
		}
		
		if (physicianNote != null) {
			buffer.append(physicianNote);
		}
		
		if(assessmentAndPlanNote != null){
			buffer.append(assessmentAndPlanNote);
		}
		
		String text = buffer.toString();
		if (text.trim().length() > 0) {
			PhysicianNoteSaver noteSaver = new PhysicianNoteSaver(patientId, text);
			noteSaver.saveNote();
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
}
