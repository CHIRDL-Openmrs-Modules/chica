/**
 * 
 */
package org.openmrs.module.chica;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Vibha Anand
 * 
 */

public class MergePatients extends AbstractTask
{
	private Log log = LogFactory.getLog(this.getClass());
//test comment
	
	private Date lastRunDate;
	private Date thresholdDate;
	private int retireProvidersPriorToDays = -1; // in days
	private boolean runTask = false;
	private Integer max;


	@Override
	public void initialize(TaskDefinition config)
	{
		super.initialize(config);
		Context.openSession();		
		init();
		Context.closeSession();
		
	}

	@Override
	public void execute()
	{
		Context.openSession();
		PatientService patientService = Context.getPatientService();
		
		
		try
		{
			
			String run = this.taskDefinition.getProperty("run");
			String limit = this.taskDefinition.getProperty("limit");
			try {
				runTask = Boolean.valueOf(run);
				max = Integer.valueOf(limit);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				shutdown();
			}

			if (runTask){
				PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
				List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
				identifierTypes.add(identifierType);
				int count = 0;
				//test
				List<Patient> patients = patientService.getPatients(null, "", identifierTypes, false);
				while (count < max){
					Patient patient = patients.get(count);
					PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
					String mrn = patientIdentifier.getIdentifier();
					List<Patient> duplicatePatients = patientService.getPatients(null, Util.removeLeadingZeros(mrn), identifierTypes, true);
					if (duplicatePatients == null || duplicatePatients.size() == 0){
						continue;
					}
					Patient nonLeadingZeroPatient = duplicatePatients.get(0);

					patientService.mergePatients(nonLeadingZeroPatient, patient);
					count++;
				}
				
			
			}
			
			/*if(retireProvidersPriorToDays == -1)
			{
				// Moved here from init() because of openSession and closeSession errors with rule token fetch 
				
				
			}
			
			UserService userService = Context.getUserService();
			EncounterService encounterService = Context.getEncounterService();
					
			List<Role> roles = new Vector<Role>();
			roles.add(userService.getRole("Provider"));
			
			List<User> doctors = userService.getUsers(null, roles, false);	// return non-voided providers
			
			lastRunDate = GregorianCalendar.getInstance().getTime();
			
			Calendar threshold = GregorianCalendar.getInstance();
			threshold.add(Calendar.DAY_OF_MONTH, retireProvidersPriorToDays);   
			thresholdDate = threshold.getTime();
			
			//List<Encounter> encounters = encounterService.getEncounters(null, null, thresholdDate, lastRunDate, null, null, false);
			
			List<Encounter> encounters = encounterService.getEncounters(null, null, thresholdDate, lastRunDate, null, null, doctors, false);
			if(encounters.size() == 0)
			{
				return;	// no encounters yet for any unvoided providers
			}
			boolean active;
			
			for(User doctor: doctors)
			{
				active = false;
				if(doctor.getUsername().compareToIgnoreCase(".Other.") != 0)
				{
					for (Encounter e: encounters)
					{
						List<User> providers = userService.getUsersByPerson(e.getProvider(), true);
						User provider = null;
						if(providers != null&& providers.size()>0){
							provider = providers.get(0);
						}
						if(provider.getUserId().equals(doctor.getUserId())) 
						{
							active = true;
							break;			// provider is active in this time period
						}
					}
					if(!active && !encounters.isEmpty() )
					{
						userService.retireUser(doctor, "Inactive in the clinic");  // retires states
					}
				}
				
			}
			log.info("CHICA Providers were retired last on: " + lastRunDate.toString());
			*/
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		} finally
		{
			Context.closeSession();
		}
	}
	
	private void init()
	{
		
	}
	
	@Override
	public void shutdown()
	{
		super.shutdown();
		

	}

}
