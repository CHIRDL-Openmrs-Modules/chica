/**
 * 
 */
package org.openmrs.module.chica.randomizer;

import java.util.Collection;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.util.Util;

/**
 * @author Tammy Dugan
 * 
 */
public class BasicRandomizer implements Randomizer
{
	public void randomize(Study study, Patient patient, Encounter encounter)
	{
		ConceptService conceptService = Context.getConceptService();
		Integer studyConceptId = study.getStudyConceptId();
		Concept studyConcept = conceptService.getConcept(studyConceptId);
		PersonService personService = Context.getPersonService();

		// make sure the patient gets randomized to a given study once
		String studyConceptName = studyConcept.getName().getName();
		String studyConceptIdString = String.valueOf(studyConcept
				.getConceptId());
		String studyAttributeName = studyConceptName + " ("
				+ studyConceptIdString + ")";
		PersonAttributeType studyConceptIdAttr = personService
				.getPersonAttributeTypeByName(studyAttributeName);
		PersonAttribute studyAttribute = null;
		if (studyConceptIdAttr != null)
		{
			studyAttribute = patient.getAttribute(studyConceptIdAttr
					.getPersonAttributeTypeId());
		}

		ObsService obsService = Context.getObsService();
		List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, studyConcept);
		
		//make sure the patient doesn't have a person attribute or obs stored for the study
		if (studyAttribute == null&&(obsList==null||obsList.size()==0))
		{
			// get the possible categories for the study
			Collection<ConceptAnswer> answers = studyConcept.getAnswers();
			ConceptAnswer[] answerArray = new ConceptAnswer[answers.size()];
			answers.toArray(answerArray);
			// randomize to a study category
			int randomNum = Util.GENERATOR.nextInt(answers.size());
			ConceptAnswer randomAnswer = answerArray[randomNum];

			saveStudyPersonAttribute(patient, studyConcept, randomAnswer.getAnswerConcept());
			saveStudyObs(patient, studyConcept, randomAnswer.getAnswerConcept(),encounter);
		}

	}

	private PersonAttributeType getStudyPersonAttributeType(Concept studyConcept)
	{
		String studyConceptName = studyConcept.getName().getName();
		String studyConceptIdString = String.valueOf(studyConcept
				.getConceptId());
		String studyAttributeName = studyConceptName + " ("
				+ studyConceptIdString + ")";
		PersonService personService = Context.getPersonService();
		PersonAttributeType studyConceptIdAttr = personService
				.getPersonAttributeTypeByName(studyAttributeName);

		if (studyConceptIdAttr == null)
		{
			studyConceptIdAttr = new PersonAttributeType();
			studyConceptIdAttr.setName(studyAttributeName);
			studyConceptIdAttr.setDescription(studyAttributeName);
			studyConceptIdAttr.setFormat("java.lang.String");
			personService.savePersonAttributeType(studyConceptIdAttr);
		}
		return studyConceptIdAttr;
	}

	protected void saveStudyPersonAttribute(Patient patient,
			Concept studyConcept, Concept randomAnswer)
	{
		PersonAttributeType studyConceptIdAttr = getStudyPersonAttributeType(studyConcept);
		String randomAnswerName = randomAnswer.getName()
				.getName();
		String randomAnswerId = String.valueOf(randomAnswer.getConceptId());
		String studyAnswerName = randomAnswerName + " (" + randomAnswerId + ")";
		PatientService patientService = Context.getPatientService();
		PersonAttribute attribute = new PersonAttribute();
		attribute.setAttributeType(studyConceptIdAttr);
		attribute.setValue(studyAnswerName);
		patient.addAttribute(attribute);
		patientService.savePatient(patient);
	}

	protected void saveStudyObs(Patient patient, Concept studyConcept,
			Concept randomAnswer,  Encounter encounter)
	{
		// save an obs for the study category
		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		obs.setConcept(studyConcept);
		obs.setPerson(patient);
		obs.setValueCoded(randomAnswer);
		obs.setObsDatetime(new java.util.Date());
		obs.setLocation(encounter.getLocation());
		obs.setEncounter(encounter);
		obsService.saveObs(obs, null);
	}
}
