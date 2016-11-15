/**
 * 
 */
package org.openmrs.module.chica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.MailSender;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Tammy Dugan
 */

public class AlertPhysicianHighRiskConditions extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final String LOC_TAG_ATTR_HIGH_RISK_CONTACT = "HighRiskContact";
	
	private static int NUM_DAYS = 3;//number of days allowed to submit PWS
	
	@Override
	public void execute() {
		Context.openSession();
		
		final String SUICIDE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("SUICIDE_NOTIFICATION_TEXT");
		final String ABUSE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("ABUSE_NOTIFICATION_TEXT");
		final String FROM_EMAIL = this.taskDefinition.getProperty("FROM_EMAIL");
		
		if(SUICIDE_NOTIFICATION_TEXT == null||ABUSE_NOTIFICATION_TEXT==null||FROM_EMAIL==null){
			log.error("One or more required properties for AlertPhysicianHighRiskCondition are not set");
			return;
		}
		
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.add(Calendar.DAY_OF_YEAR, -(NUM_DAYS + 1));
		
		Date startDate = date.getTime();
		
		date.add(Calendar.DAY_OF_YEAR, 1);
		
		Date endDate = date.getTime();
		
		EncounterService encounterService = Context.getEncounterService();
		ConceptService conceptService = Context.getConceptService();
		
		//get encounters that should have been submitted but have not been processed by the task
		List<org.openmrs.Encounter> encounters = encounterService.getEncounters(null, null, startDate, endDate, null, null,
		    null, false);
		
		//get suicide observations
		List<Concept> questions = new ArrayList<Concept>();
		questions.add(conceptService.getConceptByName("suicide_concerns"));
		List<Concept> answers = new ArrayList<Concept>();
		answers.add(conceptService.getConceptByName("True"));
		answers.add(conceptService.getConceptByName("yes"));
		ArrayList<String> ruleNames = new ArrayList<String>();
		ruleNames.add("Depression_SuicidePWS");
		ruleNames.add("bf_suicide_PWS");
		
		createAndSendNotifications(encounters, questions, answers, ruleNames, SUICIDE_NOTIFICATION_TEXT, FROM_EMAIL);
		
		//get abuse observations
		questions = new ArrayList<Concept>();
		questions.add(conceptService.getConceptByName("Abuse_Concern"));
		answers = new ArrayList<Concept>();
		answers.add(conceptService.getConceptByName("yes_parent"));
		ruleNames = new ArrayList<String>();
		ruleNames.add("Abuse_Concern_PWS");
		
		createAndSendNotifications(encounters, questions, answers, ruleNames, ABUSE_NOTIFICATION_TEXT, FROM_EMAIL);
		
		Context.closeSession();
		
	}
	
	/**
	 * Looks up observations for conditions and sends notification emails if appropriate
	 * 
	 * @param encounters
	 * @param questions
	 * @param answers
	 * @param ruleNames
	 * @param notificationText
	 */
	private void createAndSendNotifications(List<org.openmrs.Encounter> encounters, List<Concept> questions,
	                                        List<Concept> answers, ArrayList<String> ruleNames, String notificationText,
	                                        String fromEmail) {
		LocationService locationService = Context.getLocationService();
		ObsService obsService = Context.getObsService();
		org.openmrs.module.chica.service.EncounterService chicaEncounterService = Context
		        .getService(org.openmrs.module.chica.service.EncounterService.class);
		DssService dssService = Context.getService(DssService.class);
		
		HashSet<org.openmrs.Encounter> notificationSet = new HashSet<org.openmrs.Encounter>();
		
		List<Obs> obs = obsService.getObservations(null, encounters, questions, answers, null, null, null, null, null, null,
		    null, false);
		
		for (String ruleName : ruleNames) {
			Rule rule = dssService.getRule(ruleName);
			addEncounters(obs, notificationSet, rule.getRuleId());
		}
		
		//send notification emails
		for (org.openmrs.Encounter encounter : notificationSet) {
			Encounter chicaEncounter = (Encounter) chicaEncounterService.getEncounter(encounter.getEncounterId());
			Integer locationId = chicaEncounter.getLocation().getLocationId();
			String printerLocation = chicaEncounter.getPrinterLocation();
			if (printerLocation != null) {
				LocationTag locTag = locationService.getLocationTagByName(printerLocation.trim());
				if (locTag != null) {
					Integer locationTagId = locTag.getLocationTagId();
					sendEmailNotification(locationId, locationTagId, chicaEncounter, notificationText, fromEmail);
				}
			}
		}
	}
	
	/**
	 * Construct and send notification emails
	 * 
	 * @param locationId
	 * @param locationTagId
	 * @param chicaEncounter
	 * @param riskText
	 */
	private void sendEmailNotification(Integer locationId, Integer locationTagId, Encounter chicaEncounter, String riskText,
	                                   String fromEmail) {
		ChirdlUtilBackportsService cub = Context.getService(ChirdlUtilBackportsService.class);
		LocationTagAttributeValue lav = cub.getLocationTagAttributeValue(locationTagId, LOC_TAG_ATTR_HIGH_RISK_CONTACT,
		    locationId);
		PersonService personService = Context.getPersonService();
		if (lav != null) {
			String highRiskPersonIdStr = lav.getValue();
			if (highRiskPersonIdStr != null && highRiskPersonIdStr.trim().length() > 0) {
				try {
					
					String[] personIdStrs = highRiskPersonIdStr.split(",");
					
					for (String personIdStr : personIdStrs) {
						
						Integer personId = Integer.parseInt(personIdStr);
						Person person = personService.getPerson(personId);
						if (person != null) {
							PersonAttribute personAttribute = person
							        .getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_EMAIL);
							if (personAttribute != null) {
								
								// Get the email addresses
								String emailAddys = personAttribute.getValue();
								if (emailAddys == null || emailAddys.trim().length() == 0) {
									log.error("No valid " + ChirdlUtilConstants.PERSON_ATTRIBUTE_EMAIL
									        + " found for location " + locationId + ".  No one will be emailed.");
									return;
								}
								String[] emailList = emailAddys.split(",");
								try {
									
									String smtpMailHost = Context.getAdministrationService().getGlobalProperty(
									    "chirdlutil.smtpMailHost");
									if (smtpMailHost == null) {
										log.error("Please specify global property chirdlutil.smtpMailHost for correct email operability.");
										return;
									}
									
									String sendingEmail = fromEmail;
									String subject = "[CONFIDENTIAL] CHICA high risk conditions (suicide or abuse)";
									Patient patient = chicaEncounter.getPatient();
									
									String body = riskText
									        + "\n"
									        + "We do not have a record in CHICA of the patient's provider addressing this issue.\n"
									        + "We recommend checking the patient's medical record to make sure the issue was addressed.\n\n";
									
									body += "mrn: " + patient.getPatientIdentifier() + "\tname: " + patient.getGivenName()
									        + " " + patient.getFamilyName();
									
									Properties mailProps = new Properties();
									mailProps.put("mail.smtp.host", smtpMailHost);
									MailSender mailSender = new MailSender(mailProps);
									mailSender.sendMail(sendingEmail, emailList, subject, body, null);
								}
								catch (Exception e) {
									log.error("Error sending email for high risk condition", e);
									return;
								}
							}
						}
					}
				}
				catch (Exception e) {
					log.error("Error sending email for high risk condition", e);
				}
			}
		}
	}
	
	/**
	 * Determine which encounters should be added the the notification set
	 * 
	 * @param obs
	 * @param notificationSet
	 * @param ruleId
	 */
	private void addEncounters(List<Obs> obs, HashSet<org.openmrs.Encounter> notificationSet, Integer ruleId) {
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		ATDService atdService = Context.getService(ATDService.class);
		final String FORM_NAME = this.taskDefinition.getProperty("FORM_NAME");
		final String END_STATE_NAME = this.taskDefinition.getProperty("END_STATE_NAME");
		if(FORM_NAME == null||END_STATE_NAME==null){
			log.error("One or more required properties for AlertPhysicianHighRiskCondition are not set");
			return;
		}
		for (Obs currOb : obs) {
			org.openmrs.Encounter encounter = currOb.getEncounter();
			Integer encounterId = encounter.getEncounterId();
			Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
			Form form = Context.getFormService().getForm(FORM_NAME);
			Integer formId = form.getFormId();
			State endState = backportsService.getStateByName(END_STATE_NAME);
			Integer endStateId = endState.getStateId();
			Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapEnd, encounterId, endStateId, true);
			
			boolean containsEndState = formIdToPatientStateMapEnd.containsKey(formId);
			
			if (containsEndState) {
				//if the PWS was submitted, see if any boxes checked on prompt
				boolean oneBoxChecked = atdService.oneBoxChecked(encounterId, ruleId);
				
				//if there are no boxes checked, add the encounter to the notification list
				if (!oneBoxChecked) {
					notificationSet.add(encounter);
				}
			} else {
				//if the PWS was not submitted, add the encounter to the notification list
				notificationSet.add(encounter);
			}
			
		}
		
	}
	
}
