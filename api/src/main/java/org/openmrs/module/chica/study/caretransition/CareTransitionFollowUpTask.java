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
package org.openmrs.module.chica.study.caretransition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.MailSender;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

/**
 * @author Steve McKee
 * 
 * Scheduled task to email persons about following up on patients' care transition.
 */
public class CareTransitionFollowUpTask extends AbstractTask {
	
    protected Log log = LogFactory.getLog(this.getClass());
	
	protected static final String GLOBAL_PROPERTY_CARE_TRANSITION_FOLLOWUP_EMAIL_BACKUP_RECIPIENTS = 
            "chica.careTransitionFollowUpEmailBackupRecipients";
	
	private static final Integer DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN = Integer.valueOf(6);
	
	private static final String CARE_TRANSITION_FOLLOW_UP_EMAIL_SUBJECT = "Confidential: Care Transition Follow Up";
	
	private static final String PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS = 
	        "careTransitionFollowUpFromEmailAddress";
	private static final String PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN = "careTransitionFollowUpSpan";
	
	private static final String CONCEPT_TRANSITION = "Transition";
	private static final String CONCEPT_EMAIL_SENT = "email_sent";
	private static final String CONCEPT_DISCUSSED_WITH_PATIENT = "Discussed with patient";
	private static final String CONCEPT_PROVIDER_IDENTIFIED = "Provider identified";
	private static final String CONCEPT_NOT_YET = "Not yet";
	
