package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class rentalConcerns implements Rule {
	
	private Log log = LogFactory.getLog(rentalConcerns.class);
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 365);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		List<Person> patientList = new ArrayList<Person>();
		patientList.add(patient);
		
		ConceptService conceptService = Context.getConceptService();
		Concept rentalStatusConcept = conceptService.getConceptByName("Rental_Status");
		if (rentalStatusConcept == null) {
			log.error("Rental_Status concept not identified");
			return Result.emptyResult();
		}
		
		Integer locationId = (Integer) parameters.get("locationId");
		Location location = Context.getLocationService().getLocation(locationId);
		List<Location> locationList = new ArrayList<Location>();
		locationList.add(location);
		List<Obs> notCleanSafeObs = new ArrayList<Obs>();
		List<Obs> systemsDontWorkObs = new ArrayList<Obs>();
		List<Concept> questionList = new ArrayList<Concept>();
		questionList.add(rentalStatusConcept);
		
		Concept notCleanSafeConcept = conceptService.getConceptByName("Not Clean & Safe");
		if (notCleanSafeConcept == null) {
			log.error("Not Clean & Safe concept not identified");
		} else {
			List<Concept> answerList = new ArrayList<Concept>();
			answerList.add(notCleanSafeConcept);
			notCleanSafeObs = Context.getObsService().getObservations(patientList, null, questionList, answerList, null, 
				locationList, null, null, null, startDate, endDate, false);
		}
		
		Concept systemsDontWorkConcept = conceptService.getConceptByName("Systems Do Not Work");
		if (systemsDontWorkConcept == null) {
			log.error("Systems Do Not Work concept not identified");
		} else {
			List<Concept> answerList = new ArrayList<Concept>();
			answerList.add(systemsDontWorkConcept);
			systemsDontWorkObs = Context.getObsService().getObservations(patientList, null, questionList, answerList, null, 
				locationList, null, null, null, startDate, endDate, false);
		}
		
		if (notCleanSafeObs.size() == 0 && systemsDontWorkObs.size() == 0) {
			return Result.emptyResult();
		}
		
		String text = null;
		if (notCleanSafeObs.size() > 0) {
			text = "it's not clean and safe";
		}
		
		if (systemsDontWorkObs.size() > 0) {
			if (text == null) {
				text = "the systems do not work";
			} else {
				text += ", and the systems do not work";
			}
		}
		
		text += ".";
		return new Result(text);
	}
}
