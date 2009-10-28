/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.dss.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Vibha Anand
 * 
 */

public class ChicaProvidersCron extends AbstractTask
{
	private Log log = LogFactory.getLog(this.getClass());

	
	private Date startDate;
	private Date lastRunDate;
	private Date thresholdDate;
	private TaskDefinition taskConfig;
	private int retireProvidersPriorToDays = -1; // in days

	@Override
	public void initialize(TaskDefinition config)
	{
		Context.openSession();
		if (Context.isAuthenticated() == false)
			authenticate();
		
		this.taskConfig = config;
		init();
		Context.closeSession();
	}

	@Override
	public void execute()
	{
		Context.openSession();
		
		try
		{
			
			if (Context.isAuthenticated() == false)
				authenticate();
			if(retireProvidersPriorToDays == -1)
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
				if(doctor.getUsername().compareToIgnoreCase(".other") != 0)
				{
					for (Encounter e: encounters)
					{
						if(e.getProvider().getUserId().equals(doctor.getUserId())) 
						{
							active = true;
							break;			// provider is active in this time period
						}
					}
					if(!active && !encounters.isEmpty() )
					{
						userService.voidUser(doctor, "Inactive in the clinic");  // retires states
					}
				}
				else
				{
					//this.log.info("CAN NOT VOID .OTHER PROVIDER");
				}
				
			}
			log.info("CHICA Providers were retired last on: " + lastRunDate.toString());
			
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
		this.log.info("Initializing Cron job for retiring inactive providers...");
		
		
		try
		{
			startDate = GregorianCalendar.getInstance().getTime();
			AdministrationService adminService = Context.getAdministrationService();
			// configurable time in days before today's date and time, convert it to a negative number
			retireProvidersPriorToDays = -Integer.parseInt(adminService.getGlobalProperty("chica.retireProvidersPeriod"));  // in days
			
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
		this.log.info("Finished initializing Cron job for retiring inactive providers.");
	}
	
	@Override
	public void shutdown()
	{
		super.shutdown();
		

	}

}
