package org.openmrs.module.chica.study.dp3;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * CHICA-1063
 * @author Dave Ely
 */
public class DeviceSyncRunnable implements ChirdlRunnable
{
	private Log log = LogFactory.getLog(this.getClass());
	private String glookoCode;
	private String syncTimestamp;
	private String dataType; // TODO CHICA-1063 Store this in an encounter attribute
	
	/**
	 * @param glookoCode
	 * @param syncTimestamp
	 * @param dataType
	 */
	public DeviceSyncRunnable(String glookoCode, String syncTimestamp, String dataType)
	{
		this.glookoCode = glookoCode;
		this.syncTimestamp = syncTimestamp;
		this.dataType = dataType;
	}
	
	/**
	 * This thread does the following
	 * Looks up the patient using the glookoCode
	 * Looks up the patient's encounter for the day
	 * Stores the dataType in an encounter attribute
	 * Creates a "QUERY GLOOKO" patient state
	 * Runs and ends the state
	 */
	public void run() 
	{
		Context.openSession();
		try
		{	
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
					adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
			
			// Look up the patient using the glookoCode
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class); 
			PersonAttribute personAttribute = chirdlutilbackportsService.getPersonAttributeByValue("GlookoId", glookoCode); // TODO CHICA-1063 Add constant CHANGE THIS TO GlookoCode
			if(personAttribute != null)
			{
				Patient patient = Context.getPatientService().getPatient(personAttribute.getPerson().getId());
				if(patient != null)
				{
					// Find the patient's encounter
					Calendar todaysDate = Calendar.getInstance();
					todaysDate.set(Calendar.HOUR_OF_DAY, 0);
					todaysDate.set(Calendar.MINUTE, 0);
					todaysDate.set(Calendar.SECOND, 0);
					
					List<Encounter> encounters = Context.getService(EncounterService.class).getEncounters(patient, null, todaysDate.getTime(), null, null, null, null, null, null, false);
					
					if(encounters != null && encounters.size() > 0)
					{
						// Use the most recent encounter since the device sync happened right now, 
						// we should be working off of the most recent encounter for the day
						org.openmrs.module.chica.hibernateBeans.Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter)encounters.get(0);
						
						List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(chicaEncounter.getEncounterId());
						if(sessions != null && sessions.size() > 0)
						{
							Location location = chicaEncounter.getLocation();
							if (location != null) 
							{
								Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(chicaEncounter);

								// Create patient state. Once the state has been created, CHICA will query Glooko for device data
								State state = chirdlutilbackportsService.getStateByName("QUERY GLOOKO"); // TODO CHICA-1063 Add constant
								
								// We could check to see if any open states exist without an end time. 
								// However, lets just create a new state in case new data has actually been 
								// upload between sync notifications					
								chirdlutilbackportsService.addPatientState(patient, state, sessions.get(0).getSessionId(), locationTagId, location.getLocationId(), null);									
							}
							else
							{
								log.error("Device sync error. Unable to determine patient location from encounter: " + chicaEncounter.getEncounterId());
							}
						}
						else
						{
							log.error("Device sync error. Unable to get session for encounter: " + + chicaEncounter.getEncounterId());
						}
					}
					else
					{
						log.error("Device sync was received, but unable to locate recent encounter for patient with GlookoCode: " + glookoCode);
					}
				}
				else
				{
					log.error("Device sync was received, but unable to locate patient with person attribute for GlookoCode: " + glookoCode);
				}
			}
			else
			{
				log.error("Device sync was received, but unable to person attribute for GlookoCode: " + glookoCode);
			}
			
		}
		catch(ContextAuthenticationException e)
		{
			log.error("Error authenticating context in " + this.getClass().getName() + ".", e);
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
