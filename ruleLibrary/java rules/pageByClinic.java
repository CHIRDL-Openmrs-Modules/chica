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
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class pageByClinic implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
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
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer locationId = (Integer) parameters.get("locationId");
		String locationAttr = (String) parameters.get("param1");
		String message = (String) parameters.get("param2");
		
		if (locationId == null) {
			log.error("Location ID parameter not found.  No one will be paged for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (locationAttr == null) {
			log.error("Location Attribute Value parameter not found.  No one will be paged for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (message == null) {
			log.error("Message parameter not found.  No one will be paged for " + locationAttr + ".");
			return Result.emptyResult();
		}
		
		Location location = Context.getLocationService().getLocation(locationId);
		if (location == null) {
			log.error("No location found for location ID " + locationId + ".  No one will be paged for " + locationAttr + 
				".");
			return Result.emptyResult();
		}
		
		// Get the patient's preferred language
		LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
		Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
		String language = "Unknown";
		if (languageResult != null && languageResult.toString().length() > 0) {
			language = languageResult.toString();
		}
		
		String pagerMessage = "Loc: " + location.getName() + " PID: " + patientId + " Lang: " + language +" - " + message;
		
		// Get the pager numbers
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		LocationAttributeValue lav = service.getLocationAttributeValue(locationId, locationAttr);
		if (lav == null || lav.getValue() == null || lav.getValue().trim().length() == 0) {
			log.error("No valid " + locationAttr + " found for location " + locationId + ".  No one will be paged.");
			return Result.emptyResult();
		}
		
		String pagerNumbers = lav.getValue();
		StringTokenizer tokenizer = new StringTokenizer(pagerNumbers, ",");
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		while (tokenizer.hasMoreTokens()) {
			String pagerNumber = tokenizer.nextToken();
			pagerNumber = pagerNumber.replaceAll(" ", "");
			String response = Util.sendPage(pagerMessage, pagerNumber);
			if (response.contains("send failed")) {
				try {
	                Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                response = Util.sendPage(pagerMessage, pagerNumber);
                if (response.contains("send failed")) {
                	log.error("Error sending page message " + pagerMessage + " to pager " + pagerNumber);
                	Error error = new Error("Error", locationAttr, "Support page failed to be sent to number " + pagerNumber + 
                		": " + pagerMessage, null,
                		new java.util.Date(), null);
    				chirdlutilbackportsService.saveError(error);
                } else {
                	Error error = new Error("Info", locationAttr, "Support page sent: " + pagerMessage, null,
                		new java.util.Date(), null);
    				chirdlutilbackportsService.saveError(error);
                }
			} else {
				Error error = new Error("Info", locationAttr, "Support page sent: " + pagerMessage, null,
            		new java.util.Date(), null);
				chirdlutilbackportsService.saveError(error);
			}
		}
		
		return Result.emptyResult();
	}
}
