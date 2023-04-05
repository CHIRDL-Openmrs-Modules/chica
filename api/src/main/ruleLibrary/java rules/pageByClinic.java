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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Person;
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
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.service.DssService;

public class pageByClinic implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(pageByClinic.class);
	
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
		Integer encounterId = (Integer) parameters.get("encounterId");
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
		// Send Spanish if ever spoken Spanish.  Otherwise send English if has spoken English.
		LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
		Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
		String language = "Unknown";
		if (languageResult != null && !languageResult.isEmpty()) {
			for (Result result : languageResult) {
				language = result.toString();
				if ("Spanish".equalsIgnoreCase(language)) {
					break;
				}
			}
		}
		
		// Get the PCP
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		
		// CHICA-1151 Use the provider that has the "Attending Provider" role for the encounter
		String pcp = "";
		Person physician = null;
		org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
		if(provider != null)
		{
			physician = provider.getPerson();
		}
		 
		if (physician != null) {
			pcp = physician.getGivenName() + " " + physician.getFamilyName();
		}
		
		String pagerMessage = "Loc: " + location.getName() + " PID: " + patientId + " Lang: " + language + " PCP: " + pcp + 
			" - " + message;
		
		// Get the pager numbers
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		ChirdlLocationAttributeValue lav = service.getLocationAttributeValue(locationId, locationAttr);
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
