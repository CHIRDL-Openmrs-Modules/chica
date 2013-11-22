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

	private org.openmrs.Encounter encounter = null;

	public QueryImmunizationForecast(org.openmrs.Encounter encounter) {
		this.encounter = encounter;
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
		
		try {

			
			//encounter/session
			Integer encounterId = encounter.getEncounterId();
			List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounterId);
			Session session = sessions.get(0);
			Integer sessionId = session.getSessionId();
			
			//location
			Integer locationTagId = null;
			Integer locationId = null;
			org.openmrs.module.chica.hibernateBeans.Encounter chicaEncounter 
					= (org.openmrs.module.chica.hibernateBeans.Encounter) encounterService
					.getEncounter(encounter.getEncounterId());
			String printerLocation = chicaEncounter.getPrinterLocation();
			if (printerLocation != null)
			{
				Location location = encounter.getLocation();
				Set<LocationTag> tags = location.getTags();
				for(LocationTag tag:tags){
					if(printerLocation.equalsIgnoreCase(tag.getTag())){
						locationTagId = tag.getLocationTagId();
						locationId = location.getLocationId();
						break;
					}
				}
			}
			
			//patient state
			State queryImmunizationListState = chirdlutilbackportsService.getStateByName("Query Immunization Forecast");
			PatientState state = chirdlutilbackportsService.addPatientState(encounter.getPatient(), queryImmunizationListState, 
				sessionId, locationTagId,locationId);
		
			//send POST and create immunization list
			ImmunizationRegistryQuery.queryCHIRP(encounter);
					
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
				+ encounter.getEncounterId() + ")";
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
	public int getPriority() {
		return ChirdlRunnable.PRIORITY_FIVE;
	}
	
	
		
	
}
