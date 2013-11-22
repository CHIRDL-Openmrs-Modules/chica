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

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.gis.GISConstants;
import org.openmrs.module.chirdlutil.util.HttpUtil;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class informPresnet implements Rule {
	
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
		Patient patient = Context.getPatientService().getPatient(patientId);
		Integer locationId = (Integer) parameters.get("locationId");
		Integer encounterId = (Integer) parameters.get("encounterId");
		String study = (String) parameters.get("param1");
		if (study == null || study.trim().length() == 0) {
			log.error("Study variable cannot be null or empty.");
			return Result.emptyResult();
		}
		
		String studyUrl = getStudyPostURL();
		if (studyUrl == null || study.trim().length() == 0) {
			log.error("No post URL found for study: " + study);
			return Result.emptyResult();
		}
		
		// Get the connection timeout
		AdministrationService adminService = Context.getAdministrationService();
		Integer connectionTimeout = 5;
		String connectionTimeoutStr = adminService.getGlobalProperty("chica.presnetConnectionTimeout");
		if (connectionTimeoutStr == null || connectionTimeoutStr.trim().length() == 0) {
			log.error("No value set for global property: chica.presnetConnectionTimeout.  A default of 5 seconds will "
			        + "be used.");
			connectionTimeoutStr = "5";
		}
		
		try {
			connectionTimeout = Integer.parseInt(connectionTimeoutStr);
			connectionTimeout = connectionTimeout * 1000;
		}
		catch (NumberFormatException e) {
			log.error("Invalid number format for global property chica.presnetConnectionTimeout.  A default of 5 "
			        + "seconds will be used.");
			connectionTimeout = 5000;
		}
		
		// Get the read timeout
		Integer readTimeout = 5;
		String readTimeoutStr = adminService.getGlobalProperty("chica.presnetReadTimeout");
		if (readTimeoutStr == null || readTimeoutStr.trim().length() == 0) {
			log.error("No value set for global property: chica.presnetReadTimeout.  A default of 5 seconds will "
			        + "be used.");
			readTimeoutStr = "5";
		}
		
		try {
			readTimeout = Integer.parseInt(readTimeoutStr);
			readTimeout = readTimeout * 1000;
		}
		catch (NumberFormatException e) {
			log.error("Invalid number format for global property chica.presnetReadTimeout.  A default of 5 "
			        + "seconds will be used.");
			readTimeout = 5000;
		}
		
		String mrn = patient.getPatientIdentifier().getIdentifier();
		String firstName = patient.getGivenName();
		String lastName = patient.getFamilyName();
		String gender = patient.getGender();
		Date birthdate = patient.getBirthdate();
		String birthdateStr = null;
		if (birthdate != null) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			birthdateStr = dateFormatter.format(birthdate);
		}
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		Person physician = encounter.getProvider();
		String physicianName = physician.getGivenName() + " " + physician.getFamilyName();
		
		String studyParams = getStudySpecificParams(study);
		
		// Get the location display name
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		
		// Find out if patient is Spanish speaking and/or on Medicaid
		LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
		Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
		String language = null;
		if (languageResult != null && languageResult.toString().length() > 0) {
			language = languageResult.toString();
		}
		
		String data = "";
		try {
			data += URLEncoder.encode("clinic", "UTF-8") + "=" + URLEncoder.encode(locationId.toString(), "UTF-8");
			data += "&" + URLEncoder.encode("study", "UTF-8") + "=" + URLEncoder.encode(study, "UTF-8");
			data += "&" + URLEncoder.encode("patientid", "UTF-8") + "=" + URLEncoder.encode(patientId.toString(), "UTF-8");
			data += "&" + URLEncoder.encode("encounterid", "UTF-8") + "="
			        + URLEncoder.encode(encounterId.toString(), "UTF-8");
			data += "&" + URLEncoder.encode("patientmrn", "UTF-8") + "=" + URLEncoder.encode(mrn, "UTF-8");
			data += "&" + URLEncoder.encode("patientfirstname", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
			data += "&" + URLEncoder.encode("patientlastname", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
			data += "&" + URLEncoder.encode("physicianname", "UTF-8") + "=" + URLEncoder.encode(physicianName, "UTF-8");
			data += "&" + URLEncoder.encode("patientgender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
			if (birthdateStr != null) {
				data += "&" + URLEncoder.encode("patientbirthdate", "UTF-8") + "=" + URLEncoder.encode(
					birthdateStr, "UTF-8");
			}
			
			if (language != null) {
				data += "&" + URLEncoder.encode("patientlanguage", "UTF-8") + "=" + URLEncoder.encode(language, "UTF-8");
			}
		}
		catch (IOException e) {
			log.error("Error creating POST data for Presnet", e);
			return Result.emptyResult();
		}
		
		String postData = data + studyParams;
		String result = null;
		try {
			result = HttpUtil.post(studyUrl, postData, connectionTimeout, readTimeout);
			if (!"Successful!".equalsIgnoreCase(result)) {
				log.error("Post to the Presnet web site was unsuccessful: " + result);
			}
		}
		catch (IOException e) {
			log.error("Exception occurred posting data to the Presnet web site", e);
		}
		
		return new Result(result);
	}
	
	private String getStudyPostURL() {
		return Context.getAdministrationService().getGlobalProperty("chica.presnetStudyPostUrl");
	}
	
	private String getStudySpecificParams(String study) {
		if ("medicalLegal".equalsIgnoreCase(study)) {
			return getMedicalLegalParams();
		} else if ("diabetes".equalsIgnoreCase(study)) {
			return getDiabetesParams();
		}
		
		return "";
	}
	
	private String getMedicalLegalParams() {
		// homelessness
		// unsaferental
		// inadequateutilities
		// foodinsecurity
		return "";
	}
	
	private String getDiabetesParams() {
		return "";
	}
}
