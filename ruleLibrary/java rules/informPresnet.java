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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.HttpUtil;

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
		String condition = (String) parameters.get("param2");
		if (study == null || study.trim().length() == 0) {
			log.error("Study variable cannot be null or empty.");
			return Result.emptyResult();
		}
		
		String studyUrl = getStudyPostURL(study);
		if (studyUrl == null) {
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
		
		// Get the location
		Location location = Context.getLocationService().getLocation(locationId);
		String locationName = location.getName();
		
		String mrn = patient.getPatientIdentifier().getIdentifier();
		String firstName = patient.getGivenName();
		String lastName = patient.getFamilyName();
		String gender = patient.getGender();
		Date birthdate = patient.getBirthdate();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String birthdateStr = null;
		if (birthdate != null) {
			birthdateStr = dateFormatter.format(birthdate);
		}
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		String encounterDateStr = null;
		Date encounterDate = encounter.getDateCreated();
		if (encounterDate != null) {
			encounterDateStr = dateFormatter.format(encounterDate);
		}
		
		Person physician = encounter.getProvider();
		String pcp = "";
		if (physician != null) {
			pcp = physician.getGivenName() + " " + physician.getFamilyName();
		}
		
		// Send Spanish if ever spoken Spanish.  Otherwise send English if has spoken English.
		LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
		Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
		String language = null;
		if (languageResult != null && !languageResult.isEmpty()) {
			for (Result result : languageResult) {
				language = result.toString();
				if ("Spanish".equalsIgnoreCase(language)) {
					break;
				}
			}
		}
		
		// Get patient phone number
		PersonAttribute phoneAttribute = patient.getAttribute("Telephone Number");
		
		String studyParams = null;
		StringBuffer data = new StringBuffer();
		try {
			data.append(URLEncoder.encode("clinic", "UTF-8") + "=" + URLEncoder.encode(locationName, "UTF-8"));
			data.append("&" + URLEncoder.encode("study", "UTF-8") + "=" + URLEncoder.encode(study, "UTF-8"));
			data.append("&" + URLEncoder.encode("patientid", "UTF-8") + "="
			        + URLEncoder.encode(patientId.toString(), "UTF-8"));
			data.append("&" + URLEncoder.encode("encounterid", "UTF-8") + "="
			        + URLEncoder.encode(encounterId.toString(), "UTF-8"));
			data.append("&" + URLEncoder.encode("patientmrn", "UTF-8") + "=" + URLEncoder.encode(mrn, "UTF-8"));
			data.append("&" + URLEncoder.encode("patientfirstname", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8"));
			data.append("&" + URLEncoder.encode("patientlastname", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8"));
			data.append("&" + URLEncoder.encode("physicianname", "UTF-8") + "=" + URLEncoder.encode(pcp, "UTF-8"));
			data.append("&" + URLEncoder.encode("patientgender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8"));
			if (birthdateStr != null) {
				data.append("&" + URLEncoder.encode("patientbirthdate", "UTF-8") + "="
				        + URLEncoder.encode(birthdateStr, "UTF-8"));
			}
			
			if (encounterDateStr != null) {
				data.append("&" + URLEncoder.encode("appointmenttime", "UTF-8") + "="
				        + URLEncoder.encode(encounterDateStr, "UTF-8"));
			}
			
			if (language != null) {
				data.append("&" + URLEncoder.encode("patientlanguage", "UTF-8") + "=" + URLEncoder.encode(language, "UTF-8"));
			} else {
				data.append("&" + URLEncoder.encode("patientlanguage", "UTF-8") + "="
				        + URLEncoder.encode("unknown", "UTF-8"));
			}
			
			if (phoneAttribute != null && phoneAttribute.getValue() != null && phoneAttribute.getValue().trim().length() > 0) {
				data.append("&" + URLEncoder.encode("patientphone", "UTF-8") + "="
				        + URLEncoder.encode(phoneAttribute.getValue(), "UTF-8"));
			}
			
			if (condition != null) {
				data.append("&" + URLEncoder.encode("condition", "UTF-8") + "=" + URLEncoder.encode(condition, "UTF-8"));
			}
			
			studyParams = getStudySpecificParams(study, context, patientId, parameters);
		}
		catch (Exception e) {
			log.error("Error creating POST data for Presnet", e);
			return Result.emptyResult();
		}
		
		String postData = data.toString() + studyParams;
		String result = null;
		try {
			result = HttpUtil.post(studyUrl, postData, connectionTimeout, readTimeout);
			if (result == null || !result.contains("Successful")) {
				log.error("Post to the Presnet web site was unsuccessful: " + result);
				log.error("Presnet POST URL failure: " + studyUrl + "?" + postData);
			}
		}
		catch (Exception e) {
			log.error("Exception occurred posting data to the Presnet web site", e);
			log.error("Presnet POST URL failure: " + studyUrl + "?" + postData);
		}
		
		log.info("Presnet POST URL success: " + studyUrl + "?" + postData);
		return new Result(result);
	}
	
	private String getStudyPostURL(String studyName) {
		ChicaService chicaService = Context.getService(ChicaService.class);
		List<Study> studies = chicaService.getActiveStudies();
		if (studies == null || studies.size() == 0) {
			return null;
		}
		
		for (Study study : studies) {
			if (studyName.equalsIgnoreCase(study.getTitle())) {
				StudyAttributeValue studyVal = chicaService.getStudyAttributeValue(study, "presnetUrl");
				if (studyVal == null || studyVal.getValue() == null || studyVal.getValue().trim().length() == 0) {
					log.error("No study attribute value 'presnetUrl' specified for study: " + studyName);
					return null;
				}
				
				return studyVal.getValue();
			}
		}
		
		log.error("No study attribute value 'presnetUrl' specified for study: " + studyName);
		return null;
	}
	
	private String getStudySpecificParams(String study, LogicContext context, Integer patientId,
	                                      Map<String, Object> parameters) throws Exception {
		if ("MLP".equalsIgnoreCase(study)) {
			return getMedicalLegalParams(context, patientId, parameters);
		} else if ("diabetes".equalsIgnoreCase(study)) {
			return getDiabetesParams(context, patientId, parameters);
		}
		
		return "";
	}
	
	private String getMedicalLegalParams(LogicContext context, Integer patientId, Map<String, Object> parameters)
	                                                                                                             throws Exception {
		// Get target visit date
		String condition = (String) parameters.get("param2");
		StringBuffer data = new StringBuffer();
		if ("housing".equals(condition)) {
			LogicCriteria conceptCriteria = new LogicCriteriaImpl("Housing_Status");
			Result result = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
			Date match1 = getFirstResultDateByConceptName("Behind on Rent", result);
			Date match2 = getFirstResultDateByConceptName("Eviction Letter", result);
			Date match3 = getFirstResultDateByConceptName("Housing Problems", result);
			String resultDate = getFirstDate(match1, match2, match3);
			data.append("&" + URLEncoder.encode("targetvisitdate", "UTF-8") + "=" + URLEncoder.encode(resultDate, "UTF-8"));
		} else if ("food".equals(condition)) {
			LogicCriteria conceptCriteria = new LogicCriteriaImpl("Food_Insecurity");
			Result result = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
			Date match1 = getFirstResultDateByConceptName("Worried_not_enough_food", result);
			conceptCriteria = new LogicCriteriaImpl("SNAPDeniedLast30Days");
			result = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
			Date match2 = getFirstResultDateByConceptName("yes", result);
			String resultDate = getFirstDate(match1, match2);
			data.append("&" + URLEncoder.encode("targetvisitdate", "UTF-8") + "=" + URLEncoder.encode(resultDate, "UTF-8"));
		} else if ("utilities".equals(condition)) {
			LogicCriteria conceptCriteria = new LogicCriteriaImpl("Utility_Status");
			Result result = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
			Date match1 = getFirstResultDateByConceptName("Heat Shut-off", result);
			Date match2 = getFirstResultDateByConceptName("Utility Shutoff", result);
			String resultDate = getFirstDate(match1, match2);
			data.append("&" + URLEncoder.encode("targetvisitdate", "UTF-8") + "=" + URLEncoder.encode(resultDate, "UTF-8"));
		} else if ("rental".equals(condition)) {
			LogicCriteria conceptCriteria = new LogicCriteriaImpl("Rental_Status");
			Result result = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria);
			Date match1 = getFirstResultDateByConceptName("Not Clean & Safe", result);
			Date match2 = getFirstResultDateByConceptName("Systems Do Not Work", result);
			String resultDate = getFirstDate(match1, match2);
			data.append("&" + URLEncoder.encode("targetvisitdate", "UTF-8") + "=" + URLEncoder.encode(resultDate, "UTF-8"));
		}
		return data.toString();
	}
	
	private String getDiabetesParams(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		return "";
	}
	
	private Date getFirstResultDateByConceptName(String conceptName, List<Result> lst) {
		if (conceptName == null) {
			return null;
		}
		List<Date> dateList = new ArrayList<Date>();
		for (Result element : lst) {
			Concept concept = element.toConcept();
			if (concept == null) {
				continue;
			}
			String elementString = ((ConceptName) concept.getNames().toArray()[0]).getName();
			if (conceptName.equalsIgnoreCase(elementString) && element.getResultDate() != null) {
				dateList.add(element.getResultDate());
			}
		}
		if (dateList.size() == 0) {
			return null;
		}
		Collections.sort(dateList);
		return dateList.get(0);
	}
	
	private String getFirstDate(Date...dates) {
		List<Date> dateList = new ArrayList<Date>();
		for (Date date : dates) {
			if (date != null) {
				dateList.add(date);
			}
		}
		if (dateList.size() > 0) {
			Collections.sort(dateList);
			Date date = dateList.get(0);
			return getFormattedResultDate(date);
		}
		return "";
	}
	
	private String getFormattedResultDate(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormatter.format(date);
	}
}
