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
package org.openmrs.module.chica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.MailSender;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

/**
 * @author Steve McKee
 * 
 * Scheduled task to email persons about following up on a patient's care transition.
 */
public class CareTransitionFollowUpTask extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final Integer DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN = 6;
	
	private static final String CARE_TRANSITION_FOLLOW_UP_EMAIL_SUBJECT = "Care Transition Follow Up";
	
	private static final String LOCATION_ATTR_CARE_TRANSITION_FOLLOWUP_EMAIL_RECIPIENTS = "careTransitionFollowUpEmailRecipients";
	
	private static final String PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS = "careTransitionFollowUpFromEmailAddress";
	private static final String PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN = "careTransitionFollowUpSpan";
	
	private static final String CONCEPT_TRANSITION = "Transition";
	private static final String CONCEPT_DISCUSSED_WITH_PATIENT = "Discussed with patient";
	private static final String CONCEPT_PROVIDER_IDENTIFIED = "Provider identified";
	private static final String CONCEPT_EMAIL_SENT = "email_sent";
	private static final String CONCEPT_ANSWER_YES = "yes";
	
	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
		log.info("Initializing care transition follow up scheduled task.");
	}
	
	@Override
	public void execute() {
		Context.openSession();
		List<EmailInfo> emailInfoList = new ArrayList<EmailInfo>();
		try {
			String mailHost = 
					Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EMAIL_SMTP_HOST);
			if (StringUtils.isBlank(mailHost)) {
				log.error("SMTP mail host not specified in global property " + ChirdlUtilConstants.GLOBAL_PROP_EMAIL_SMTP_HOST + 
					".  No Care Transition follow up emails will be sent.");
				return;
			}
			
			String fromEmailAddress = getTaskDefinition().getProperty(PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS);
			if (StringUtils.isBlank(fromEmailAddress)) {
				log.error("Task property " + PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS + 
					" does not contain a value.  No Care Transition follow up emails will be sent.");
				return;
			}
			
			ConceptService conceptService = Context.getConceptService();
			Concept emailSentConcept = conceptService.getConceptByName(CONCEPT_EMAIL_SENT);
			if (emailSentConcept == null) {
				log.error("No concept found with name: " + CONCEPT_EMAIL_SENT + ".  No emails will be sent for " +
						"Care Transition.");
				return;
			}
			
			emailInfoList = getCareTransitionFollowUpEmailInfo(emailSentConcept);
			if (emailInfoList.size() == 0) {
				return;
			}
			
			for (EmailInfo emailInfo : emailInfoList) {
				sendCareTransitionEmail(emailInfo, mailHost, fromEmailAddress, emailSentConcept);
			}
		}
		catch (Exception e) {
			log.error("Error creating and sending emails.", e);
		}
		finally {
			Context.closeSession();
		}
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		log.info("Shutting down care transition follow up scheduled task.");
	}
	
	
	/**
	 * Retrieve the email information for the Care Transition Study.
	 * 
	 * @param emailSentConcept Concept for sent email.
	 * @return List of EmailInfo objects.
	 */
	private List<EmailInfo> getCareTransitionFollowUpEmailInfo(Concept emailSentConcept) {
		List<EmailInfo> emailInfo = new ArrayList<EmailInfo>();
		
		ConceptService conceptService = Context.getConceptService();
		Concept transitionConcept = conceptService.getConceptByName(CONCEPT_TRANSITION);
		if (transitionConcept == null) {
			log.error("No concept found with name: " + CONCEPT_TRANSITION + ".  No emails will be sent for Care Transition.");
			return emailInfo;
		}
		
		List<Concept> conceptList = new ArrayList<Concept>();
		conceptList.add(transitionConcept);
		
		Concept discussedWithPatient = conceptService.getConceptByName(CONCEPT_DISCUSSED_WITH_PATIENT);
		if (discussedWithPatient == null) {
			log.error("No concept found with name: " + CONCEPT_DISCUSSED_WITH_PATIENT + ".  No emails will be sent for " +
					"Care Transition.");
			return emailInfo;
		}
		
		Concept providerIdentified = conceptService.getConceptByName(CONCEPT_PROVIDER_IDENTIFIED);
		if (providerIdentified == null) {
			log.error("No concept found with name: " + CONCEPT_PROVIDER_IDENTIFIED + ".  No emails will be sent for " +
					"Care Transition.");
			return emailInfo;
		}
		
		List<Concept> answerList = new ArrayList<Concept>();
		answerList.add(discussedWithPatient);
		answerList.add(providerIdentified);
		List<PERSON_TYPE> personTypeList = new ArrayList<PERSON_TYPE>();
		personTypeList.add(PERSON_TYPE.PATIENT);
		String timeSpanStr = getTaskDefinition().getProperty(PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN);
		Integer timeSpan = null;
		if (timeSpanStr == null || timeSpanStr.trim().length() == 0) {
			log.error("The task property '" + PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN + "' does not exist.  " +
					"The default value of " + DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN + " months will be used.");
			timeSpan = DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN;
		}
		
		try {
			timeSpan = Integer.parseInt(timeSpanStr);
		} catch (NumberFormatException e) {
			log.error("The task property '" + PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN + " contains an invalid value.  " +
					"The default value of " + DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN + " months will be used.");
			timeSpan = DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN;
		}
		
		Calendar endCal = Calendar.getInstance();
		endCal.set(GregorianCalendar.MONTH, endCal.get(GregorianCalendar.MONTH) - timeSpan);
		Date endDate = endCal.getTime();
		
		List<Obs> obsList = Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, 
			null, null, null, null, null, endDate, false);
		if (obsList == null || obsList.size() == 0) {
			log.info("There are no patients today needing follow up for care transition.  No email will sent.");
			return emailInfo;
		}
		
		// Create an encounter set.  Would like to use the actual Encounter object, but OpenMRS does override the hashCode
		// method to support it.
		Set<Integer> encounterSet = new HashSet<Integer>();
		for (Obs obs : obsList) {
			Encounter encounter = obs.getEncounter();
			if (encounter != null) {
				encounterSet.add(encounter.getEncounterId());
			}
		}
		
		// Remove the Encounters that have already had an email sent.
		answerList.clear();
		answerList.add(emailSentConcept);
		List<Obs> emailObsList = Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, 
			null, null, null, null, endDate, new Date(), false);

		if (emailObsList != null) {
			for (Obs obs : emailObsList) {
				Encounter encounter = obs.getEncounter();
				if (encounter != null) {
					encounterSet.remove(encounter.getEncounterId());
				}
			}
		}
		
        Map<Integer, EmailInfo> locationEmailMap = new HashMap<Integer, EmailInfo>();
        ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
        EncounterService encounterService = Context.getEncounterService();
        Iterator<Integer> encounterIter = encounterSet.iterator();
        Set<Integer> processedPatientIds = new HashSet<Integer>();
		while (encounterIter.hasNext()) {
			Integer encounterId = encounterIter.next();
			Encounter encounter = encounterService.getEncounter(encounterId);
			Patient patient = encounter.getPatient();
			Integer patientId = patient.getPatientId();
			// We only want to send one email per patient.
			if (processedPatientIds.contains(patientId)) {
				continue;
			}
			
			Location location = encounter.getLocation();
			Integer locationId = location.getLocationId();
			EmailInfo info = locationEmailMap.get(locationId);
			if (info != null) {
				info.getEncounters().add(encounter);
			} else {
				ChirdlLocationAttributeValue lav = backportsService.getLocationAttributeValue(
					locationId, LOCATION_ATTR_CARE_TRANSITION_FOLLOWUP_EMAIL_RECIPIENTS);
				if (lav == null || StringUtils.isBlank(lav.getValue())) {
					log.error("Please specify a location attribute for '" + 
							LOCATION_ATTR_CARE_TRANSITION_FOLLOWUP_EMAIL_RECIPIENTS + "'.");
					continue;
				}
				
				String emails = lav.getValue();
				String[] emailArray = emails.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
				for (int i = 0; i < emailArray.length; i++) {
					emailArray[i] = emailArray[i].trim();
				}
				
				List<Encounter> encounters = new ArrayList<Encounter>();
				encounters.add(encounter);
				info = new EmailInfo(location, emailArray, encounters);
				locationEmailMap.put(locationId, info);
			}
			
			processedPatientIds.add(patientId);
		}
		
		encounterSet.clear();
		return emailInfo;
	}
	
	private void sendCareTransitionEmail(EmailInfo emailInfo, String mailHost, String fromEmailAddress, Concept emailSentConcept) {
		Location location = emailInfo.getLocation();
		if (location == null) {
			log.error("Location is null.  No Care Transition Follow Up email will be sent.");
			return;
		}
		
		List<Encounter> encounters = emailInfo.getEncounters();
		if (encounters == null || encounters.size() == 0) {
			log.info("There are no patients today needing follow up for care transition at " + location.getName() + 
				".  No email will sent.");
			return;
		}
		
		Properties mailProps = new Properties();
		mailProps.put(ChirdlUtilConstants.EMAIL_SMTP_HOST_PROPERTY, mailHost);
		MailSender mailSender = new MailSender(mailProps);
		StringBuffer body = new StringBuffer("The following ");
		body.append(encounters.size() == 1 ? "patient was" : "patients were");
		body.append(" counseled for adult care transition six months ago.  ");
		body.append("Please follow up to confirm successful care transition.");
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		int successfulSaves = 0;
		for (Encounter encounter : encounters) {
			Integer encounterId = encounter.getEncounterId();
			Patient patient = encounter.getPatient();
			try {
				Util.saveObs(patient, emailSentConcept, encounterId, CONCEPT_ANSWER_YES, new Date());
			} catch (Exception e) {
				log.error("Error saving " + CONCEPT_EMAIL_SENT + " observation for patient: " + 
						patient.getPatientId() + " encounter: " + encounterId);
				continue;
			}
			
			String givenName = patient.getGivenName();
			String familyName = patient.getFamilyName();
			body.append(givenName);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(familyName);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(ChirdlUtilConstants.GENERAL_INFO_OPEN_PAREN);
			body.append(patient.getPatientIdentifier());
			body.append(ChirdlUtilConstants.GENERAL_INFO_CLOSE_PAREN);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			successfulSaves++;
		}
		
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append("Thank you,");
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append("CHICA Care Transition");
		
		mailSender.sendMail(fromEmailAddress, emailInfo.getEmailRecipients(), 
			CARE_TRANSITION_FOLLOW_UP_EMAIL_SUBJECT, body.toString());
		
		log.info("Care Transition Follow Up Email sent for " + emailInfo.getLocation().getName() + " containing " + 
				successfulSaves + " patient(s).");
	}
	
	/**
	 * Class to store the email information per clinic.
	 *
	 * @author Steve McKee
	 */
	private class EmailInfo {
		
		private Location location;
		private String[] emailRecipients;
		private List<Encounter> encounters;
		
		/**
		 * Constructor method
		 * 
		 * @param location The clinic location.
		 * @param emailRecipients Array of email address for receiving the message.
		 * @param encounters List of encounters to acknowledge in the email for the clinic.
		 */
		public EmailInfo(Location location, String[] emailRecipients, List<Encounter> encounters) {
			this.location = location;
			this.emailRecipients = emailRecipients;
			this.encounters = encounters;
		}
		
        /**
         * @return the location
         */
        public Location getLocation() {
        	return location;
        }
		
        /**
         * @return the emailRecipients
         */
        public String[] getEmailRecipients() {
        	return emailRecipients;
        }
        
        /**
         * @return the encounters
         */
        public List<Encounter> getEncounters() {
        	return encounters;
        }
	}
}

