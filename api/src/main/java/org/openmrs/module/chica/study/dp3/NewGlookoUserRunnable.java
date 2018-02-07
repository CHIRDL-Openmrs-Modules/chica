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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

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
		Context.openSession();
		try
		{	
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
					adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));

			List<Patient> patients = Context.getPatientService().getPatients(firstName + ChirdlUtilConstants.GENERAL_INFO_COMMA + lastName, null, null, false);
			if(patients != null && patients.size() > 0)
			{
				List<Patient> possibleMatches = new ArrayList<Patient>();
				
				// Look through the list to find possible matches
				Date glookoDOB = DateUtil.parseDate(dateOfBirth, ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd);
				for(Patient patient : patients)
				{
					if(firstName.equalsIgnoreCase(patient.getPersonName().getGivenName()) && lastName.equalsIgnoreCase(patient.getPersonName().getFamilyName()) && patient.getBirthdate().compareTo(glookoDOB) == 0)
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
					List<Patient> matches = new ArrayList<Patient>();
					
					for(Patient patient : possibleMatches)
					{
						// Look for encounters for today for this patient
						Calendar todaysDate = Calendar.getInstance();
						todaysDate.set(Calendar.HOUR_OF_DAY, 0);
						todaysDate.set(Calendar.MINUTE, 0);
						todaysDate.set(Calendar.SECOND, 0);
						
						//List<Encounter> encounters = encounterService.getEncounters(patient, null, todaysDate.getTime(), null, null, null, null, null, null, false);
						
						EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setFromDate(todaysDate.getTime())
								.setIncludeVoided(false).createEncounterSearchCriteria();
						List<org.openmrs.Encounter> encounters = Context.getService(EncounterService.class).getEncounters(encounterSearchCriteria); 
							
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
					log.error("New Glooko user notification was received. Unable to locate patient match for: " + lastName + ", " + firstName);
				}
			}
			else
			{
				log.error("New Glooko user notification was received. Unable to locate possible patient matches for: " + lastName + ", " + firstName);
			}	
		}
		catch(ContextAuthenticationException e)
		{
			log.error("Error authenticating context.", e);
		}
		catch(Exception e)
		{
			log.error("Error in " + this.getClass().getName() + ".", e);
		}
		finally
		{
			Context.closeSession();
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
				log.error("Unable to create GlookoCode person attribute for patient: " + patient.getPatientId() + " Person attribute type does not exist.");
				return;
			}
			
			PersonAttribute attr = new PersonAttribute(attributeType, glookoCode);
			attr.setDateCreated(new Date());
			attr.setCreator(Context.getAuthenticatedUser());
			patient.addAttribute(attr);
			
			Context.getPatientService().savePatient(patient);
		}
		catch(APIException e)
		{
			log.error("Unable to create GlookoCode person attribute for patient: " + patient.getPatientId(), e);
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
		return this.getClass().getName() + " (GlookoCode: " + glookoCode + ")";
	}
}
