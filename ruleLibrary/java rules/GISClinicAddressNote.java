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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.gis.PatientGISData;
import org.openmrs.module.chica.gis.PatientGISDataStorage;


/**
 *
 * @author Steve McKee
 */
public class GISClinicAddressNote implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String mapType = (String) parameters.get("param0");
		if (mapType == null) {
			log.error("Map type string not specified.");
			return Result.emptyResult();
		}
		
		PatientGISData data = PatientGISDataStorage.getPatientGISData(patientId, mapType);
		if (data == null) {
			return Result.emptyResult();
		}
		
		if (data.isClinicAddress()) {
			String language = (String) parameters.get("param1");
			if ("Spanish".equalsIgnoreCase(language)) {
				return new Result("Nota: La direción de la clinica se usó para hacer esta mapa.");
			}
			
			return new Result("NOTE: The clinic address was used to make this map.");
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
		return new String[] {};
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}
}
