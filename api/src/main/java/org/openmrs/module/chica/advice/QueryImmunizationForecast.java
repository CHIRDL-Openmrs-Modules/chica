/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.QueryImmunizationsException;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.immunization.ImmunizationRegistryQuery;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan This thread class runs the immunization forecasting service
 *         query
 */
public class QueryImmunizationForecast implements ChirdlRunnable {

	private Log log = LogFactory.getLog(this.getClass());

	private QueryImmunizationsException exception = null;
	
	private Integer encounterId = null; // Use encounterId instead of an encounter object to prevent lazy initialization errors

	public QueryImmunizationForecast(Integer encounterId) {
		this.encounterId = encounterId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		
		AdministrationService adminService = Context
			.getAdministrationService();
		Context.authenticate(adminService
				.getGlobalProperty("scheduler.username"), adminService
				.getGlobalProperty("scheduler.password"));
		ChirdlUtilBackportsService chirdlutilbackportsService = Context
			.getService(ChirdlUtilBackportsService.class);
		EncounterService encounterService = Context
			.getService(org.openmrs.module.chica.service.EncounterService.class);
		Encounter chicaEncounter = (Encounter) encounterService.getEncounter(encounterId);
		
		try {
			//encounter/session
			List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounterId);
			Session session = sessions.get(0);
			Integer sessionId = session.getSessionId();
			
			//location
			Integer locationTagId = null;
			Integer locationId = null;
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
			
			//patient state
			State queryImmunizationListState = chirdlutilbackportsService.getStateByName("Query Immunization Forecast");
			PatientState state = chirdlutilbackportsService.addPatientState(chicaEncounter.getPatient(), queryImmunizationListState, 
				sessionId, locationTagId, locationId, null);
		
			//send POST and create immunization list
			ImmunizationRegistryQuery.queryCHIRP(chicaEncounter);
					
			state.setEndTime(new java.util.Date());
			chirdlutilbackportsService.updatePatientState(state);
			

		} catch (Exception e) {
			log.error("Immunization Query exception: ", e);
			
		} finally {
			Context.closeSession();
		}
	}

	
	public QueryImmunizationsException getException() {
		return this.exception;
	}

	/**
	 * 
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
	public String getName() {
		return "Query Immunization Forecast (Encounter: "
				+ encounterId + ")";
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
	public int getPriority() {
		return ChirdlRunnable.PRIORITY_FIVE;
	}
	
	
		
	
}
