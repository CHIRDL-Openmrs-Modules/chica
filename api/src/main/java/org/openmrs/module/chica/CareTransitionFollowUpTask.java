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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
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
	private static final Integer DEFAULT_FOLLOW_UP_TIME_SPAN = 6;
	
	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
		log.info("Initializing care transition follow up scheduled task.");
	}
	
	@Override
	public void execute() {
		Context.openSession();
		List<EmailInfo> emailInfo = new ArrayList<EmailInfo>();
		try {
			emailInfo = getEmailInfo();
			if (emailInfo.size() == 0) {
				return;
			}
			/*****************************
			 * TODO: Create and send emails here
			 *****************************/
		}
		catch (Exception e) {
			log.error("Error creating and sending emails", e);
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
	 * Retrieve all the emails to be send.
	 * 
	 * @return List of EmailInfo objects.
	 */
	private List<EmailInfo> getEmailInfo() {
		List<EmailInfo> emailInfo = new ArrayList<EmailInfo>();
		emailInfo.addAll(getCareTransitionFollowUpEmailInfo());
		return emailInfo;
	}
	
	/**
	 * Retrieve the email information for the Care Transition Study.
	 * 
	 * @return List of EmailInfo objects.
	 */
	private List<EmailInfo> getCareTransitionFollowUpEmailInfo() {
		List<EmailInfo> emailInfo = new ArrayList<EmailInfo>();
		
		ConceptService conceptService = Context.getConceptService();
		Concept transitionConcept = conceptService.getConceptByName("Transition");
		if (transitionConcept == null) {
			log.error("No concept found with name: Transition.  No emails will be sent for Care Transition.");
			return emailInfo;
		}
		
		List<Concept> conceptList = new ArrayList<Concept>();
		conceptList.add(transitionConcept);
		
		Concept discussedWithPatient = conceptService.getConceptByName("Discussed with patient");
		if (discussedWithPatient == null) {
			log.error("No concept found with name: Discussed with patient.  No emails will be sent for " +
					"Care Transition.");
			return emailInfo;
		}
		
		Concept providerIdentified = conceptService.getConceptByName("Provider identified");
		if (providerIdentified == null) {
			log.error("No concept found with name: Provider identified.  No emails will be sent for " +
					"Care Transition.");
			return emailInfo;
		}
		
		List<Concept> answerList = new ArrayList<Concept>();
		answerList.add(discussedWithPatient);
		answerList.add(providerIdentified);
		List<PERSON_TYPE> personTypeList = new ArrayList<PERSON_TYPE>();
		personTypeList.add(PERSON_TYPE.PATIENT);
		String timeSpanStr = getTaskDefinition().getProperty("careTransitionFollowUpSpan");
		Integer timeSpan = null;
		if (timeSpanStr == null || timeSpanStr.trim().length() == 0) {
			log.error("The task property 'careTransitionFollowUpSpan' does not exist.  The default value of " + 
					DEFAULT_FOLLOW_UP_TIME_SPAN + " months will be used");
			timeSpan = DEFAULT_FOLLOW_UP_TIME_SPAN;
		}
		
		try {
			timeSpan = Integer.parseInt(timeSpanStr);
		} catch (NumberFormatException e) {
			log.error("The task property 'careTransitionFollowUpSpan' contains an invalid.  The default value of " + 
					DEFAULT_FOLLOW_UP_TIME_SPAN + " months will be used");
			timeSpan = DEFAULT_FOLLOW_UP_TIME_SPAN;
		}
		
		Calendar endCal = Calendar.getInstance();
		endCal.set(GregorianCalendar.MONTH, endCal.get(GregorianCalendar.MONTH) - timeSpan);
		Date endDate = endCal.getTime();
		
		List<Obs> obsList = Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, 
			null, null, null, null,
			null, endDate, false);
		if (obsList == null || obsList.size() == 0) {
			return emailInfo;
		}
		
		// Create an encounter map to hold the Obs.  We only need to store one Obs per encounter.  Would like to store the 
		// actual Encounter object as the key, but OpenMRS does not override the hasshCode method for Encounter.
		Map<Integer, Obs> encounterObsMap = new HashMap<Integer, Obs>();
		for (Obs obs : obsList) {
			Encounter encounter = obs.getEncounter();
			if (encounter != null) {
				encounterObsMap.put(encounter.getEncounterId(), obs);
			}
		}
		
		// Remove the Encounters that have already had an email sent.
		Concept emailSent = conceptService.getConceptByName("email_sent");
		if (emailSent == null) {
			log.error("No concept found with name: email_sent.  No emails will be sent for " +
					"Care Transition.");
			return emailInfo;
		}
		
		answerList.clear();
		answerList.add(emailSent);
		List<Obs> emailObsList = Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, 
			null, null, null, null,
			endDate, new Date(), false);

		if (emailObsList != null) {
			for (Obs obs : emailObsList) {
				Encounter encounter = obs.getEncounter();
				if (encounter != null) {
					encounterObsMap.remove(encounter.getEncounterId());
				}
			}
		}
		
		obsList.clear();
		obsList = new ArrayList<Obs>(encounterObsMap.values());
		
        Map<Integer, EmailInfo> locationEmailMap = new HashMap<Integer, EmailInfo>();
        ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		for (Obs obs : obsList) {
			Person patient = obs.getPerson();
			Location location = obs.getLocation();
			Integer locationId = location.getLocationId();
			List<String> emailRecipients = new ArrayList<String>();
			EmailInfo info = locationEmailMap.get(locationId);
			if (info != null) {
				info.getPatients().add(patient);
			} else {
				ChirdlLocationAttributeValue lav = backportsService.getLocationAttributeValue(locationId, "careTransitionEmailRecipients");
				if (lav == null || StringUtils.isBlank(lav.getValue())) {
					log.error("Please specify a location attribute for 'careTransitionEmailRecipients'.");
					continue;
				}
				
				String emails = lav.getValue();
				String[] emailArray = emails.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
				for (int i = 0; i < emailArray.length; i++) {
					emailArray[i] = emailArray[i].trim();
				}
				
				List<Person> patients = new ArrayList<Person>();
				patients.add(patient);
				info = new EmailInfo(location, emailArray, patients);
				locationEmailMap.put(locationId, info);
			}
		}
		
		encounterObsMap.clear();
//		log.info("Type 2 Diabetes has " + callInfo.size() + " patient calls pending.");
		return emailInfo;
	}
	
	/**
	 * Class to store the email information per clinic.
	 *
	 * @author Steve McKee
	 */
	private class EmailInfo {
		
		private Location location;
		private String[] emailRecipients;
		private List<Person> patients;
		
		/**
		 * Constructor method
		 * 
		 * @param location The clinic location.
		 * @param emailRecipients Array of email address for receiving the message.
		 * @param patients List of patients to acknowledge in the email for the clinic.
		 */
		public EmailInfo(Location location, String[] emailRecipients, List<Person> patients) {
			this.location = location;
			this.emailRecipients = emailRecipients;
			this.patients = patients;
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
         * @return the patients
         */
        public List<Person> getPatients() {
        	return patients;
        }
	}
}

