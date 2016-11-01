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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Tammy Dugan
 */

public class AlertPhysicianHighRiskConditions extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static int NUM_DAYS = 3;//number of days allowed to submit PWS
	
	@Override
	public void execute() {
		Context.openSession();
		
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
		ObsService obsService = Context.getObsService();
		ConceptService conceptService = Context.getConceptService();
		HashSet<org.openmrs.Encounter> notificationSet = new HashSet<org.openmrs.Encounter>();
		
		//get suicide observations
		List<Concept> questions = new ArrayList<Concept>();
		questions.add(conceptService.getConceptByName("suicide_concerns"));
		List<Concept> answers = new ArrayList<Concept>();
		answers.add(conceptService.getConceptByName("True"));
		
		List<Obs> obs = obsService.getObservations(null, encounters, questions, answers, null, null, null, null, null, null,
		    null, false);
		
		addEncounters(obs, notificationSet);
		
		//get abuse observations
		questions = new ArrayList<Concept>();
		questions.add(conceptService.getConceptByName("Abuse_Concern"));
		answers = new ArrayList<Concept>();
		answers.add(conceptService.getConceptByName("yes_parent"));
		
		obs = obsService.getObservations(null, encounters, questions, answers, null, null, null, null, null, null, null,
		    false);
		
		addEncounters(obs, notificationSet);
		
		ChirdlUtilBackportsService cub = Context.getService(ChirdlUtilBackportsService.class);
		
		for (org.openmrs.Encounter encounter : notificationSet) {
			Encounter chicaEncounter = (Encounter) encounterService.getEncounter(encounter.getEncounterId());
			Integer locationId = chicaEncounter.getLocation().getLocationId();
			String printerLocation = chicaEncounter.getPrinterLocation();
			if (printerLocation != null) {
				LocationTagAttribute locTagAttr = cub.getLocationTagAttribute(printerLocation.trim());
				if (locTagAttr != null) {
					Integer locationTagId = locTagAttr.getLocationTagAttributeId();
					sendEmailNotification(locationId, locationTagId);
				}
			}
		}
		
		Context.closeSession();
		
	}
	
	private static void sendEmailNotification(Integer locationId, Integer locationTagId) {
		ChirdlUtilBackportsService cub = Context.getService(ChirdlUtilBackportsService.class);
		LocationTagAttributeValue lav = cub.getLocationTagAttributeValue(locationTagId, "HighRiskContact", locationId);
		PersonService personService = Context.getPersonService();
		if (lav != null) {
			String highRiskPersonIdStr = lav.getValue();
			if (highRiskPersonIdStr != null) {
				try {
					Integer personId = Integer.parseInt(highRiskPersonIdStr);
					Person person = personService.getPerson(personId);
					if (person != null) {
						PersonAttribute personAttribute = person.getAttribute("email");
						if (personAttribute != null) {
							String email = personAttribute.getValue();
							String lastName = person.getPersonName().getFamilyName();
							String firstName = person.getPersonName().getGivenName();
							//TODO construct email
						}
					}
				}
				catch (Exception e) {}
			}
		}
	}
	
	private static void addEncounters(List<Obs> obs, HashSet<org.openmrs.Encounter> notificationSet) {
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		for (Obs currOb : obs) {
			org.openmrs.Encounter encounter = currOb.getEncounter();
			Integer encounterId = encounter.getEncounterId();
			Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
			Form form = Context.getFormService().getForm("PWS");
			Integer formId = form.getFormId();
			State endState = backportsService.getStateByName("PWS_process");
			Integer endStateId = endState.getStateId();
			Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapEnd, encounterId, endStateId, true);
			
			boolean containsEndState = formIdToPatientStateMapEnd.containsKey(formId);
			
			if (containsEndState) {
				//if the PWS was submitted, see if any checkboxes on prompt
				//TODO
			} else {
				//if the PWS was not submitted, add the encounter to the notification list
				notificationSet.add(encounter);
			}
			
		}
		
	}
	
}
