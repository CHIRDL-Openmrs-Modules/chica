/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * 
 */
public class CheckinPatient implements ChirdlRunnable
{
	private Log log = LogFactory.getLog(this.getClass());
	private Integer encounterId = null; // Use encounterId instead of an encounter object to prevent lazy initialization errors

	public CheckinPatient(Integer encounterId)
	{
		this.encounterId = encounterId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		this.log.info("Started execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
			new Timestamp(new Date().getTime()) + ")");
		
		try
		{
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			
			org.openmrs.module.chica.service.EncounterService encounterService = Context
								        .getService(org.openmrs.module.chica.service.EncounterService.class);
			Encounter chicaEncounter = (Encounter) encounterService.getEncounter(this.encounterId);
			
			if(chicaEncounter == null){
				return;
			}
								
			Patient patient = chicaEncounter.getPatient();

			//The session is unique because only 1 session exists at checkin
			List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(chicaEncounter.getEncounterId());
			Session session = sessions.get(0);
			
			Integer sessionId = session.getSessionId();
			
			Integer locationTagId = null;
			Integer locationId = null;
			
			// lookup location tag id by printer location
			String printerLocation = chicaEncounter.getPrinterLocation();
			if (printerLocation != null)
			{
				Location location = chicaEncounter.getLocation();
				Set<LocationTag> tags = location.getTags();
				for(LocationTag tag:tags){
					if(printerLocation.equalsIgnoreCase(tag.getName())){ // CHICA-1151 replaced getTag() with getName()
						locationTagId = tag.getLocationTagId();
						locationId = location.getLocationId();
						break;
					}
				}
			}
		
			Program program = chirdlutilbackportsService.getProgram(locationTagId,locationId);
			StateManager.changeState(patient, sessionId, null,
					program,null,
					locationTagId,locationId,BaseStateActionHandler.getInstance());
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}finally{
			this.log.info("Finished execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
				new Timestamp(new Date().getTime()) + ")");
		}
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    @Override
	public String getName() {
	    return "Checkin Patient (Encounter: " + this.encounterId + ")";
    }

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
    @Override
	public int getPriority() {
	    return ChirdlRunnable.PRIORITY_ONE;
    }
}
