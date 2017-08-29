package org.openmrs.module.chica.study.dp3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
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
		Context.openSession();
		try
		{	
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
					adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));

			List<Patient> patients = Context.getPatientService().getPatients(firstName + ChirdlUtilConstants.GENERAL_INFO_COMMA + lastName, null, null, false);
			List<Patient> possibleMatches = new ArrayList<Patient>();
			if(patients != null && patients.size() > 0)
			{
				// Look through the list to find possible matches
				Date glookoDOB = DateUtil.parseDate(dateOfBirth, ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd);
				for(Patient patient : patients)
				{
					if(firstName.equalsIgnoreCase(patient.getPersonName().getGivenName()) && lastName.equalsIgnoreCase(patient.getPersonName().getFamilyName()) && patient.getBirthdate().compareTo(glookoDOB) == 0)
					{
						possibleMatches.add(patient);
					}


					// TODO CHICA-1063 Should we continue looking? One possible solution would
					// be to build the list of all possible matches, then look for a match based on 
					// patients that have an encounter for today. This would only work if the patient has
					// already been registered. The work flow for the device sync has yet to be determined.
					// The sync could happen prior to registration. Although very unlikely.
					//								if(possibleMatches.size() > 1)
					//								{
					//									// We have more than one possible match, no need to continue looking
					//									// We don't receive any further information from Glooko to match on
					//									break;
					//								}
				}
			}

			// Create person attribute to store GlookoId
			addGlookoCodePersonAttribute(possibleMatches.get(0)); // TODO CHICA-1063 Don't just use the first one in the list
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
	private void addGlookoCodePersonAttribute(Patient patient){
		
		PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByName("GlookoId"); // TODO CHICA-1063 Add constant CHANGE THIS TO GlookoCode

		if (attributeType == null)
		{
			// TODO CHICA-1063 log
			return;
		}
		
		PersonAttribute attr = new PersonAttribute(attributeType, glookoCode);
		attr.setDateCreated(new Date());
		attr.setCreator(Context.getAuthenticatedUser());
		patient.addAttribute(attr);
		
		Context.getPatientService().savePatient(patient);
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
