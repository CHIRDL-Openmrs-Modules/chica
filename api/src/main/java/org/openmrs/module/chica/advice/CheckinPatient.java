/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * 
 */
public class CheckinPatient implements ChirdlRunnable
{
	private static final Logger log = LoggerFactory.getLogger(CheckinPatient.class);
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
		log.info("Started execution of {} ({}, {})", getName(), Thread.currentThread().getName(), new Timestamp(new Date().getTime()));
		
		try
		{
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			
			EncounterService encounterService = Context.getEncounterService();
			Encounter encounter = encounterService.getEncounter(this.encounterId);
					
			if(encounter == null){
				return;
			}

			Patient patient = encounter.getPatient();

			//The session is unique because only 1 session exists at checkin
			List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounter.getEncounterId());
			Session session = sessions.get(0);
			
			Integer sessionId = session.getSessionId();
			
			Integer locationTagId = null;
			Integer locationId = null;
			String printerLocation =  null;	
			
			// lookup location tag id by printer location
			EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService
					.getEncounterAttributeValueByName(encounter.getEncounterId(),ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);	
			
			if (encounterAttributeValue != null) {
				printerLocation =  encounterAttributeValue.getValueText();	
				
			} else {
				log.error("Encounter attribute value not found. EncounterAttribute: {} Encounter id: {}.", ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION, this.encounterId );
			}
						
			if (printerLocation != null){
				Location location = encounter.getLocation();
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
			log.error("Exception checking in patient. Encounter id: {}", this.encounterId, e);
		}finally{
			log.info("Finished execution of {} ({}, {})",  getName(), Thread.currentThread().getName(), new Timestamp(new Date().getTime()) );
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
