/**
 * 
 */
package org.openmrs.module.chica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
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
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.MailSender;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
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
	
	@Override
	public void execute() {
		Context.openSession();
		
		final String SUICIDE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("SUICIDE_NOTIFICATION_TEXT");
		final String ABUSE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("ABUSE_NOTIFICATION_TEXT");
		final String DV_NOTIFICATION_TEXT = this.taskDefinition.getProperty("DV_NOTIFICATION_TEXT");
		final String FROM_EMAIL = this.taskDefinition.getProperty("FROM_EMAIL");
		final String SUBJECT = this.taskDefinition.getProperty("SUBJECT");
		Integer NUM_DAYS = null;
		try {
			NUM_DAYS = Integer.parseInt(this.taskDefinition.getProperty("NUM_DAYS"));
		}
		catch (Exception e) {
			log.error("Error generated", e);
		}
		
		if (SUICIDE_NOTIFICATION_TEXT == null || ABUSE_NOTIFICATION_TEXT == null || FROM_EMAIL == null
		        || DV_NOTIFICATION_TEXT == null || SUBJECT == null || NUM_DAYS == null) {
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
		
		//get encounters that should have been submitted but have not been processed by the task
		List<org.openmrs.Encounter> encounters = encounterService.getEncounters(null, null, startDate, endDate, null, null,
		    null, false);
		
		//get suicide observations
		ArrayList<String> ruleNames = new ArrayList<String>();
		ruleNames.add("Depression_SuicidePWS");
		ruleNames.add("bf_suicide_PWS");
		createAndSendNotifications(encounters, ruleNames, SUICIDE_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT);
		
		//get abuse observations
		ruleNames = new ArrayList<String>();
		ruleNames.add("Abuse_Concern_PWS");
		createAndSendNotifications(encounters, ruleNames, ABUSE_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT);
		
		//get domestic violence observations
		ruleNames = new ArrayList<String>();
		ruleNames.add("Dom_Viol_PWS");
		createAndSendNotifications(encounters, ruleNames, DV_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT);
		
		Context.closeSession();
		
	}
	
	/**
	 * Sends notification emails if appropriate
	 * 
	 * @param encounters
	 * @param ruleNames
	 * @param notificationText
	 * @param fromEmail
	 * @param subject
	 */
	private void createAndSendNotifications(List<org.openmrs.Encounter> encounters, ArrayList<String> ruleNames,
	                                        String notificationText, String fromEmail, String subject) {
		LocationService locationService = Context.getLocationService();
		org.openmrs.module.chica.service.EncounterService chicaEncounterService = Context
		        .getService(org.openmrs.module.chica.service.EncounterService.class);
		DssService dssService = Context.getService(DssService.class);
		
		HashSet<org.openmrs.Encounter> notificationSet = new HashSet<org.openmrs.Encounter>();
		
		for (String ruleName : ruleNames) {
			Rule rule = dssService.getRule(ruleName);
			addEncounters(encounters, notificationSet, rule.getRuleId(), ruleName);
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
					sendEmailNotification(locationId, locationTagId, chicaEncounter, notificationText, fromEmail, subject);
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
	 * @param fromEmail
	 * @param subject
	 */
	private void sendEmailNotification(Integer locationId, Integer locationTagId, Encounter chicaEncounter, String riskText,
	                                   String fromEmail, String subject) {
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
									        + " found for personId " + personId + ".  No one will be emailed.");
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
	 * @param encounters
	 * @param notificationSet
	 * @param ruleId
	 * @param ruleName
	 */
	private void addEncounters(List<org.openmrs.Encounter> encounters, HashSet<org.openmrs.Encounter> notificationSet,
	                           Integer ruleId, String ruleName) {
		ATDService atdService = Context.getService(ATDService.class);
		ObsService obsService = Context.getObsService();
		
		for (org.openmrs.Encounter encounter : encounters) {
			Integer encounterId = encounter.getEncounterId();
			
			//don't send a notification if the rule didn't fire
			boolean ruleFired = atdService.ruleFiredForEncounter(encounterId, ruleId);
			if (!ruleFired) {
				continue;
			}
			
			//see if any boxes checked on prompt
			boolean oneBoxChecked = atdService.oneBoxChecked(encounterId, ruleId);
			
			//if there are no boxes checked, add the encounter to the notification list
			if (!oneBoxChecked) {
				
				//special processing for combined suicide/depression rule
				if (ruleName.equals("Depression_SuicidePWS")) {
					List<Person> patientList = new ArrayList<Person>();
					patientList.add(encounter.getPatient());
					List<Concept> questionList = new ArrayList<Concept>();
					ConceptService conceptService = Context.getConceptService();
					Concept suicideConcept = conceptService.getConceptByName("suicide_concerns");
					if (suicideConcept != null) {
						questionList.add(suicideConcept);
						List<String> sort = new ArrayList<String>();
						sort.add("obsDatetime");
						
						List<Obs> suicideObs = obsService.getObservations(patientList, null, questionList, null, null, null,
						    sort, 1, null, null, null, false);
						if (suicideObs != null && suicideObs.size() > 0) {
							Obs obs = suicideObs.get(0);
							if (obs.getValueCoded() != null && obs.getValueCoded().getName().getName().equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE)) {
								notificationSet.add(encounter);
							}
						}
					}
				} else {
					notificationSet.add(encounter);
				}
			}
		}
	}
}
