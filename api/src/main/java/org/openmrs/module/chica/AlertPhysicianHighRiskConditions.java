/**
 * 
 */
package org.openmrs.module.chica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
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
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tammy Dugan
 */

public class AlertPhysicianHighRiskConditions extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(AlertPhysicianHighRiskConditions.class);
	
	@Override
	public void execute() {
		Context.openSession();
		
		final String SUICIDE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("SUICIDE_NOTIFICATION_TEXT");
		final String ABUSE_NOTIFICATION_TEXT = this.taskDefinition.getProperty("ABUSE_NOTIFICATION_TEXT");
		final String DV_NOTIFICATION_TEXT = this.taskDefinition.getProperty("DV_NOTIFICATION_TEXT");
		final String FROM_EMAIL = this.taskDefinition.getProperty("FROM_EMAIL");
		final String SUBJECT = this.taskDefinition.getProperty("SUBJECT");
		
		final String ABUSE_CONCEPT = "Abuse_Concern";
		final String DV_CONCEPT = "Domest_Violence_Concern";
		
		Integer NUM_DAYS = null;
		try {
			NUM_DAYS = Integer.parseInt(this.taskDefinition.getProperty("NUM_DAYS"));
		}
		catch (Exception e) {
			log.error("Exception parsing property NUM_DAYS.", e);
		}
		
		Integer RISK_MONTHS = null;
		try {
			RISK_MONTHS = Integer.parseInt(this.taskDefinition.getProperty("RISK_MONTHS"));
		}
		catch (Exception e) {
			log.error("Exception parsing RISK_MONTHS.", e);
		}
		
		if (SUICIDE_NOTIFICATION_TEXT == null || ABUSE_NOTIFICATION_TEXT == null || FROM_EMAIL == null
		        || DV_NOTIFICATION_TEXT == null || SUBJECT == null || NUM_DAYS == null) {
			log.error("One or more required properties for AlertPhysicianHighRiskCondition are not set.");
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
		
		Date dateThreshold = null;
		
		if (RISK_MONTHS != null) {
			Calendar dateCal = Calendar.getInstance();
			dateCal.set(Calendar.HOUR_OF_DAY, 0);
			dateCal.set(Calendar.MINUTE, 0);
			dateCal.set(Calendar.SECOND, 0);
			dateCal.add(Calendar.MONTH, -RISK_MONTHS);
			dateThreshold = dateCal.getTime();
		}
		
		EncounterService encounterService = Context.getEncounterService();
		
		//get encounters that should have been submitted but have not been processed by the task
		List<org.openmrs.Encounter> encounters = encounterService.getEncounters(null, null, startDate, endDate, null, null,
		    null, null, null, false); // CHICA-1151 Add null parameters for Collection<VisitType> and Collection<Visit>
		
		//get suicide observations
		ArrayList<String> ruleNames = new ArrayList<>();
		ruleNames.add(ChicaConstants.RULE_NAME_DEPRESSION_SUICIDE_PWS);
		ruleNames.add(ChicaConstants.RULE_NAME_BF_SUICIDE_PWS);
		
		createAndSendNotifications(encounters, ruleNames, SUICIDE_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT, ChicaConstants.CONCEPT_SUICIDE_CONCERNS, dateThreshold);
		
		//get abuse observations
		ruleNames = new ArrayList<String>();
		ruleNames.add(ChicaConstants.RULE_NAME_ABUSE_CONCERN_PWS);
		createAndSendNotifications(encounters, ruleNames, ABUSE_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT, ABUSE_CONCEPT, dateThreshold);
		
		//get domestic violence observations
		ruleNames = new ArrayList<String>();
		ruleNames.add(ChicaConstants.RULE_NAME_DOM_VIOL_PWS);
		createAndSendNotifications(encounters, ruleNames, DV_NOTIFICATION_TEXT, FROM_EMAIL, SUBJECT, DV_CONCEPT, dateThreshold);
		
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
	 * @param riskConceptName
	 * @param dateThreshold
	 */
	private void createAndSendNotifications(List<org.openmrs.Encounter> encounters, ArrayList<String> ruleNames,
	                                        String notificationText, String fromEmail, String subject,String riskConceptName,
	                                        Date dateThreshold) {
		LocationService locationService = Context.getLocationService();
		EncounterService encounterService = Context.getEncounterService();
		DssService dssService = Context.getService(DssService.class);
		ObsService obsService = Context.getObsService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context
				.getService(ChirdlUtilBackportsService.class);
		HashSet<Encounter> notificationSet = new HashSet<>();
		
		for (String ruleName : ruleNames) {
			Rule rule = dssService.getRule(ruleName);
			addEncounters(encounters, notificationSet, rule.getRuleId(), ruleName);
		}
		
		//send notification emails
		for (Encounter encounter : notificationSet) {
			Encounter chicaEncounter = encounterService.getEncounter(encounter.getEncounterId());
			
			EncounterAttributeValue attributeValue = chirdlutilbackportsService
					.getEncounterAttributeValueByName( encounter.getEncounterId(),ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);
			Integer locationId = chicaEncounter.getLocation().getLocationId();
			Date riskDate = null;
			String printerLocation =  null;	
			
			if (attributeValue != null) {
				printerLocation =  attributeValue.getValueText();	
			}
			
			if (printerLocation != null) {
				LocationTag locTag = locationService.getLocationTagByName(printerLocation.trim());
				if (locTag != null) {
					Integer locationTagId = locTag.getLocationTagId();
					
					List<Person> patientList = new ArrayList<>();
					patientList.add(encounter.getPatient());
					List<Concept> questionList = new ArrayList<>();
					ConceptService conceptService = Context.getConceptService();
					Concept riskConcept = conceptService.getConceptByName(riskConceptName);
					if (riskConcept != null) {
						questionList.add(riskConcept);
						List<String> sort = new ArrayList<>();
						sort.add("obsDatetime");
						
						List<Obs> riskObs = obsService.getObservations(patientList, null, questionList, null, null, null,
						    sort, 1, null, null, null, false);
						if (riskObs != null && !riskObs.isEmpty()) {
							Obs obs = riskObs.get(0);
							riskDate = obs.getObsDatetime();
						}
					}
					
					
					if (dateThreshold != null && riskDate != null) {
						if (riskDate.compareTo(dateThreshold) >= 0) {
							sendEmailNotification(locationId, locationTagId, chicaEncounter, notificationText, fromEmail,
							    subject, riskDate);
						}
					} else {
						sendEmailNotification(locationId, locationTagId, chicaEncounter, notificationText, fromEmail,
						    subject, riskDate);
					}
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
	 * @param riskDate
	 */
	private void sendEmailNotification(Integer locationId, Integer locationTagId, Encounter chicaEncounter, String riskText,
	                                   String fromEmail, String subject, Date riskDate) {
		ChirdlUtilBackportsService cub = Context.getService(ChirdlUtilBackportsService.class);
		LocationTagAttributeValue lav = cub.getLocationTagAttributeValue(locationTagId, ChirdlUtilConstants.LOC_TAG_ATTR_HIGH_RISK_CONTACT,
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
									log.error("No valid {} found for personId {}.  No one will be emailed.", ChirdlUtilConstants.PERSON_ATTRIBUTE_EMAIL, personId);
									return;
								}
								try {
									
									Patient patient = chicaEncounter.getPatient();
									
									String body = riskText
									        + "\n"
									        + "We do not have a record in CHICA of the patient's provider addressing this issue.\n"
									        + "We recommend checking the patient's medical record to make sure the issue was addressed.\n\n";
									
									String riskDateString = "unknown";
									
									if(riskDate != null){
										String pattern = "M/d/yyyy";
										SimpleDateFormat dateForm = new SimpleDateFormat(pattern);
										riskDateString = dateForm.format(riskDate);
									}
									
									body += "mrn: " + patient.getPatientIdentifier() + "\nname: " + patient.getGivenName()
									        + " " + patient.getFamilyName()+"\ndate risk factor identified: "+riskDateString;
									
							        Context.getMessageService().sendMessage(emailAddys, fromEmail, subject, body);
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
		ConceptService conceptService = Context.getConceptService();

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
				List<Person> patientList = new ArrayList<>();
				patientList.add(encounter.getPatient());
				List<String> sort = new ArrayList<>();
				sort.add("obsDatetime");
				
				//special processing for combined suicide/depression rule
				//limit notifications to only those with suicide
				if (ruleName.equals(ChicaConstants.RULE_NAME_DEPRESSION_SUICIDE_PWS)) {
					List<Concept> questionList = new ArrayList<>();
					Concept suicideConcept = conceptService.getConceptByName(ChicaConstants.CONCEPT_SUICIDE_CONCERNS);
					
					if (suicideConcept != null) {
						questionList.add(suicideConcept);
						
						List<Obs> suicideObs = obsService.getObservations(patientList, null, questionList, null, null, null,
						    sort, 1, null, null, null, false);
						if (suicideObs != null && !suicideObs.isEmpty()) {
							Obs obs = suicideObs.get(0);
							if (obs.getValueCoded() != null && obs.getValueCoded().getName().getName().equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE)) {
								notificationSet.add(encounter);
							}
						}
					}
					//special processing for dv rule
					//don't notify if Spanish is preferred language, Not_Safe_in_Relationship is stored,
					//and Kick_Hit_Slap is not present
					//The spanish translation of the "Safe in relationship" question is causing false positives
					//when that is the only PSF question that triggers the PWS
				} else if (ruleName.equals(ChicaConstants.RULE_NAME_DOM_VIOL_PWS)){
					boolean ignoreNotification = false;
					List<Concept> questionList = new ArrayList<>();
					Concept dvConcept = conceptService.getConceptByName("Domest_Violence_Concern");
					org.openmrs.Encounter dvEncounter = null;
							
					if (dvConcept != null) {
						questionList.add(dvConcept);
						
						boolean notSafeRelationship = false;
						boolean kickHitSlap = false;
						List<Obs> dvObs = obsService.getObservations(patientList, null, questionList, null, null, null,
						    sort, 2, null, null, null, false);
						if (dvObs != null && !dvObs.isEmpty()) {
							for(Obs obs:dvObs){
								if(obs.getValueCoded().getName().getName().equalsIgnoreCase("Not_Safe_in_Relationship")){
									notSafeRelationship = true;
									dvEncounter = obs.getEncounter();
								}else if(obs.getValueCoded().getName().getName().equalsIgnoreCase("Kick_Hit_Slap")){
									kickHitSlap = true;
								}
							}
							
							if(notSafeRelationship&&!kickHitSlap&&dvEncounter!= null){
								List<org.openmrs.Encounter> dvEncounters = new ArrayList<>();
								dvEncounters.add(dvEncounter);
								questionList = new ArrayList<>();
								Concept preferredLangConcept = conceptService.getConceptByName("preferred_language");
								questionList.add(preferredLangConcept);
								List<Obs> preferredLangObs = obsService.getObservations(null, dvEncounters, questionList, null,
								    null, null, sort, 1, null, null, null, false);
								if (preferredLangObs != null && !preferredLangObs.isEmpty()) {
									
									Obs obs = preferredLangObs.get(0);
									if(obs.getValueCoded().getName().getName().equalsIgnoreCase("spanish")){
										ignoreNotification = true;
									}
								}
							}
							
						}
					}
					if(!ignoreNotification){
						notificationSet.add(encounter);
					}
					
				}else{
					notificationSet.add(encounter);
				}
			}
		}
	}
}
