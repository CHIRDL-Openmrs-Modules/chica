package org.openmrs.module.chica.study.dp3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;

/**
 * CHICA-1063
 * @author Dave Ely
 */
public class NewGlookoUserRunnable implements ChirdlRunnable
{
	private Log log = LogFactory.getLog(this.getClass());
	private String firstName;
	private String lastName; 
	private String dateOfBirth; 
	private String glookoCode; 
	
	/**
	 * @param firstName
	 * @param lastName
	 * @param dateOfBirth
	 * @param glookoCode
	 */
	public NewGlookoUserRunnable(String firstName, String lastName, String dateOfBirth, String glookoCode)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.glookoCode = glookoCode;
	}
	
	/**
	 * This thread does the following
	 * Attempts to match the patient using first name, last name, and date of birth
	 * If a match is found, the GlookoCode person attribute is created
	 */    
	@Override
	public void run() 
	{
		try
		{	
			List<Patient> patients = Context.getPatientService().getPatients(this.firstName + ChirdlUtilConstants.GENERAL_INFO_COMMA + this.lastName, null, null, false);
			if(patients != null && patients.size() > 0)
			{
				List<Patient> possibleMatches = new ArrayList<>();
				
				// Look through the list to find possible matches
				Date glookoDOB = DateUtil.parseDate(this.dateOfBirth, ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd);
				for(Patient patient : patients)
				{
					if(this.firstName.equalsIgnoreCase(patient.getPersonName().getGivenName()) && this.lastName.equalsIgnoreCase(patient.getPersonName().getFamilyName()) && patient.getBirthdate().compareTo(glookoDOB) == 0)
					{
						possibleMatches.add(patient);
					}	
				}
					
				Patient matchedPatient = null;
				if(possibleMatches.size() == 1)
				{
					matchedPatient = possibleMatches.get(0);
				}
				else if(possibleMatches.size() > 1)
				{
					// More than one possible match. Try to match by locating a patient that has an encounter for the day
					EncounterService encounterService = Context.getEncounterService();
					List<Patient> matches = new ArrayList<>();
					
					for(Patient patient : possibleMatches)
					{
						// Look for encounters for today for this patient
						Calendar todaysDate = Calendar.getInstance();
						todaysDate.set(Calendar.HOUR_OF_DAY, 0);
						todaysDate.set(Calendar.MINUTE, 0);
						todaysDate.set(Calendar.SECOND, 0);
						
						List<Encounter> encounters = encounterService.getEncounters(patient, null, todaysDate.getTime(), null, null, null, null, null, null, false);
						
						if(encounters.size() > 0)
						{
							// Found an encounter for the day, add to the list of matches
							matches.add(patient);
						}
					}
					
					if(matches.size() == 1)
					{
						matchedPatient = matches.get(0);
					}
				}
				
				if(matchedPatient != null)
				{
					// Create person attribute to store GlookoId
					addGlookoCodePersonAttribute(matchedPatient);
				}
				else
				{
					this.log.error("New Glooko user notification was received. Unable to locate patient match for: " + this.lastName + ", " + this.firstName);
				}
			}
			else
			{
				this.log.error("New Glooko user notification was received. Unable to locate possible patient matches for: " + this.lastName + ", " + this.firstName);
			}	
		}
		catch(ContextAuthenticationException e)
		{
			this.log.error("Error authenticating context.", e);
		}
		catch(Exception e)
		{
			this.log.error("Error in " + this.getClass().getName() + ".", e);
		}		
	}
	
	/**
	 * Add the GlookoCode person attribute
	 * @param patient
	 */
	private void addGlookoCodePersonAttribute(Patient patient)
	{
		try
		{
			PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByName(ChirdlUtilConstants.PERSON_ATTRIBUTE_GLOOKO_CODE);

			if (attributeType == null)
			{
				this.log.error("Unable to create GlookoCode person attribute for patient: " + patient.getPatientId() + " Person attribute type does not exist.");
				return;
			}
			
			PersonAttribute attr = new PersonAttribute(attributeType, this.glookoCode);
			attr.setDateCreated(new Date());
			attr.setCreator(Context.getAuthenticatedUser());
			patient.addAttribute(attr);
			
			Context.getPatientService().savePatient(patient);
		}
		catch(APIException e)
		{
			this.log.error("Unable to create GlookoCode person attribute for patient: " + patient.getPatientId(), e);
		}	
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
	@Override
	public int getPriority() 
	{
		 return ChirdlRunnable.PRIORITY_THREE;
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
	@Override
	public String getName() 
	{
		return this.getClass().getName() + " (GlookoCode: " + this.glookoCode + ")";
	}
}