	private static final String MESSAGE_NO_CONCEPT_FOUND_WITH_NAME = "No concept found with name: ";
	private static final String MESSAGE_NO_EMAILS_WILL_BE_SENT_FOR_CARE_TRANSITION = 
	        ".  No emails will be sent for Care Transition.";
	
	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
		log.info("Initializing care transition follow up scheduled task.");
	}
	
	@Override
	public void execute() {
		Context.openSession();
		List<EmailInfo> emailInfoList = new ArrayList<>();
		try {
			String mailHost = 
					Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EMAIL_SMTP_HOST);
			if (StringUtils.isBlank(mailHost)) {
				log.error("SMTP mail host not specified in global property " + 
				    ChirdlUtilConstants.GLOBAL_PROP_EMAIL_SMTP_HOST + ".  No Care Transition follow up emails will be sent.");
				return;
			}
			
			String fromEmailAddress = getTaskDefinition().getProperty(PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS);
			if (StringUtils.isBlank(fromEmailAddress)) {
				log.error("Task property " + PROPERTY_CARE_TRANSITION_FOLLOW_UP_FROM_EMAIL_ADDRESS + 
					" does not contain a value.  No Care Transition follow up emails will be sent.");
				return;
			}
			
			ConceptService conceptService = Context.getConceptService();
			Concept transitionConcept = conceptService.getConceptByName(CONCEPT_TRANSITION);
			if (transitionConcept == null) {
				log.error(MESSAGE_NO_CONCEPT_FOUND_WITH_NAME + CONCEPT_TRANSITION + 
					".  No Care Transition follow up emails will be sent.");
				return;
			}
			
			emailInfoList = getCareTransitionFollowUpEmailInfo(transitionConcept);
			if (emailInfoList.isEmpty()) {
				return;
			}
			
			for (EmailInfo emailInfo : emailInfoList) {
				sendCareTransitionEmail(emailInfo, mailHost, fromEmailAddress, transitionConcept);
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
		log.info("Shutting down Care Transition follow up scheduled task.");
	}
	
	
	/**
	 * Retrieve the email information for the Care Transition Study.
	 * 
	 * @param Concept for the email status
	 * @return List of EmailInfo objects.
	 */
	private List<EmailInfo> getCareTransitionFollowUpEmailInfo(Concept transitionConcept) {
		ConceptService conceptService = Context.getConceptService();
		
		Concept emailSentConcept = conceptService.getConceptByName(CONCEPT_EMAIL_SENT);
		if (emailSentConcept == null) {
			log.error(MESSAGE_NO_CONCEPT_FOUND_WITH_NAME + CONCEPT_EMAIL_SENT + 
			    MESSAGE_NO_EMAILS_WILL_BE_SENT_FOR_CARE_TRANSITION);
			return new ArrayList<>();
		}
		
		Concept notYetConcept = conceptService.getConceptByName(CONCEPT_NOT_YET);
		if (notYetConcept == null) {
			log.error(MESSAGE_NO_CONCEPT_FOUND_WITH_NAME + CONCEPT_NOT_YET + 
			    MESSAGE_NO_EMAILS_WILL_BE_SENT_FOR_CARE_TRANSITION);
			return new ArrayList<>();
		}
		
		List<PERSON_TYPE> personTypeList = new ArrayList<>();
        personTypeList.add(PERSON_TYPE.PATIENT);
        
        Integer timeSpan = getTranistionObsTimeSpan();
        Date startDate = getTransitionObsStartDate(timeSpan);
        Date endDate = getTransitionObsEndDate(timeSpan);
		
		List<Obs> obsList = getQualifyingObs(conceptService, transitionConcept, personTypeList, startDate, endDate);
		if (obsList == null || obsList.isEmpty()) {
            log.info("There are no patients today needing follow up for Care Transition.  No emails will sent.");
            return new ArrayList<>();
        }
		
		// Create an encounter map of all encounters that have the possible triggers for the email.
		Map<Integer, Encounter> encounterMap = new HashMap<>();
		for (Obs obs : obsList) {
			Encounter encounter = obs.getEncounter();
			if (encounter != null) {
				encounterMap.put(encounter.getEncounterId(), encounter);
			}
		}
		
		// Remove encounters that have "Not yet" specified for Transition.
		encounterMap = removeDisqualifyingEncounters(transitionConcept, notYetConcept, encounterMap, personTypeList, 
		    startDate, endDate);
		
		// Remove the Encounters that have already had an email sent.
		encounterMap = removeEncountersAlreadyEmailed(transitionConcept, emailSentConcept, encounterMap, personTypeList);
		
		// Build the email list.
        return buildEmailInfoList(encounterMap);
	}
	
	/**
	 * Retrieve observations that qualify for the follow up email.
	 * 
	 * @param conceptService Concept service to lookup concept information
	 * @param transitionConcept The Transition concept
	 * @param personTypeList List of person types to include in the query
	 * @param startDate The start date for looking up observations
	 * @param endDate The end date for looking up observations
	 * @return List of observations containing the qualifying concepts
	 */
	private List<Obs> getQualifyingObs(ConceptService conceptService, Concept transitionConcept, 
	        List<PERSON_TYPE> personTypeList, Date startDate, Date endDate) {
	    Concept discussedWithPatientConcept = conceptService.getConceptByName(CONCEPT_DISCUSSED_WITH_PATIENT);
        if (discussedWithPatientConcept == null) {
            log.error(MESSAGE_NO_CONCEPT_FOUND_WITH_NAME + CONCEPT_DISCUSSED_WITH_PATIENT + 
                MESSAGE_NO_EMAILS_WILL_BE_SENT_FOR_CARE_TRANSITION);
            return new ArrayList<>();
        }
        
        Concept providerIdentifiedConcept = conceptService.getConceptByName(CONCEPT_PROVIDER_IDENTIFIED);
        if (providerIdentifiedConcept == null) {
            log.error(MESSAGE_NO_CONCEPT_FOUND_WITH_NAME + CONCEPT_PROVIDER_IDENTIFIED + 
                MESSAGE_NO_EMAILS_WILL_BE_SENT_FOR_CARE_TRANSITION);
            return new ArrayList<>();
        }
        
        // Look for Transition answers of "Discussed with patient" or "Provider identified".
        List<Concept> conceptList = new ArrayList<>();
        conceptList.add(transitionConcept);
        List<Concept> answerList = new ArrayList<>();
        answerList.add(discussedWithPatientConcept);
        answerList.add(providerIdentifiedConcept);
        
        return Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, null, null, null, 
            null, startDate, endDate, false);
	}
	
	/**
	 * Returns the time span to look back for observations.
	 * @return The time span to look back for observations
	 */
	private Integer getTranistionObsTimeSpan() {
	    String timeSpanStr = getTaskDefinition().getProperty(PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN);
        Integer timeSpan = null;
        if (StringUtils.isBlank(timeSpanStr)) {
            log.error("The task property '" + PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN + "' does not exist.  " +
                    "The default value of " + DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN + " months will be used.");
        }
        
        try {
            timeSpan = Integer.valueOf(timeSpanStr);
        } catch (NumberFormatException e) {
            log.error("The task property '" + PROPERTY_CARE_TRANSITION_FOLLOW_UP_SPAN + " contains an invalid value.  " +
                    "The default value of " + DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN + " months will be used.", e);
            timeSpan = DEFAULT_CARE_TRANSITION_FOLLOW_UP_TIME_SPAN;
        }
        
        return timeSpan;
	}
	
	/**
	 * Returns the start time for looking for observations.
	 * 
	 * @param timeSpan The time span to look back for observations
	 * @return The start date to look for observations
	 */
	private Date getTransitionObsStartDate(Integer timeSpan) {
	    // Set the start time another month back.  This is to reduce the number of obs that come back from the query instead 
        // of searching all observations.
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH) - (timeSpan.intValue() + 1));
        return startCal.getTime();
	}
	
	/**
	 * Returns the end time for looking for the observations.
	 * 
	 * @param timeSpan The time span to look back for observations
	 * @return The end date to look for observations
	 */
	private Date getTransitionObsEndDate(Integer timeSpan) {
	    Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, endCal.get(Calendar.MONTH) - timeSpan.intValue());
        return endCal.getTime();
	}
	
	/**
	 * Removes encounters that are disqualified (contains the Not yet answer for the Transition concept).
	 * 
	 * @param transitionConcept The Transition concept
	 * @param notYetConcept The Not yet concept
	 * @param encounterMap Map of encounter ID to encounter
	 * @param personTypeList List of person types
	 * @param startDate The start date to look for observations
	 * @param endDate The end date to look for observations
	 * @return Map of encounter ID to encounter with disqualified encounters removed.
	 */
	private Map<Integer, Encounter> removeDisqualifyingEncounters(Concept transitionConcept, Concept notYetConcept, 
	        Map<Integer, Encounter> encounterMap, List<PERSON_TYPE> personTypeList, Date startDate, Date endDate) {
	    Map<Integer, Encounter> newEncounterMap = new HashMap<>(encounterMap);
	    List<Concept> conceptList = new ArrayList<>();
        conceptList.add(transitionConcept);
        List<Concept> answerList = new ArrayList<>();
        answerList.add(notYetConcept);
        List<Obs> notYetObsList = Context.getObsService().getObservations(null, null, conceptList, answerList, 
            personTypeList, null, null, null, null, startDate, endDate, false);
        if (notYetObsList != null) {
            for (Obs obs : notYetObsList) {
                Encounter encounter = obs.getEncounter();
                if (encounter != null) {
                    Integer encounterId = encounter.getEncounterId();
                    newEncounterMap.remove(encounterId);
                }
            }
        }
        
        return newEncounterMap;
	}
	
	/**
	 * Removes encounters where there has already been an email sent.
	 * 
	 * @param transitionConcept The Transition concept
	 * @param emailSentConcept The email_sent concept
	 * @param encounterMap Map of encounter ID to encounter
	 * @param personTypeList List of person types
	 * @return Map of encounter ID to encounter with encounters removed where there has already been an email sent.
	 */
	private Map<Integer, Encounter> removeEncountersAlreadyEmailed(Concept transitionConcept, Concept emailSentConcept, 
	        Map<Integer, Encounter> encounterMap, List<PERSON_TYPE> personTypeList) {
	    Map<Integer, Encounter> newEncounterMap = new HashMap<>(encounterMap);
	    List<Concept> conceptList = new ArrayList<>();
        conceptList.add(transitionConcept);
        List<Concept> answerList = new ArrayList<>();
        answerList.add(emailSentConcept);
        List<Encounter> currentEncounters = new ArrayList<>(newEncounterMap.values());
        List<Obs> emailObsList = Context.getObsService().getObservations(null, currentEncounters, conceptList, answerList, 
            personTypeList, null, null, null, null, null, null, false);

        if (emailObsList != null) {
            for (Obs obs : emailObsList) {
                Encounter encounter = obs.getEncounter();
                if (encounter != null) {
                    newEncounterMap.remove(encounter.getEncounterId());
                }
            }
        }
        
        return newEncounterMap;
	}
	
	/**
	 * Builds the list of email messages that will be sent.
	 * 
	 * @param encounterMap Map of encounter ID to encounter containing the patient encounters that need emails sent
	 * @return List of EmailInfo object for physician email messages.
	 */
	private List<EmailInfo> buildEmailInfoList(Map<Integer, Encounter> encounterMap) {
	    Map<Integer, EmailInfo> providerEmailMap = new HashMap<>();
        List<Encounter> encounters = new ArrayList<>(encounterMap.values());
        Set<Integer> processedPatientIds = new HashSet<>();
        EncounterService encounterService = Context.getEncounterService();
        EncounterRole attendingProviderRole = 
                encounterService.getEncounterRoleByName(ChirdlUtilConstants.ENCOUNTER_ROLE_ATTENDING_PROVIDER);
        for (Encounter encounter : encounters) {
            Patient patient = encounter.getPatient();
            Integer patientId = patient.getPatientId();
            Set<Provider> providers = encounter.getProvidersByRole(attendingProviderRole, true);
            
            // We only want to send one email per patient.  We were informed to ignore encounters where there are no 
            // providers assigned.
            if (processedPatientIds.contains(patientId) || providers.isEmpty()) {
                continue;
            }
            
            for (Provider provider : providers) {
                Integer providerId = provider.getProviderId();
                EmailInfo info = providerEmailMap.get(providerId);
                if (info != null) {
                    info.getEncounters().add(encounter);
                } else {
                    List<Encounter> encounterList = new ArrayList<>();
                    encounterList.add(encounter);
                    info = new EmailInfo(provider, encounterList);
                    providerEmailMap.put(providerId, info);
                }
            }
            
            processedPatientIds.add(patientId);
        }
        
        List<EmailInfo> emailInfo = new ArrayList<>(providerEmailMap.values());
        if (emailInfo.isEmpty()) {
            log.info("There are no patients today needing follow up for Care Transition.  No email will sent.");
        }
        
        encounterMap.clear();
        providerEmailMap.clear();
        return emailInfo;
	}
	
	/**
	 * Send an email to a specified location about patients that need care transition follow up.
	 * 
	 * @param emailInfo Contains the information needed to construct the email.
	 * @param mailHost The SMTP mail host used to send the email.
	 * @param fromEmailAddress The address the email is from.
	 * @param transitionConcept Concept for the email status 
	 */
	private void sendCareTransitionEmail(EmailInfo emailInfo, String mailHost, String fromEmailAddress, 
	        Concept transitionConcept) {
		List<Encounter> encounters = emailInfo.getEncounters();
		Properties mailProps = new Properties();
		mailProps.put(ChirdlUtilConstants.EMAIL_SMTP_HOST_PROPERTY, mailHost);
		MailSender mailSender = new MailSender(mailProps);
		StringBuilder body = new StringBuilder("The following ");
		body.append(encounters.size() == 1 ? "patient was" : "patients were");
		body.append(" counseled for adult care transition six months ago.  ");
		body.append("Please follow up to confirm successful care transition.");
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		int successfulSaves = 0;
		List<Obs> savedObs = new ArrayList<>();
		for (Encounter encounter : encounters) {
			Integer encounterId = encounter.getEncounterId();
			Patient patient = encounter.getPatient();
			try {
				Obs obs = Util.saveObs(patient, transitionConcept, encounterId.intValue(), CONCEPT_EMAIL_SENT, new Date());
				savedObs.add(obs);
			} catch (Exception e) {
				log.error("Error saving " + CONCEPT_TRANSITION + " observation for patient: " + 
						patient.getPatientId() + " encounter: " + encounterId, e);
				continue;
			}
			
			String givenName = patient.getGivenName();
			String familyName = patient.getFamilyName();
			body.append(givenName);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(familyName);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(ChirdlUtilConstants.GENERAL_INFO_OPEN_PAREN);
			body.append(getPatientIdentifier(patient));
			body.append(ChirdlUtilConstants.GENERAL_INFO_CLOSE_PAREN);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			successfulSaves++;
		}
		
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append("Thank you,");
		body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
		body.append("CHICA Care Transition");
		
		boolean success = mailSender.sendMail(fromEmailAddress, emailInfo.getProviderEmails(), 
			CARE_TRANSITION_FOLLOW_UP_EMAIL_SUBJECT, body.toString());
		Integer providerId = emailInfo.getProvider().getProviderId();
		if (!success) {
			log.error("An error occurred sending Care Transition followup email for provider ID " + providerId + ".");
			ObsService obsService = Context.getObsService();
			for (Obs obs : savedObs) {
				obsService.voidObs(obs, "Care Transition follow up email was unable to be sent.");
			}
		} else {
			log.info("Care Transition Follow Up Email sent for provider ID " + providerId + " containing " + 
					successfulSaves + " patient(s).");
		}
	}
	
	/**
	 * Get the patient's identifier.  This will look at the MRN_EHR first and default to the preferred identifier if it 
	 * does not exist.
	 * 
	 * @param patient The patient for identifier lookup
	 * @return patient identifier
	 */
	private String getPatientIdentifier(Patient patient) {
	    // We ideally want to return the EHR identifier because this will match the EHR identifier better
	    PatientIdentifier pi = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR);
	    if (pi != null && StringUtils.isNotBlank(pi.getIdentifier())) {
	        return pi.getIdentifier();
	    }
	    
	    // Return the preferred identifier
	    pi =  patient.getPatientIdentifier();
	    return pi == null ? ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING : pi.getIdentifier();
	}
	
	/**
	 * Class to store the email information per clinic.
	 *
	 * @author Steve McKee
	 */
	private class EmailInfo {
		
		private Provider provider;
		private List<Encounter> encounters;
		
		/**
		 * Constructor method
		 * 
		 * @param provider The provider.
		 * @param encounters List of encounters to acknowledge in the email for the clinic.
		 */
		public EmailInfo(Provider provider, List<Encounter> encounters) {
			this.provider = provider;
			this.encounters = encounters;
		}
		
        /**
         * @return the provider
         */
        public Provider getProvider() {
        	return provider;
        }
        
        /**
         * @return the encounters
         */
        public List<Encounter> getEncounters() {
        	return encounters;
        }
        
        /**
         * Returns the list of email addresses specified for a provider.  If the provider does not have an email specified, 
         * The list of email addresses from the chica.careTransitionFollowUpEmailBackupRecipients global property will be 
         * returned.
         * 
         * @return Array of email addresses
         */
        public String[] getProviderEmails() {
            Integer providerId = provider.getProviderId();
            Person person = provider.getPerson();
            if (person != null) {
                PersonAttribute emailAttribute = person.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_EMAIL);
                if (emailAttribute != null && StringUtils.isNotBlank(emailAttribute.getValue())) {
                    return emailAttribute.getValue().split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
                }
                
                log.error("There is no email person attribute specified for provider ID " + providerId + ".  Email " +
                    " will be sent to the personnel stored in the " + 
                    GLOBAL_PROPERTY_CARE_TRANSITION_FOLLOWUP_EMAIL_BACKUP_RECIPIENTS + " global property.");
                return getDefaultEmails();
            }
            
            log.error("No person object found for provider ID " + providerId);
            return getDefaultEmails();
        }
        
        /**
         * Retrieve the default email addresses from the chica.careTransitionFollowUpEmailBackupRecipients global property.
         * 
         * @return Array of email addresses
         */
        private String[] getDefaultEmails() {
            String[] emails = new String[0];
            String defaultEmails = Context.getAdministrationService().getGlobalProperty(
                GLOBAL_PROPERTY_CARE_TRANSITION_FOLLOWUP_EMAIL_BACKUP_RECIPIENTS);
            if (StringUtils.isNotBlank(defaultEmails)) {
                emails = defaultEmails.split(ChirdlUtilConstants.GENERAL_INFO_COMMA); 
            } else {
                log.error("No value specified for global property: " + 
                    GLOBAL_PROPERTY_CARE_TRANSITION_FOLLOWUP_EMAIL_BACKUP_RECIPIENTS);
            }
            
            return emails;
        }
	}
}

