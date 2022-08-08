/**
 * 
 */
package org.openmrs.module.chica.randomizer;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.chica.hibernateBeans.Study;

/**
 * @author Tammy Dugan
 * 
 */
public class DobGtSentinelDateK22Randomizer extends BasicRandomizer implements Randomizer
{
	@Override
	public void randomize(Study study, Patient patient,Encounter encounter)
	{
		//first see if the patient should be randomized
		Date studyStartDate = study.getStartDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(studyStartDate);
		calendar.add(Calendar.MONTH, -9);
		Date indexDate = calendar.getTime();
		Date dob = patient.getBirthdate();
		
		if(dob.compareTo(indexDate)>=0){
			super.randomize(study, patient, encounter);
		}
	}
}
