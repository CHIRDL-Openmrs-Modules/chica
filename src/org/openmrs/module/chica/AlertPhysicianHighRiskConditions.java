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
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

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
		List<Encounter> encounters = encounterService.getEncounters(null, null, startDate, endDate, null, null, null, false);
		ObsService obsService = Context.getObsService();
		ConceptService conceptService = Context.getConceptService();
		HashSet<Encounter> notificationSet = new HashSet<Encounter>();
		
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
		
		//TODO construct and send email
		
		Context.closeSession();
		
	}
	
	private static void addEncounters(List<Obs> obs, HashSet<Encounter> notificationSet) {
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		for (Obs currOb : obs) {
			Encounter encounter = currOb.getEncounter();
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
			} else {
				//if the PWS was not submitted, add the encounter to the notification list
				notificationSet.add(encounter);
			}
			
		}
		
	}
	
}
