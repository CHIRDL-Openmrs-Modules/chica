/**
 * 
 */
package org.openmrs.module.chica.randomizer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Study;

/**
 * @author Tammy Dugan
 * 
 */
public class NewToClinicSinceStudyStartRandomizer extends BasicRandomizer implements Randomizer
{
	@Override
	public void randomize(Study study, Patient patient, Encounter encounter)
	{
		//first see if the patient should be randomized
		
		Date studyStartDate = study.getStartDate();
		Calendar encounterDate = Calendar.getInstance();
		encounterDate.setTime(encounter.getEncounterDatetime());
		Calendar today = Calendar.getInstance();
		EncounterService encounterService = Context.getEncounterService();
		
		//check that the encounter date is the same day as today
		if(today.get(Calendar.YEAR)== encounterDate.get(Calendar.YEAR)&&
			today.get(Calendar.MONTH)== encounterDate.get(Calendar.MONTH)&&
			today.get(Calendar.DAY_OF_YEAR)== encounterDate.get(Calendar.DAY_OF_YEAR)){
			
			//make sure the patients has no previous visits before the study start date
			List<Encounter> encounters = encounterService.getEncountersByPatient(patient); // CHICA-1151 replace getEncounters() with getEncountersByPatient()
			
			for(Encounter lookupEncounter: encounters){
				if(lookupEncounter.getEncounterDatetime().compareTo(studyStartDate)<0){
					return;
				}
			}
			
			super.randomize(study, patient, encounter);
			
		}
		
		
	}
}
