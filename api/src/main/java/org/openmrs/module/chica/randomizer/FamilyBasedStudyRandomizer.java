/**
 * 
 */
package org.openmrs.module.chica.randomizer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.service.ChicaService;

/**
 * @author Tammy Dugan
 * 
 */
public class FamilyBasedStudyRandomizer extends BasicRandomizer implements
		Randomizer
{
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void randomize(Study study, Patient patient, Encounter encounter)
	{
		// first see if the patient should be randomized
		Date studyStartDate = study.getStartDate();
		Calendar encounterDate = Calendar.getInstance();
		encounterDate.setTime(encounter.getEncounterDatetime());
		Calendar today = Calendar.getInstance();
		EncounterService encounterService = Context.getEncounterService();

		// check that the encounter date is the same day as today
		if (today.get(Calendar.YEAR) == encounterDate.get(Calendar.YEAR)
				&& today.get(Calendar.MONTH) == encounterDate
						.get(Calendar.MONTH)
				&& today.get(Calendar.DAY_OF_YEAR) == encounterDate
						.get(Calendar.DAY_OF_YEAR))
		{

			// make sure the patients has no previous visits before the study
			// start date
			List<Encounter> encounters = encounterService
					.getEncountersByPatient(patient); // CHICA-1151 replace getEncounters() with getEncountersByPatient()

			for (Encounter lookupEncounter : encounters)
			{
				if (lookupEncounter.getEncounterDatetime().compareTo(
						studyStartDate) < 0)
				{
					return;
				}
			}

			Integer patientId = patient.getPatientId();

			ChicaService chicaService = Context
					.getService(ChicaService.class);
			PatientFamily patientFamily = chicaService
					.getPatientFamily(patientId);

			// the patient family link already exists
			if (patientFamily != null)
			{
				return;
			}

			PersonAddress fullAddress = patient.getPersonAddress();
			String address = fullAddress.getAddress1();
			String state = fullAddress.getStateProvince();
			String city = fullAddress.getCityVillage();
			String phoneNum = null;
			PersonAttribute patientAttribute = patient
					.getAttribute("Telephone Number");
			if (patientAttribute != null)
			{
				phoneNum = patientAttribute.getValue();
			}

			Family family = chicaService.getFamilyByAddress(address);
			if (family != null)
			{
				randomizeToFamily(family.getFamilyId(), study, patient, encounter);
				Integer numKids = family.getNumKids();
				family.setNumKids(++numKids);
				chicaService.updateFamily(family);
				patientFamily = new PatientFamily();
				patientFamily.setCreationTime(new Date());
				patientFamily.setFamilyId(family.getFamilyId());
				patientFamily.setPatientId(patientId);
				patientFamily.setPhoneNum(phoneNum);
				patientFamily.setStreetAddress(address);
				chicaService.savePatientFamily(patientFamily);
			} else if (!(phoneNum != null && phoneNum.contains("(999)999-9999")))
			{
				family = chicaService.getFamilyByPhone(phoneNum);
				if (family != null)
				{
					randomizeToFamily(family.getFamilyId(), study, patient, encounter);
					Integer numKids = family.getNumKids();
					family.setNumKids(++numKids);
					chicaService.updateFamily(family);
					patientFamily = new PatientFamily();
					patientFamily.setCreationTime(new Date());
					patientFamily.setFamilyId(family.getFamilyId());
					patientFamily.setPatientId(patientId);
					patientFamily.setPhoneNum(phoneNum);
					patientFamily.setStreetAddress(address);
					patientFamily.setFlag("PHONE_MATCH");
					chicaService.savePatientFamily(patientFamily);
				} else
				{
					if (address != null)
					{
						family = new Family();
						family.setCity(city);
						family.setCreationTime(new Date());
						family.setNumKids(1);
						family.setPhoneNum(phoneNum);
						family.setState(state);
						family.setStreetAddress(address);
						chicaService.saveFamily(family);
						patientFamily = new PatientFamily();
						patientFamily.setCreationTime(new Date());
						patientFamily.setFamilyId(family.getFamilyId());
						patientFamily.setPatientId(patientId);
						patientFamily.setPhoneNum(phoneNum);
						patientFamily.setStreetAddress(address);
						chicaService.savePatientFamily(patientFamily);
						super.randomize(study, patient, encounter);
					}
				}
			}
		}
	}
	
	private void randomizeToFamily(Integer familyId, Study study, Patient patient, Encounter encounter){
		ChicaService chicaService = Context.getService(ChicaService.class);
		ConceptService conceptService = Context.getConceptService();
		Integer studyConceptId = study.getStudyConceptId();
		Concept studyConcept = conceptService.getConcept(studyConceptId);
		Obs obs = chicaService.getStudyArmObs(familyId, studyConcept);
		
		if(obs == null){
			log.error("An error occurred while trying to randomize patient: "+patient.getPatientId()+
					" to family: "+familyId+". There where no obs found for the family id");
		}else{
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
				Concept randomAnswer = obs.getValueCoded();
				saveStudyPersonAttribute(patient, studyConcept, randomAnswer);
				saveStudyObs(patient, studyConcept, randomAnswer,encounter);
			}
		}
	}
}
